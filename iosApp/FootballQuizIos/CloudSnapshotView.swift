import FootballQuizShared
import SwiftUI

struct CloudSnapshotView: View {
  let userId: String
  @StateObject private var reader = CloudProgressReader()
  @StateObject private var localStore = LocalProgressStore()
  @StateObject private var certificateStore = LocalCertificateStore()
  @State private var statusMessage: String?
  @State private var forceConflictReview = false

  private let background = Color(red: 0.95, green: 0.96, blue: 0.91)
  private let forest = Color(red: 0.06, green: 0.24, blue: 0.17)

  var body: some View {
    ScrollView {
      VStack(alignment: .leading, spacing: 18) {
        Text("CLOUD SNAPSHOT")
          .font(.system(size: 12, weight: .black, design: .rounded))
          .tracking(3)
          .foregroundStyle(forest)

        Text("Account sync preview")
          .font(.system(size: 34, weight: .bold, design: .rounded))

        Text("This screen compares Android-compatible cloud data with protected local iOS storage. Cloud writes remain disabled.")
          .foregroundStyle(.secondary)

        content
      }
      .padding(24)
    }
    .background(background.ignoresSafeArea())
    .navigationTitle("Cloud data")
    .navigationBarTitleDisplayMode(.inline)
    .task(id: userId) {
      localStore.load(userId: userId)
      certificateStore.load(userId: userId)
      await reloadCloud()
    }
    .onDisappear {
      reader.clear()
      localStore.clearInMemory()
      certificateStore.clearInMemory()
    }
  }

  @ViewBuilder
  private var content: some View {
    switch reader.state {
    case .idle, .loading:
      ProgressView("Reading Firestore…")
        .frame(maxWidth: .infinity, alignment: .leading)
    case .failed(let message):
      statusCard(title: "Could not read cloud data", message: message, symbol: "exclamationmark.triangle.fill")
      localStatsCard
      localCertificatesCard
      retryButton
    case .loaded(let payload, let certificates, let invalidCertificateCount, let certificatesFromCache):
      statsCard(payload?.snapshot)
      localStatsCard
      syncDecisionCard(payload)
      certificatesCard(
        certificates,
        invalidCount: invalidCertificateCount,
        isFromCache: certificatesFromCache
      )
      localCertificatesCard
      retryButton
    }
  }

  private var localStatsCard: some View {
    VStack(alignment: .leading, spacing: 12) {
      Label("Local iOS snapshot", systemImage: "iphone")
        .font(.headline)
      if let local = localStore.record?.snapshot {
        metric("Total score", value: "\(local.totalScore)")
        metric("Answered", value: "\(local.totalAnswered)")
        metric("Completed stages", value: "\(local.completedStageIds.count)")
        Text("Stored for this Firebase UID with iOS file protection.")
          .font(.footnote)
          .foregroundStyle(.secondary)
      } else {
        Text("No real local progress is stored for this account. Demo progress is not used.")
          .foregroundStyle(.secondary)
      }
      if let errorMessage = localStore.errorMessage {
        Text(errorMessage)
          .font(.footnote)
          .foregroundStyle(.red)
      }
    }
    .cardStyle()
  }

