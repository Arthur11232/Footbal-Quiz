import CryptoKit
import FirebaseAuth
import FirebaseCore
import FirebaseFirestore
import Foundation

struct ConfirmedCloudUpload {
  let encodedData: Data
}

@MainActor
final class ControlledCloudWriter: ObservableObject {
  enum State {
    case idle
    case writing
    case succeeded
    case failed(String)
  }

  @Published private(set) var state: State = .idle

  nonisolated static var isAllowedBuild: Bool {
    #if DEBUG
      Bundle.main.bundleIdentifier == "com.arthuralexandryan.footballquiz.dev"
        && FirebaseApp.app()?.options.projectID == "football-quiz-dev"
    #else
      false
    #endif
  }

  private let firestore: Firestore

  init(firestore: Firestore = Firestore.firestore()) {
    self.firestore = firestore
  }

  func uploadStats(
    userId: String,
    localData: Data,
    expectedCloudExists: Bool,
    expectedCloudFingerprint: String?
  ) async -> ConfirmedCloudUpload? {
    state = .writing

    do {
      guard Self.isAllowedBuild else { throw UploadError.devBuildRequired }
      guard Auth.auth().currentUser?.uid == userId else { throw UploadError.accountChanged }
      if expectedCloudExists, expectedCloudFingerprint == nil {
        throw UploadError.unverifiableBackup
      }

      let encodedData = try Self.prepareLocalData(localData, userId: userId)
      guard let payload = try JSONSerialization.jsonObject(with: encodedData) as? [String: Any] else {
        throw UploadError.unsupportedLocalSchema
      }

      let reference = firestore.collection("users").document(userId)
      _ = try await firestore.runTransaction { transaction, errorPointer in
        do {
          let current = try transaction.getDocument(reference)
          let currentFingerprint = current.data().flatMap(Self.fingerprint)
          guard
            current.exists == expectedCloudExists,
            currentFingerprint == expectedCloudFingerprint
          else {
            errorPointer?.pointee = UploadError.cloudChanged as NSError
            return nil
          }

          transaction.setData(payload, forDocument: reference, merge: true)
        } catch {
          errorPointer?.pointee = error as NSError
        }
        return nil
      }

      state = .succeeded
      return ConfirmedCloudUpload(encodedData: encodedData)
    } catch {
      state = .failed(Self.message(for: error))
      return nil
    }
  }

  func reset() {
    state = .idle
  }

  nonisolated private static func prepareLocalData(_ data: Data, userId: String) throws -> Data {
    let object = try JSONSerialization.jsonObject(with: data)
    guard var root = object as? [String: Any], var gameState = root["gameState"] as? [String: Any] else {
      throw UploadError.unsupportedLocalSchema
    }
    if let storedUserId = (root["regId"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines),
       !storedUserId.isEmpty,
       storedUserId != userId {
      throw UploadError.localAccountMismatch
    }

    root["regId"] = userId
    gameState["lastSynced"] = Int64(Date().timeIntervalSince1970 * 1_000)
    root["gameState"] = gameState
    guard JSONSerialization.isValidJSONObject(root) else {
      throw UploadError.unsupportedLocalSchema
    }
    return try JSONSerialization.data(withJSONObject: root, options: [.sortedKeys])
  }

  nonisolated private static func fingerprint(_ dictionary: [String: Any]) -> String? {
    guard let data = try? JSONSerialization.data(withJSONObject: dictionary, options: [.sortedKeys]) else {
      return nil
    }
    return SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
  }

  private static func message(for error: Error) -> String {
    let nsError = error as NSError
    if nsError.domain == UploadError.errorDomain,
       let uploadError = UploadError(rawValue: nsError.code) {
      return uploadError.errorDescription ?? "Cloud upload was stopped."
    }
    if nsError.domain == FirestoreErrorDomain {
      switch FirestoreErrorCode.Code(rawValue: nsError.code) {
      case .permissionDenied:
        return "Firestore denied the write. No local or cloud data was changed."
      case .unavailable:
        return "Cloud upload requires an online server connection. Nothing was changed."
      default:
        break
      }
    }
    return "Cloud upload failed before confirmation: \(error.localizedDescription)"
  }

  private enum UploadError: Int, LocalizedError, CustomNSError {
    case devBuildRequired = 1
    case accountChanged
    case unverifiableBackup
    case unsupportedLocalSchema
    case localAccountMismatch
    case cloudChanged

    static var errorDomain: String { "FootballQuizIos.ControlledCloudWriter" }
    var errorCode: Int { rawValue }
    var errorUserInfo: [String: Any] { [NSLocalizedDescriptionKey: errorDescription ?? "Cloud upload was stopped."] }

    var errorDescription: String? {
      switch self {
      case .devBuildRequired:
        return "Cloud upload is available only in the Debug app connected to the Firebase Dev project."
      case .accountChanged:
        return "The signed-in Firebase account changed. Sign in again before uploading."
      case .unverifiableBackup:
        return "The existing cloud document has no verifiable backup fingerprint. Upload was blocked."
      case .unsupportedLocalSchema:
        return "The local snapshot uses an unsupported schema. Upload was blocked."
      case .localAccountMismatch:
        return "The local snapshot belongs to a different Firebase UID. Upload was blocked."
      case .cloudChanged:
        return "Cloud data changed after the backup. Read it again and create a fresh backup before retrying."
      }
    }
  }
}
