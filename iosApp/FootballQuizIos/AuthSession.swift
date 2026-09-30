import FirebaseAuth
import FirebaseCore
import FootballQuizShared
import GoogleSignIn
import SwiftUI
import UIKit

@MainActor
final class AuthSession: ObservableObject {
  @Published private(set) var account: AuthenticatedUser?
  @Published private(set) var isBusy = false
  @Published private(set) var errorMessage: String?

  private var authListener: AuthStateDidChangeListenerHandle?

  init() {
    authListener = Auth.auth().addStateDidChangeListener { [weak self] _, user in
      Task { @MainActor in
        self?.account = user.map {
          AuthenticatedUser(
            id: $0.uid,
            email: $0.email,
            displayName: $0.displayName,
            photoUrl: $0.photoURL?.absoluteString
          )
        }
      }
    }
  }

  deinit {
    if let authListener {
      Auth.auth().removeStateDidChangeListener(authListener)
    }
  }

  func signIn() async {
    guard !isBusy else { return }
    errorMessage = nil

    guard let clientID = FirebaseApp.app()?.options.clientID else {
      errorMessage = "Firebase client ID is missing. Check GoogleService-Info.plist."
      return
    }
    guard let presenter = Self.activePresenter() else {
      errorMessage = "Could not open Google Sign-In. Please try again."
      return
    }

    isBusy = true
    defer { isBusy = false }
    GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)

    do {
      let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: presenter)
      guard let idToken = result.user.idToken?.tokenString else {
        GIDSignIn.sharedInstance.signOut()
        errorMessage = "Google did not return an ID token. Please try again."
        return
      }
      let credential = GoogleAuthProvider.credential(
        withIDToken: idToken,
        accessToken: result.user.accessToken.tokenString
      )
      do {
        try await Auth.auth().signIn(with: credential)
      } catch {
        GIDSignIn.sharedInstance.signOut()
        errorMessage = "Firebase sign-in failed: \(error.localizedDescription)"
      }
    } catch {
      let signInError = error as NSError
      if signInError.domain == kGIDSignInErrorDomain,
        signInError.code == -5  // kGIDSignInErrorCodeCanceled in GoogleSignIn
      {
        return
      }
      errorMessage = "Google Sign-In did not finish: \(error.localizedDescription)"
    }
  }

  func signOut() {
    guard !isBusy else { return }
    errorMessage = nil
    do {
      try Auth.auth().signOut()
      GIDSignIn.sharedInstance.signOut()
    } catch {
      errorMessage = "Sign-out failed: \(error.localizedDescription)"
    }
  }

  private static func activePresenter() -> UIViewController? {
    let scene = UIApplication.shared.connectedScenes
      .compactMap { $0 as? UIWindowScene }
      .first { $0.activationState == .foregroundActive }
    var presenter = scene?.windows.first { $0.isKeyWindow }?.rootViewController
    while let presented = presenter?.presentedViewController {
      presenter = presented
    }
    return presenter
  }
}
