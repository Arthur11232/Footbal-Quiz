import CryptoKit
import FirebaseFirestore
import Foundation

struct CloudProgressSnapshot {
  let hasProgress: Bool
  let totalScore: Int
  let totalAnswered: Int
  let completedStageIds: [String]
  let lastSyncedMillis: Int64?
  let isFromCache: Bool
  let schemaNotice: String?
}

struct CloudUserStatsPayload {
  let snapshot: CloudProgressSnapshot
  let encodedData: Data?
  let fingerprint: String?
}

struct CloudCertificate: Codable, Equatable, Identifiable {
  let id: String
  let stageId: String
  let certificateId: String
  let issuedAtMillis: Int64
  let recipientName: String
  let source: String
}

@MainActor
final class CloudProgressReader: ObservableObject {
  enum LoadState {
    case idle
    case loading
    case loaded(
      stats: CloudUserStatsPayload?,
      certificates: [CloudCertificate],
      invalidCertificateCount: Int,
      certificatesFromCache: Bool
    )
    case failed(String)
  }

  @Published private(set) var state: LoadState = .idle

  private let firestore: Firestore
  private var activeUserId: String?

  init(firestore: Firestore = Firestore.firestore()) {
    self.firestore = firestore
  }

  func load(userId: String) async {
    guard !userId.isEmpty else {
      clear()
      return
    }

    activeUserId = userId
    state = .loading

    do {
      async let statsDocument = fetchStats(userId: userId)
      async let certificatesQuery = fetchCertificates(userId: userId)
      let (statsResult, certificatesResult) = try await (statsDocument, certificatesQuery)

      guard activeUserId == userId else { return }
      let parsedCertificates = Self.parseCertificates(certificatesResult)
      state = .loaded(
        stats: Self.parseStats(statsResult),
        certificates: parsedCertificates.items,
        invalidCertificateCount: parsedCertificates.invalidCount,
        certificatesFromCache: certificatesResult.metadata.isFromCache
      )
    } catch {
      guard activeUserId == userId else { return }
      state = .failed(Self.message(for: error))
    }
  }

  func clear() {
    activeUserId = nil
    state = .idle
  }

  private func fetchStats(userId: String) async throws -> DocumentSnapshot {
    try await withCheckedThrowingContinuation { continuation in
      firestore.collection("users").document(userId).getDocument { snapshot, error in
        if let error {
          continuation.resume(throwing: error)
        } else if let snapshot {
          continuation.resume(returning: snapshot)
        } else {
          continuation.resume(throwing: ReaderError.emptyResponse)
        }
      }
    }
  }

  private func fetchCertificates(userId: String) async throws -> QuerySnapshot {
    try await withCheckedThrowingContinuation { continuation in
      firestore.collection("users").document(userId).collection("certificates").getDocuments {
        snapshot, error in
        if let error {
          continuation.resume(throwing: error)
        } else if let snapshot {
          continuation.resume(returning: snapshot)
        } else {
          continuation.resume(throwing: ReaderError.emptyResponse)
        }
      }
    }
  }

  private static func parseStats(_ document: DocumentSnapshot) -> CloudUserStatsPayload? {
    guard document.exists, let data = document.data() else { return nil }

    let gameState = data["gameState"] as? [String: Any]
    let progression = data["progression"] as? [String: Any]
    let completedStages = progression?["completedStages"] as? [[String: Any]] ?? []
    let completedStageIds = completedStages.compactMap { stage in
      (stage["stageId"] as? String)?.nilIfBlank
    }

    let answeredKeys = [
      "top5_answered", "ufa_answered", "world_answered", "vsRM_answered", "vsRB_answered",
    ]
    let totalAnswered = answeredKeys.reduce(0) { total, key in
      total + intValue(gameState?[key])
    }
    let totalScore = intValue(gameState?["total"])
    let knownTopLevelFields: Set<String> = ["email", "gameState", "name", "regId", "progression"]
    let unknownFields = Set(data.keys).subtracting(knownTopLevelFields).sorted()
    var schemaNotices: [String] = []
    if gameState == nil {
      schemaNotices.append("gameState is missing or has an unsupported shape")
    }
    if progression == nil {
      schemaNotices.append("progression is missing or has an unsupported shape")
    }
    if !unknownFields.isEmpty {
      schemaNotices.append("unknown top-level fields were ignored: \(unknownFields.joined(separator: ", "))")
    }

    let encodedData = try? JSONSerialization.data(withJSONObject: data, options: [.sortedKeys])
    return CloudUserStatsPayload(
      snapshot: CloudProgressSnapshot(
        hasProgress: totalScore > 0 || totalAnswered > 0,
        totalScore: totalScore,
        totalAnswered: totalAnswered,
        completedStageIds: completedStageIds,
        lastSyncedMillis: int64Value(gameState?["lastSynced"]),
        isFromCache: document.metadata.isFromCache,
        schemaNotice: schemaNotices.isEmpty ? nil : schemaNotices.joined(separator: "; ") + "."
      ),
      encodedData: encodedData,
      fingerprint: encodedData.map { data in
        SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
      }
    )
  }

  private static func parseCertificates(_ query: QuerySnapshot) -> (items: [CloudCertificate], invalidCount: Int) {
    var invalidCount = 0
    let items = query.documents.compactMap { document -> CloudCertificate? in
      let data = document.data()
      let stageId = (data["stageId"] as? String)?.nilIfBlank ?? document.documentID
      guard
        !stageId.isEmpty,
        let certificateId = (data["certificateId"] as? String)?.nilIfBlank,
        let issuedAtMillis = int64Value(data["issuedAt"]),
        issuedAtMillis > 0
      else {
        invalidCount += 1
        return nil
      }

      return CloudCertificate(
        id: document.documentID,
        stageId: stageId,
        certificateId: certificateId,
        issuedAtMillis: issuedAtMillis,
        recipientName: data["recipientName"] as? String ?? "",
        source: data["source"] as? String ?? "unknown"
      )
    }
    .sorted { $0.issuedAtMillis > $1.issuedAtMillis }
    return (items, invalidCount)
  }

  private static func intValue(_ value: Any?) -> Int {
    (value as? NSNumber)?.intValue ?? value as? Int ?? 0
  }

  private static func int64Value(_ value: Any?) -> Int64? {
    (value as? NSNumber)?.int64Value ?? value as? Int64
  }

  private static func message(for error: Error) -> String {
    let nsError = error as NSError
    if nsError.domain == FirestoreErrorDomain {
      switch FirestoreErrorCode.Code(rawValue: nsError.code) {
      case .permissionDenied:
        return "Firestore denied access. Check that the signed-in UID is allowed to read its own user document."
      case .unavailable:
        return "Cloud data is unavailable. Check the connection and try again; no local data was changed."
      default:
        break
      }
    }
    return "Cloud data could not be read: \(error.localizedDescription)"
  }

  private enum ReaderError: Error {
    case emptyResponse
  }
}

private extension String {
  var nilIfBlank: String? {
    trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : self
  }
}