  @ViewBuilder
  private func syncDecisionCard(_ cloudPayload: CloudUserStatsPayload?) -> some View {
    let decision = resolveDecision(cloudPayload)

    VStack(alignment: .leading, spacing: 12) {
      Label("Sync decision", systemImage: "arrow.triangle.2.circlepath")
        .font(.headline)

      if decision === SyncDecision.none {
        Text(localStore.record == nil ? "Nothing to restore or upload." : "Local and cloud summaries match.")
          .foregroundStyle(.secondary)
      } else if decision === SyncDecision.autoRestoreCloud {
        Text("Cloud progress exists and local iOS progress is empty.")
          .foregroundStyle(.secondary)
        Button("Restore cloud snapshot locally") {
          guard let cloudPayload else { return }
          if localStore.restore(userId: userId, payload: cloudPayload) {
            clearKeepLocalChoice()
            statusMessage = "Cloud snapshot restored to protected local iOS storage. Firestore was not changed."
          }
        }
        .buttonStyle(.borderedProminent)
        .tint(forest)
        .disabled(cloudPayload?.encodedData == nil)
      } else if decision === SyncDecision.uploadLocal {
        Text("Local progress exists and cloud progress is empty. Upload is locked until a test UID backup and a safe write environment are confirmed.")
          .foregroundStyle(.secondary)
      } else if decision === SyncDecision.promptConflict {
        if isKeepLocalChoiceSaved(cloudPayload), !forceConflictReview {
          Text("Keep Local was selected for this exact local/cloud pair. Nothing was uploaded or overwritten.")
            .foregroundStyle(.secondary)
          Button("Review conflict again") {
            forceConflictReview = true
          }
          .buttonStyle(.bordered)
        } else {
          Text("Local and cloud progress differ. Choose explicitly; leaving this screen changes nothing.")
            .foregroundStyle(.secondary)
          Button("Restore Cloud to this iPhone") {
            guard let cloudPayload else { return }
            if localStore.restore(userId: userId, payload: cloudPayload) {
              clearKeepLocalChoice()
              forceConflictReview = false
              statusMessage = "Cloud snapshot replaced the local iOS snapshot. Firestore was not changed."
            }
          }
          .buttonStyle(.borderedProminent)
          .tint(forest)
          .disabled(cloudPayload?.encodedData == nil)

          Button("Keep Local — do not upload") {
            saveKeepLocalChoice(cloudPayload)
            forceConflictReview = false
            statusMessage = "Local progress kept. No cloud upload was performed."
          }
          .buttonStyle(.bordered)
        }
      }

      if let statusMessage {
        Text(statusMessage)
          .font(.footnote)
          .foregroundStyle(forest)
      }
    }
    .cardStyle()
  }

  private func statsCard(_ stats: CloudProgressSnapshot?) -> some View {
    VStack(alignment: .leading, spacing: 12) {
      Label("Progress snapshot", systemImage: "chart.bar.doc.horizontal")
        .font(.headline)

      if let stats {
        metric("Total score", value: "\(stats.totalScore)")
        metric("Answered", value: "\(stats.totalAnswered)")
        metric("Completed stages", value: "\(stats.completedStageIds.count)")
        if let lastSyncedMillis = stats.lastSyncedMillis, lastSyncedMillis > 0 {
          metric("Last synced", value: Self.dateText(milliseconds: lastSyncedMillis))
        }
        if !stats.completedStageIds.isEmpty {
          Text(stats.completedStageIds.joined(separator: ", "))
            .font(.system(.footnote, design: .monospaced))
            .textSelection(.enabled)
        }
        Text(stats.isFromCache ? "Source: offline cache" : "Source: Firestore server")
          .font(.footnote)
          .foregroundStyle(.secondary)
        if let notice = stats.schemaNotice {
          Text(notice)
            .font(.footnote)
            .foregroundStyle(.orange)
        }
      } else {
        Text("No user stats document exists for this UID.")
          .foregroundStyle(.secondary)
      }
    }
    .cardStyle()
  }

  private func certificatesCard(
    _ certificates: [CloudCertificate],
    invalidCount: Int,
    isFromCache: Bool
  ) -> some View {
    VStack(alignment: .leading, spacing: 14) {
      Label("Cloud certificates", systemImage: "checkmark.seal")
        .font(.headline)

      if certificates.isEmpty {
        Text("No readable certificates were found.")
          .foregroundStyle(.secondary)
      } else {
        ForEach(certificates) { certificate in
          VStack(alignment: .leading, spacing: 5) {
            Text(certificate.stageId.replacingOccurrences(of: "_", with: " ").capitalized)
              .font(.headline)
            if !certificate.recipientName.isEmpty {
              Text(certificate.recipientName)
            }
            Text(certificate.certificateId)
              .font(.system(.caption, design: .monospaced))
              .textSelection(.enabled)
            Text(Self.dateText(milliseconds: certificate.issuedAtMillis) + " · " + certificate.source)
              .font(.caption)
              .foregroundStyle(.secondary)
          }
          if certificate.id != certificates.last?.id {
            Divider()
          }
        }
      }

      if invalidCount > 0 {
        Text("\(invalidCount) certificate document(s) used an unsupported schema and were skipped.")
          .font(.footnote)
          .foregroundStyle(.orange)
      }

      Text(isFromCache ? "Source: offline cache" : "Source: Firestore server")
        .font(.footnote)
        .foregroundStyle(.secondary)
    }
    .cardStyle()
  }

