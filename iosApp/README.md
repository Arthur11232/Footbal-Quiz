# Football Quiz iOS shell

This is the first SwiftUI integration checkpoint, not the full iOS game. It uses demo progress only and does not sign in or read cloud data.

## Run in Simulator

1. Open `iosApp/FootballQuizIos.xcodeproj` in Xcode.
2. Select the `FootballQuizIos` scheme and an Apple Silicon iPhone Simulator.
3. Press Run. The build phase runs `:shared:embedAndSignAppleFrameworkForXcode` automatically.
4. Open **Inspect shared engine**. France 39/40 must be **In progress** and 40/40 must be **Complete**. The screen also lists all 16 shared stage IDs.
5. Open **Preview stage progress**. France should show **Achieved**, Germany **Complete**, and Italy **To play**. The remaining stages are also **To play**. This is demo data only; no certificate can be issued from this screen.

The current KMP targets are `iosArm64` and `iosSimulatorArm64`; an Intel (`x86_64`) Simulator is not configured. The project excludes that simulator architecture. Xcode 27.0 and iOS Simulator 26.5 were used for the initial build and launch check.

CLI build (from the repository root):

```sh
xcodebuild -project iosApp/FootballQuizIos.xcodeproj \
  -scheme FootballQuizIos -configuration Debug \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

For a physical iPhone, select a development team in Xcode and use an iOS device target. App Store distribution and iOS authentication are not configured yet.
