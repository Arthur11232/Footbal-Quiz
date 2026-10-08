import CryptoKit
import Foundation

struct LocalProgressRecord {
  let snapshot: CloudProgressSnapshot
  let fingerprint: String
  let encodedData: Data
}

@MainActor
final class LocalProgressStore: ObservableObject {
  @Published private(set) var record: LocalProgressRecord?
  @Published private(set) var errorMessage: String?

  private let fileManager: FileManager

  init(fileManager: FileManager = .default) {
    self.fileManager = fileManager
  }

  func load(userId: String) {
    errorMessage = nil
    do {
      record = try readRecord(userId: userId)
    } catch {
      record = nil
      errorMessage = "Local progress could not be read: \(error.localizedDescription)"
    }
  }

  @discardableResult
  func restore(userId: String, payload: CloudUserStatsPayload) -> Bool {
    errorMessage = nil
    guard let encodedData = payload.encodedData else {
      errorMessage = "This cloud document contains values that cannot be stored safely by this iOS version."
      return false
    }

    do {
      let destination = try recordURL(userId: userId, createDirectory: true)
      try encodedData.write(to: destination, options: [.atomic, .completeFileProtectionUntilFirstUserAuthentication])
      record = LocalProgressRecord(
        snapshot: payload.snapshot,
        fingerprint: Self.fingerprint(encodedData),
        encodedData: encodedData
      )
      return true
    } catch {
      errorMessage = "Cloud progress was not restored locally: \(error.localizedDescription)"
      return false
    }
  }

  func clearInMemory() {
    record = nil
    errorMessage = nil
  }

  @discardableResult
  func replaceAfterConfirmedUpload(userId: String, encodedData: Data) -> Bool {
    errorMessage = nil
    do {
      let object = try JSONSerialization.jsonObject(with: encodedData)
      guard let dictionary = object as? [String: Any] else {
        throw StoreError.invalidRoot
      }
      let snapshot = try Self.snapshot(from: dictionary)
      let destination = try recordURL(userId: userId, createDirectory: true)
      try encodedData.write(to: destination, options: [.atomic, .completeFileProtectionUntilFirstUserAuthentication])
      record = LocalProgressRecord(
        snapshot: snapshot,
        fingerprint: Self.fingerprint(encodedData),
        encodedData: encodedData
      )
      return true
    } catch {
      errorMessage = "Cloud upload succeeded, but the updated local snapshot could not be saved: \(error.localizedDescription)"
      return false
    }
  }

  private func readRecord(userId: String) throws -> LocalProgressRecord? {
    let url = try recordURL(userId: userId, createDirectory: false)
    guard fileManager.fileExists(atPath: url.path) else { return nil }

    let data = try Data(contentsOf: url)
    let object = try JSONSerialization.jsonObject(with: data)
    guard let dictionary = object as? [String: Any] else {
      throw StoreError.invalidRoot
    }
    let snapshot = try Self.snapshot(from: dictionary)
    return LocalProgressRecord(
      snapshot: snapshot,
      fingerprint: Self.fingerprint(data),
      encodedData: data
    )
  }

  private func recordURL(userId: String, createDirectory: Bool) throws -> URL {
    guard !userId.isEmpty else { throw StoreError.emptyUserId }
    guard let appSupport = fileManager.urls(for: .applicationSupportDirectory, in: .userDomainMask).first else {
      throw StoreError.applicationSupportUnavailable
    }
    let directory = appSupport.appendingPathComponent("FootballQuiz/Progress", isDirectory: true)
    if createDirectory {
      try fileManager.createDirectory(at: directory, withIntermediateDirectories: true)
    }
    return directory.appendingPathComponent(Self.fingerprint(Data(userId.utf8)) + ".json")
  }

  private static func snapshot(from data: [String: Any]) throws -> CloudProgressSnapshot {
    guard let gameState = data["gameState"] as? [String: Any] else {
      throw StoreError.unsupportedSchema
    }
    let progression = data["progression"] as? [String: Any]
    let stages = progression?["completedStages"] as? [[String: Any]] ?? []
    let stageIds = stages.compactMap { ($0["stageId"] as? String)?.nonBlankValue }
    let answeredKeys = [
      "top5_answered", "ufa_answered", "world_answered", "vsRM_answered", "vsRB_answered",
    ]
    let totalAnswered = answeredKeys.reduce(0) { $0 + intValue(gameState[$1]) }
    let totalScore = intValue(gameState["total"])
    let hasProgress = totalScore > 0 || totalAnswered > 0

    return CloudProgressSnapshot(
      hasProgress: hasProgress,
      totalScore: totalScore,
      totalAnswered: totalAnswered,
      completedStageIds: stageIds,
      lastSyncedMillis: int64Value(gameState["lastSynced"]),
      isFromCache: false,
      schemaNotice: nil
    )
  }

  private static func intValue(_ value: Any?) -> Int {
    (value as? NSNumber)?.intValue ?? value as? Int ?? 0
  }

  private static func int64Value(_ value: Any?) -> Int64? {
    (value as? NSNumber)?.int64Value ?? value as? Int64
  }

  private static func fingerprint(_ data: Data) -> String {
    SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
  }

  private enum StoreError: LocalizedError {
    case applicationSupportUnavailable
    case emptyUserId
    case invalidRoot
    case unsupportedSchema

    var errorDescription: String? {
      switch self {
      case .applicationSupportUnavailable:
        return "Application Support is unavailable."
      case .emptyUserId:
        return "The account UID is missing."
      case .invalidRoot, .unsupportedSchema:
        return "The saved document uses an unsupported schema."
      }
    }
  }
}

private extension String {
  var nonBlankValue: String? {
    trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : self
  }
}