  private var localCertificatesCard: some View {
    VStack(alignment: .leading, spacing: 14) {
      Label("Saved certificates on this iPhone", systemImage: "iphone.and.arrow.forward")
        .font(.headline)

      if certificateStore.certificates.isEmpty {
        Text("No certificates are saved locally for this UID.")
          .foregroundStyle(.secondary)
      } else {
        ForEach(certificateStore.certificates) { certificate in
          VStack(alignment: .leading, spacing: 5) {
            Text(certificate.stageId.replacingOccurrences(of: "_", with: " ").capitalized)
              .font(.headline)
            Text(certificate.certificateId)
              .font(.system(.caption, design: .monospaced))
              .textSelection(.enabled)
            Text(Self.dateText(milliseconds: certificate.issuedAtMillis))
              .font(.caption)
              .foregroundStyle(.secondary)
          }
          if certificate.id != certificateStore.certificates.last?.id {
            Divider()
          }
        }
      }

      Text("Saved per Firebase UID. Logout, progress restore, and an empty cloud response do not delete these records.")
        .font(.footnote)
        .foregroundStyle(.secondary)
      if let notice = certificateStore.mergeNotice {
        Text(notice)
          .font(.footnote)
          .foregroundStyle(forest)
      }
      if let errorMessage = certificateStore.errorMessage {
        Text(errorMessage)
          .font(.footnote)
          .foregroundStyle(.red)
      }
    }
    .cardStyle()
  }

  private var retryButton: some View {
    Button {
      Task { await reloadCloud() }
    } label: {
      Label("Read again", systemImage: "arrow.clockwise")
        .frame(maxWidth: .infinity)
    }
    .buttonStyle(.borderedProminent)
    .tint(forest)
  }

  private func reloadCloud() async {
    await reader.load(userId: userId)
    if case .loaded(_, let certificates, _, _) = reader.state {
      certificateStore.mergeCloud(certificates, userId: userId)
    }
  }

  private func metric(_ label: String, value: String) -> some View {
    HStack {
      Text(label)
      Spacer()
      Text(value).fontWeight(.bold)
    }
  }

  private func statusCard(title: String, message: String, symbol: String) -> some View {
    VStack(alignment: .leading, spacing: 10) {
      Label(title, systemImage: symbol).font(.headline)
      Text(message).foregroundStyle(.secondary)
    }
    .cardStyle()
  }

  private static func dateText(milliseconds: Int64) -> String {
    Date(timeIntervalSince1970: TimeInterval(milliseconds) / 1_000).formatted(
      date: .abbreviated,
      time: .shortened
    )
  }

  private func resolveDecision(_ cloudPayload: CloudUserStatsPayload?) -> SyncDecision {
    let local = localStore.record?.snapshot
    let localSummary = LocalProgressSummary(
      hasProgress: local?.hasProgress ?? false,
      totalScore: Int32(clamping: Int64(local?.totalScore ?? 0)),
      lastSynced: local?.lastSyncedMillis ?? 0
    )
    let cloudSummary = cloudPayload.map { payload in
      CloudProgressSummary(
        hasProgress: payload.snapshot.hasProgress,
        totalScore: Int32(clamping: Int64(payload.snapshot.totalScore)),
        lastSynced: payload.snapshot.lastSyncedMillis ?? 0
      )
    }
    return SyncDecisionResolver.shared.resolve(local: localSummary, cloud: cloudSummary)
  }

  private func isKeepLocalChoiceSaved(_ cloudPayload: CloudUserStatsPayload?) -> Bool {
    guard
      let localFingerprint = localStore.record?.fingerprint,
      let cloudFingerprint = cloudPayload?.fingerprint
    else { return false }
    return UserDefaults.standard.string(forKey: keepLocalKey) == "\(localFingerprint):\(cloudFingerprint)"
  }

  private func saveKeepLocalChoice(_ cloudPayload: CloudUserStatsPayload?) {
    guard
      let localFingerprint = localStore.record?.fingerprint,
      let cloudFingerprint = cloudPayload?.fingerprint
    else { return }
    UserDefaults.standard.set("\(localFingerprint):\(cloudFingerprint)", forKey: keepLocalKey)
  }

  private func clearKeepLocalChoice() {
    UserDefaults.standard.removeObject(forKey: keepLocalKey)
  }

  private var keepLocalKey: String {
    "sync_keep_local_\(userId)"
  }
}

private extension View {
  func cardStyle() -> some View {
    padding(18)
      .frame(maxWidth: .infinity, alignment: .leading)
      .background(.white, in: RoundedRectangle(cornerRadius: 18))
  }
}
