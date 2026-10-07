import CryptoKit
import FirebaseCore
import Foundation

struct CloudBackupRecord {
  let url: URL
  let createdAtMillis: Int64
  let cloudFingerprint: String?
  let certificatesFingerprint: String
}

@MainActor
final class CloudBackupStore: ObservableObject {
  @Published private(set) var latest: CloudBackupRecord?
  @Published private(set) var errorMessage: String?

  private let fileManager: FileManager

  init(fileManager: FileManager = .default) {
    self.fileManager = fileManager
  }

  func loadLatest(userId: String) {
    errorMessage = nil
    do {
      let directory = try backupDirectory(userId: userId, create: false)
      guard fileManager.fileExists(atPath: directory.path) else {
        latest = nil
        return
      }
      let urls = try fileManager.contentsOfDirectory(
        at: directory,
        includingPropertiesForKeys: [.contentModificationDateKey],
        options: [.skipsHiddenFiles]
      )
      let candidates = urls.filter { $0.pathExtension == "json" }.sorted { $0.lastPathComponent > $1.lastPathComponent }
      latest = try candidates.compactMap(Self.readRecord).first
    } catch {
      latest = nil
      errorMessage = "Cloud backup metadata could not be read: \(error.localizedDescription)"
    }
  }

  @discardableResult
  func create(
    userId: String,
    stats: CloudUserStatsPayload?,
    certificates: [CloudCertificate]
  ) -> Bool {
    errorMessage = nil
    do {
      let createdAtMillis = Int64(Date().timeIntervalSince1970 * 1_000)
      let certificatesData = try Self.certificatesData(certificates)
      let envelope = BackupEnvelope(
        schemaVersion: 1,
        createdAtMillis: createdAtMillis,
        firebaseProjectId: FirebaseApp.app()?.options.projectID ?? "unknown",
        bundleIdentifier: Bundle.main.bundleIdentifier ?? "unknown",
        userIdHash: Self.fingerprint(Data(userId.utf8)),
        cloudFingerprint: stats?.fingerprint,
        certificatesFingerprint: Self.fingerprint(certificatesData),
        statsJSONBase64: stats?.encodedData?.base64EncodedString(),
        certificates: Self.sorted(certificates)
      )
      let encoded = try JSONEncoder.backup.encode(envelope)
      let directory = try backupDirectory(userId: userId, create: true)
      let destination = directory.appendingPathComponent("cloud-backup-\(createdAtMillis).json")
      try encoded.write(to: destination, options: [.atomic, .completeFileProtectionUntilFirstUserAuthentication])
      latest = CloudBackupRecord(
        url: destination,
        createdAtMillis: createdAtMillis,
        cloudFingerprint: envelope.cloudFingerprint,
        certificatesFingerprint: envelope.certificatesFingerprint
      )
      return true
    } catch {
      errorMessage = "Pre-write cloud backup could not be created: \(error.localizedDescription)"
      return false
    }
  }

  func matchesCurrent(stats: CloudUserStatsPayload?, certificates: [CloudCertificate]) -> Bool {
    guard let latest else { return false }
    guard latest.cloudFingerprint == stats?.fingerprint else { return false }
    guard let data = try? Self.certificatesData(certificates) else { return false }
    return latest.certificatesFingerprint == Self.fingerprint(data)
  }

  func clearInMemory() {
    latest = nil
    errorMessage = nil
  }

  private func backupDirectory(userId: String, create: Bool) throws -> URL {
    guard !userId.isEmpty else { throw BackupError.emptyUserId }
    guard let appSupport = fileManager.urls(for: .applicationSupportDirectory, in: .userDomainMask).first else {
      throw BackupError.applicationSupportUnavailable
    }
    let directory = appSupport
      .appendingPathComponent("FootballQuiz/CloudBackups", isDirectory: true)
      .appendingPathComponent(Self.fingerprint(Data(userId.utf8)), isDirectory: true)
    if create {
      try fileManager.createDirectory(at: directory, withIntermediateDirectories: true)
    }
    return directory
  }

  private static func readRecord(_ url: URL) throws -> CloudBackupRecord? {
    let envelope = try JSONDecoder().decode(BackupEnvelope.self, from: Data(contentsOf: url))
    guard envelope.schemaVersion == 1 else { return nil }
    return CloudBackupRecord(
      url: url,
      createdAtMillis: envelope.createdAtMillis,
      cloudFingerprint: envelope.cloudFingerprint,
      certificatesFingerprint: envelope.certificatesFingerprint
    )
  }

  private static func certificatesData(_ certificates: [CloudCertificate]) throws -> Data {
    try JSONEncoder.backup.encode(sorted(certificates))
  }

  private static func sorted(_ certificates: [CloudCertificate]) -> [CloudCertificate] {
    certificates.sorted {
      if $0.stageId == $1.stageId { return $0.certificateId < $1.certificateId }
      return $0.stageId < $1.stageId
    }
  }

  private static func fingerprint(_ data: Data) -> String {
    SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
  }

  private struct BackupEnvelope: Codable {
    let schemaVersion: Int
    let createdAtMillis: Int64
    let firebaseProjectId: String
    let bundleIdentifier: String
    let userIdHash: String
    let cloudFingerprint: String?
    let certificatesFingerprint: String
    let statsJSONBase64: String?
    let certificates: [CloudCertificate]
  }

  private enum BackupError: LocalizedError {
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
  static var backup: JSONEncoder {
    let encoder = JSONEncoder()
    encoder.outputFormatting = [.prettyPrinted, .sortedKeys]
    return encoder
  }
}
