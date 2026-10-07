# Football Quiz iOS shell

This is an early SwiftUI integration checkpoint, not the full iOS game. It supports Google/Firebase sign-in, uses the shared progression/sync decision models, and has a separate Firestore snapshot screen. Stage progress remains demo data and is never treated as local account state. A signed-in user can copy a compatible cloud snapshot into protected local iOS storage; no iOS code uploads or otherwise writes Firestore data yet.

The iOS target has two local Firebase configurations:

- Debug: `com.arthuralexandryan.footballquiz.dev`, displayed as **Football Quiz Dev**, reads `iosApp/Firebase/Dev/GoogleService-Info.plist`.
- Release: `com.arthuralexandryan.footballquiz`, displayed as **Football Quiz**, reads `iosApp/Firebase/Prod/GoogleService-Info.plist`.

Both Apple apps currently belong to Firebase project `footbal-quiz-1f62a`. This separates bundle/client configuration, but it does **not** isolate Auth users or Firestore data into a different backend. The build phase copies only the selected plist into the app and fails when its `BUNDLE_ID` does not match the target. Both files are intentionally ignored by Git, like Android's `google-services.json`. Firebase Auth, Firestore, and Google Sign-In are installed with Swift Package Manager; `Package.resolved` is versioned for reproducible builds.

## Run in Simulator

1. Open `iosApp/FootballQuizIos.xcodeproj` in Xcode.
2. Select the `FootballQuizIos` scheme and an Apple Silicon iPhone Simulator.
3. Press Run. The build phase runs `:shared:embedAndSignAppleFrameworkForXcode` automatically.
4. Open **Inspect shared engine**. France 39/40 must be **In progress** and 40/40 must be **Complete**. The screen also lists all 16 shared stage IDs.
5. Open **Preview stage progress**. France should show **Achieved**, Germany **Complete**, and Italy **To play**. The remaining stages are also **To play**. This is demo data only; no certificate can be issued from this screen.
6. In **Account checkpoint**, tap **Sign in** and choose the same Google account used on Android. Compare the displayed Firebase UID with that account's UID in Firebase Console > Authentication > Users. Restart the app to check session persistence, then tap **Sign out**. Cancelling the account picker should not show an error.
7. While signed in, open **Read cloud snapshot**. It reads `users/{uid}` and `users/{uid}/certificates/{stageId}`, shows whether the result came from the server or offline cache, and handles missing data, permission errors, unknown top-level fields, and account changes.
8. The sync card uses the shared KMP decision matrix. Cloud progress can be copied atomically into UID-scoped, file-protected local iOS storage. **Keep Local** never uploads or overwrites cloud data and is remembered only for the exact local/cloud snapshot pair. `UPLOAD_LOCAL` remains locked until a test-user backup and a safe write environment are confirmed.
9. Valid cloud certificates are merged independently into UID-scoped local storage. A logout, empty cloud response, or progress restore does not erase them. The screen displays cloud and saved-local lists separately; it never issues or uploads certificates.

The current KMP targets are `iosArm64` and `iosSimulatorArm64`; an Intel (`x86_64`) Simulator is not configured. The project excludes that simulator architecture. Xcode 27.0 and iOS Simulator 26.5 were used for the initial build and launch check.

The Debug/dev and Release/prod configurations were both verified in Simulator builds on 2026-10-01: each `.app` contains only its selected Firebase plist, and bundle ID plus URL scheme match that configuration. Google sign-in, session persistence, sign-out, and repeat sign-in with the same iOS UID were confirmed on a real iPhone. Matching the UID with Android still needs manual verification. Firestore reads also need a real-account/device check. If sign-in fails, confirm the Google provider is enabled in Firebase Authentication and the selected local plist matches the build configuration. The header safe-area adjustment made on 2026-09-30 also needs a visual recheck on iPhone.

CLI build (from the repository root):

```sh
xcodebuild -project iosApp/FootballQuizIos.xcodeproj \
  -scheme FootballQuizIos -configuration Debug \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

For a physical iPhone, select a development team in Xcode and use an iOS device target. Debug installs beside production because it has a distinct bundle ID. App Store distribution and cloud writes are not configured yet.
