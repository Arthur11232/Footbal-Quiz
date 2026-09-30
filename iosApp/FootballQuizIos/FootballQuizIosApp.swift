import FirebaseCore
import GoogleSignIn
import SwiftUI

@main
struct FootballQuizIosApp: App {
  init() {
    guard Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil else {
      fatalError("Missing GoogleService-Info.plist in the iOS app bundle")
    }
    FirebaseApp.configure()
  }

  var body: some Scene {
    WindowGroup {
      ContentView()
        .onOpenURL { url in
          _ = GIDSignIn.sharedInstance.handle(url)
        }
    }
  }
}
