# Football Quiz iOS shell

This is an early SwiftUI integration checkpoint, not the full iOS game. It supports Google/Firebase sign-in and uses shared progression/sync decision models, but stage progress is still demo data. It does not read or write cloud progress.

The iOS Firebase app is registered in project `footbal-quiz-1f62a` with bundle ID `com.arthuralexandryan.footballquiz`. Place its `GoogleService-Info.plist` in `iosApp/FootballQuizIos/` before building. Xcode includes it in the app resources; the file is intentionally ignored by Git, like Android's `google-services.json`. Firebase Auth and Google Sign-In are installed with Swift Package Manager; `Package.resolved` is versioned for reproducible builds. Cloud progress and Firestore are not connected.

## Run in Simulator

1. Open `iosApp/FootballQuizIos.xcodeproj` in Xcode.
2. Select the `FootballQuizIos` scheme and an Apple Silicon iPhone Simulator.
3. Press Run. The build phase runs `:shared:embedAndSignAppleFrameworkForXcode` automatically.
4. Open **Inspect shared engine**. France 39/40 must be **In progress** and 40/40 must be **Complete**. The screen also lists all 16 shared stage IDs.
5. Open **Preview stage progress**. France should show **Achieved**, Germany **Complete**, and Italy **To play**. The remaining stages are also **To play**. This is demo data only; no certificate can be issued from this screen.
6. In **Account checkpoint**, tap **Sign in** and choose the same Google account used on Android. Compare the displayed Firebase UID with that account's UID in Firebase Console > Authentication > Users. Restart the app to check session persistence, then tap **Sign out**. Cancelling the account picker should not show an error. This is an auth-only test; it must not read or write progress.

The current KMP targets are `iosArm64` and `iosSimulatorArm64`; an Intel (`x86_64`) Simulator is not configured. The project excludes that simulator architecture. Xcode 27.0 and iOS Simulator 26.5 were used for the initial build and launch check.

The Firebase configuration was verified in a Debug Simulator build on 2026-09-25: `GoogleService-Info.plist` is present in the `.app` bundle and its bundle ID matches the built app. Google sign-in succeeded on a real iPhone. Session persistence, sign-out, and matching the Firebase UID with Android still need manual verification. If sign-in fails, confirm the Google provider is enabled in Firebase Authentication and the URL scheme in `Info.plist` matches `REVERSED_CLIENT_ID` from the local Firebase plist. The header safe-area adjustment made on 2026-09-30 also needs a visual recheck on iPhone.

CLI build (from the repository root):

```sh
xcodebuild -project iosApp/FootballQuizIos.xcodeproj \
  -scheme FootballQuizIos -configuration Debug \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

For a physical iPhone, select a development team in Xcode and use an iOS device target. App Store distribution and cloud sync are not configured yet.
