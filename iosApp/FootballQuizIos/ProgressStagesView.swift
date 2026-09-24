import FootballQuizShared
import SwiftUI

struct ProgressStagesView: View {
    private let demo = DemoProgressReadModel()
    private let background = Color(red: 0.95, green: 0.96, blue: 0.91)
    private let forest = Color(red: 0.06, green: 0.24, blue: 0.17)

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                Text("PROGRESSION")
                    .font(.system(size: 12, weight: .black, design: .rounded))
                    .tracking(3)
                    .foregroundStyle(forest)

                Text("Every stage.\nOne shared rule.")
                    .font(.system(size: 36, weight: .bold, design: .rounded))

                Text("Preview data only: no account, cloud progress, or certificates are used here.")
                    .font(.system(size: 15, design: .rounded))
                    .foregroundStyle(.secondary)

                ForEach(0..<Int(demo.stageCount), id: \.self) { index in
                    stageRow(index)
                }
            }
            .padding(24)
        }
        .background(background.ignoresSafeArea())
        .navigationTitle("Stages")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func stageRow(_ index: Int) -> some View {
        let stageId = demo.stageIdAt(index: Int32(index))
        let state = demo.stageStateAt(index: Int32(index))
        let achieved = state == "ACHIEVED"
        let completed = state == "COMPLETED_NOW"

        return HStack(spacing: 16) {
            Image(systemName: achieved ? "checkmark.seal.fill" : completed ? "checkmark.circle.fill" : "circle")
                .font(.system(size: 24))
                .foregroundStyle(achieved ? forest : completed ? .orange : .gray)

            VStack(alignment: .leading, spacing: 5) {
                Text(stageId.replacingOccurrences(of: "_", with: " ").capitalized)
                    .font(.system(size: 16, weight: .bold, design: .rounded))
                Text(stageId)
                    .font(.system(size: 11, design: .monospaced))
                    .foregroundStyle(.secondary)
            }

            Spacer(minLength: 8)
            Text(achieved ? "Achieved" : completed ? "Complete" : "To play")
                .font(.system(size: 12, weight: .bold, design: .rounded))
                .foregroundStyle(achieved ? forest : .secondary)
        }
        .padding(16)
        .background(.white, in: RoundedRectangle(cornerRadius: 18))
    }
}
