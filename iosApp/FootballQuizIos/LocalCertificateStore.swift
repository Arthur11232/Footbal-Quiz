import CryptoKit
import Foundation

@MainActor
final class LocalCertificateStore: ObservableObject {
  @Published private(set) var certificates: [CloudCertificate] = []
  @Published private(set) var errorMessage: String?
  @Published private(set) var mergeNotice: String?

  private let fileManager: FileManager

  init(fileManager: FileManager = .default) {
    self.fileManager = fileManager
  }

  func load(userId: String) {
    errorMessage = nil
    mergeNotice = nil
    do {
      let url = try recordURL(userId: userId, createDirectory: false)
      guard fileManager.fileExists(atPath: url.path) else {
        certificates = []
        return
      }
      let data = try Data(contentsOf: url)
      certificates = Self.normalized(try JSONDecoder().decode([CloudCertificate].self, from: data))
    } catch {
      certificates = []
      errorMessage = "Local certificates could not be read: \(error.localizedDescription)"
    }
  }

  func mergeCloud(_ remote: [CloudCertificate], userId: String) {
    errorMessage = nil
    mergeNotice = nil
    guard !remote.isEmpty else { return }

    var byStage = Dictionary(uniqueKeysWithValues: Self.normalized(certificates).map { ($0.stageId, $0) })
    var added = 0
    var conflictingStages = 0

    for cloudCertificate in Self.normalized(remote) {
      guard let localCertificate = byStage[cloudCertificate.stageId] else {
        byStage[cloudCertificate.stageId] = cloudCertificate
        added += 1
        continue
      }
      guard localCertificate.certificateId != cloudCertificate.certificateId else { continue }

      conflictingStages += 1
      if cloudCertificate.issuedAtMillis < localCertificate.issuedAtMillis {
        byStage[cloudCertificate.stageId] = cloudCertificate
      }
    }

    let merged = Self.sorted(Array(byStage.values))
    guard merged != certificates else {
      if conflictingStages > 0 {
        mergeNotice = "A certificate ID conflict was detected for \(conflictingStages) stage(s); the earliest issued certificate was preserved."
      }
      return
    }

    do {
      let data = try JSONEncoder.sorted.encode(merged)
      let destination = try recordURL(userId: userId, createDirectory: true)
      try data.write(to: destination, options: [.atomic, .completeFileProtectionUntilFirstUserAuthentication])
      certificates = merged
      if conflictingStages > 0 {
        mergeNotice = "Saved \(added) cloud certificate(s). For \(conflictingStages) conflicting stage(s), the earliest issued certificate was preserved."
      } else if added > 0 {
        mergeNotice = "Saved \(added) cloud certificate(s) locally."
      }
    } catch {
      errorMessage = "Certificates were read from cloud but could not be saved locally: \(error.localizedDescription)"
    }
  }

  func clearInMemory() {
    certificates = []
    errorMessage = nil
    mergeNotice = nil
  }

  private func recordURL(userId: String, createDirectory: Bool) throws -> URL {
    guard !userId.isEmpty else { throw StoreError.emptyUserId }
    guard let appSupport = fileManager.urls(for: .applicationSupportDirectory, in: .userDomainMask).first else {
      throw StoreError.applicationSupportUnavailable
    }
    let directory = appSupport.appendingPathComponent("FootballQuiz/Certificates", isDirectory: true)
    if createDirectory {
      try fileManager.createDirectory(at: directory, withIntermediateDirectories: true)
    }
    return directory.appendingPathComponent(Self.fingerprint(Data(userId.utf8)) + ".json")
  }

  private static func normalized(_ values: [CloudCertificate]) -> [CloudCertificate] {
    var byStage: [String: CloudCertificate] = [:]
    for certificate in values where !certificate.stageId.isEmpty && !certificate.certificateId.isEmpty {
      guard let existing = byStage[certificate.stageId] else {
        byStage[certificate.stageId] = certificate
        continue
      }
      if existing.certificateId == certificate.certificateId {
        if certificate.issuedAtMillis < existing.issuedAtMillis {
          byStage[certificate.stageId] = certificate
        }
      } else if certificate.issuedAtMillis < existing.issuedAtMillis {
        byStage[certificate.stageId] = certificate
      }
    }
    return sorted(Array(byStage.values))
  }

  private static func sorted(_ values: [CloudCertificate]) -> [CloudCertificate] {
    values.sorted {
      if $0.issuedAtMillis == $1.issuedAtMillis { return $0.stageId < $1.stageId }
      return $0.issuedAtMillis > $1.issuedAtMillis
    }
  }

  private static func fingerprint(_ data: Data) -> String {
    SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
  }

  private enum StoreError: LocalizedError {
    case applicationSupportUnavailable
    case emptyUserId

    var errorDescription: String? {
      switch self {
      case .applicationSupportUnavailable:
        return "Application Support is unavailable."
      case .emptyUserId:
        return "The account UID is missing."
      }
    }
  }
}

private extension JSONEncoder {
  static var sorted: JSONEncoder {
    let encoder = JSONEncoder()
    encoder.outputFormatting = [.sortedKeys]
    return encoder
  }
}
