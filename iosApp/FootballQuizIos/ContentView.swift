import FootballQuizShared
import SwiftUI

private enum Palette {
    static let forest = Color(red: 0.06, green: 0.24, blue: 0.17)
    static let lime = Color(red: 0.80, green: 0.95, blue: 0.44)
    static let paper = Color(red: 0.95, green: 0.96, blue: 0.91)
}

struct ContentView: View {
    var body: some View {
        NavigationStack {
            ZStack {
                Palette.forest.ignoresSafeArea()
                VStack(alignment: .leading, spacing: 24) {
                    Text("FOOTBALL QUIZ")
                        .font(.system(size: 13, weight: .black, design: .rounded))
                        .tracking(3)
                        .foregroundStyle(Palette.lime)

                    Spacer()

                    Text("A new pitch\nfor iOS.")
                        .font(.system(size: 52, weight: .bold, design: .rounded))
                        .tracking(-2)
                        .foregroundStyle(.white)

                    Text("First checkpoint: the same progression rules now run on Android and iOS.")
                        .font(.system(size: 18, design: .rounded))
                        .foregroundStyle(.white.opacity(0.75))

                    NavigationLink {
                        SharedDiagnosticsView()
                    } label: {
                        HStack {
                            Text("Inspect shared engine")
                            Spacer()
                            Image(systemName: "arrow.up.right")
                        }
                        .font(.system(size: 17, weight: .bold, design: .rounded))
                        .padding(20)
                        .foregroundStyle(Palette.forest)
                        .background(Palette.lime, in: RoundedRectangle(cornerRadius: 18))
                    }
                }
                .padding(28)
            }
        }
        .tint(Palette.lime)
    }
}

private struct SharedDiagnosticsView: View {
    private let diagnostics = SharedDiagnostics()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                Text("SHARED ENGINE")
                    .font(.system(size: 12, weight: .black, design: .rounded))
                    .tracking(3)
                    .foregroundStyle(Palette.forest)

                Text(diagnostics.moduleName)
                    .font(.system(size: 32, weight: .bold, design: .rounded))

                Text("Demo data only. No account or cloud progress is loaded here.")
                    .foregroundStyle(.secondary)

                VStack(alignment: .leading, spacing: 12) {
                    Text("France stage / 40 questions")
                        .font(.headline)
                    resultRow("39 / 40", complete: diagnostics.isFranceComplete(answered: 39))
                    resultRow("40 / 40", complete: diagnostics.isFranceComplete(answered: 40))
                }
                .padding(20)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(.white, in: RoundedRectangle(cornerRadius: 20))

                Text("\(diagnostics.stageCount) stage definitions")
                    .font(.headline)
                Text(diagnostics.stageIdsText)
                    .font(.system(.body, design: .monospaced))
                    .lineSpacing(5)
                    .textSelection(.enabled)
            }
            .padding(24)
        }
        .background(Palette.paper.ignoresSafeArea())
        .navigationTitle("Shared diagnostics")
        .navigationBarTitleDisplayMode(.inline)
        .toolbarBackground(Palette.forest, for: .navigationBar)
        .toolbarColorScheme(.dark, for: .navigationBar)
    }

    private func resultRow(_ label: String, complete: Bool) -> some View {
        HStack {
            Text(label)
            Spacer()
            Image(systemName: complete ? "checkmark.circle.fill" : "circle")
            Text(complete ? "Complete" : "In progress")
        }
        .foregroundStyle(complete ? Palette.forest : .secondary)
    }
}
