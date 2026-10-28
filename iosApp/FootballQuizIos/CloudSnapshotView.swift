import FootballQuizShared
import FirebaseFirestore
import SwiftUI

struct CloudSnapshotView: View {
  let userId: String
  @StateObject private var reader = CloudProgressReader()
  @StateObject private var localStore = LocalProgressStore()
  @StateObject private var certificateStore = LocalCertificateStore()
  @StateObject private var backupStore = CloudBackupStore()
  @StateObject private var cloudWriter = ControlledCloudWriter()
  @State private var statusMessage: String?
  @State private var forceConflictReview = false
  @State private var pendingUpload: PendingCloudUpload?
  #if DEBUG
    @State private var rulesProbeState: RulesProbeState = .idle
  #endif

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

        Text("This screen compares Android-compatible cloud data with protected local iOS storage. Writes require the Debug dev app, a matching backup, and explicit confirmation.")
          .foregroundStyle(.secondary)

        content
      }
      .padding(24)
    }
    .background(background.ignoresSafeArea())
    .navigationTitle("Cloud data")
    .navigationBarTitleDisplayMode(.inline)
    .task(id: userId) {
      #if DEBUG
        rulesProbeState = .idle
      #endif
      localStore.load(userId: userId)
      certificateStore.load(userId: userId)
      backupStore.loadLatest(userId: userId)
      await reloadCloud()
    }
    .onDisappear {
      reader.clear()
      localStore.clearInMemory()
      certificateStore.clearInMemory()
      backupStore.clearInMemory()
      cloudWriter.reset()
      #if DEBUG
        rulesProbeState = .idle
      #endif
    }
    .alert(
      "Upload local progress to cloud?",
      isPresented: Binding(
        get: { pendingUpload != nil },
        set: { if !$0 { pendingUpload = nil } }
      )
    ) {
      Button("Cancel", role: .cancel) {
        pendingUpload = nil
      }
      Button("Upload", role: .destructive) {
        guard let request = pendingUpload else { return }
        pendingUpload = nil
        Task { await performUpload(request) }
      }
    } message: {
      Text("Only the progress document will be written. The transaction will stop if Firestore changed after the matching backup. Certificates are not modified.")
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
    case .loaded(
      let payload,
      let statsFromCache,
      let certificates,
      let invalidCertificateCount,
      let certificatesFromCache
    ):
      statsCard(payload?.snapshot)
      localStatsCard
      syncDecisionCard(payload)
      certificatesCard(
        certificates,
        invalidCount: invalidCertificateCount,
        isFromCache: certificatesFromCache
      )
      localCertificatesCard
      backupCard(stats: payload, certificates: certificates)
      cloudWriteCard(
        stats: payload,
        statsFromCache: statsFromCache,
        certificates: certificates,
        certificatesFromCache: certificatesFromCache
      )
      #if DEBUG
        rulesIsolationCard
      #endif
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
        Text("Local progress exists and cloud progress is empty. Use the controlled dev upload below only after creating a matching backup.")
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

  private func backupCard(
    stats: CloudUserStatsPayload?,
    certificates: [CloudCertificate]
  ) -> some View {
    let matchesCurrent = backupStore.matchesCurrent(stats: stats, certificates: certificates)
    return VStack(alignment: .leading, spacing: 12) {
      Label("Pre-write backup", systemImage: "externaldrive.badge.checkmark")
        .font(.headline)

      Text(
        matchesCurrent
          ? "The current cloud document and certificate set have a matching protected backup."
          : "Create a fresh backup before a controlled dev upload. Upload stays locked until the backup matches."
      )
      .foregroundStyle(.secondary)

      Button("Create current cloud backup") {
        _ = backupStore.create(userId: userId, stats: stats, certificates: certificates)
      }
      .buttonStyle(.borderedProminent)
      .tint(forest)

      if let latest = backupStore.latest {
        Text("Latest: \(Self.dateText(milliseconds: latest.createdAtMillis))")
          .font(.footnote)
          .foregroundStyle(.secondary)
        ShareLink(item: latest.url) {
          Label("Export backup file", systemImage: "square.and.arrow.up")
        }
        .font(.headline)
      }

      Text("The exported file contains account progress and certificate details. Store it privately.")
        .font(.footnote)
        .foregroundStyle(.orange)

      if let errorMessage = backupStore.errorMessage {
        Text(errorMessage)
          .font(.footnote)
          .foregroundStyle(.red)
      }
    }
    .cardStyle()
  }

  @ViewBuilder
  private func cloudWriteCard(
    stats: CloudUserStatsPayload?,
    statsFromCache: Bool,
    certificates: [CloudCertificate],
    certificatesFromCache: Bool
  ) -> some View {
    let decision = resolveDecision(stats)
    let isExactMatch = localStore.record?.fingerprint == stats?.fingerprint
      && localStore.record != nil
      && stats != nil
    let isRelevant = decision === SyncDecision.uploadLocal
      || decision === SyncDecision.promptConflict
      || (decision === SyncDecision.none && isExactMatch)

    if isRelevant {
      let hasServerSnapshot = !statsFromCache && !certificatesFromCache
      let hasMatchingBackup = backupStore.matchesCurrent(stats: stats, certificates: certificates)
      let hasLocalPayload = localStore.record?.encodedData != nil
      let canUpload = ControlledCloudWriter.isAllowedBuild
        && hasServerSnapshot
        && hasMatchingBackup
        && hasLocalPayload

      VStack(alignment: .leading, spacing: 12) {
        Label("Controlled dev upload", systemImage: "icloud.and.arrow.up")
          .font(.headline)

        Text(
          isExactMatch
            ? "Local and cloud fingerprints match. This first-write test changes only lastSynced; scores, stages, and certificates stay unchanged."
            : "Writes only users/{uid} stats. Certificates are never written by this step."
        )
          .foregroundStyle(.secondary)

        if !ControlledCloudWriter.isAllowedBuild {
          Text("Locked: use the Debug dev app.")
            .font(.footnote)
            .foregroundStyle(.orange)
        } else if !hasServerSnapshot {
          Text("Locked: read the current data from the Firestore server while online.")
            .font(.footnote)
            .foregroundStyle(.orange)
        } else if !hasMatchingBackup {
          Text("Locked: create a fresh matching backup above.")
            .font(.footnote)
            .foregroundStyle(.orange)
        } else if !hasLocalPayload {
          Text("Locked: no valid local progress payload is available.")
            .font(.footnote)
            .foregroundStyle(.orange)
        }

        Button(isExactMatch ? "Verify matching progress upload" : "Upload local progress") {
          guard let localData = localStore.record?.encodedData else { return }
          pendingUpload = PendingCloudUpload(
            localData: localData,
            expectedCloudExists: stats != nil,
            expectedCloudFingerprint: stats?.fingerprint
          )
        }
        .buttonStyle(.borderedProminent)
        .tint(.orange)
        .disabled(!canUpload || cloudWriter.isWriting)

        switch cloudWriter.state {
        case .failed(let message):
          Text(message)
            .font(.footnote)
            .foregroundStyle(.red)
        case .writing:
          ProgressView("Verifying backup and writing transaction…")
            .font(.footnote)
        case .succeeded:
          Text("The server confirmed the progress upload.")
            .font(.footnote)
            .foregroundStyle(forest)
        case .idle:
          EmptyView()
        }
      }
      .cardStyle()
    }
  }

  #if DEBUG
    private var rulesIsolationCard: some View {
      VStack(alignment: .leading, spacing: 12) {
        Label("Firestore rules check", systemImage: "lock.shield")
          .font(.headline)

        Text("Performs server-only reads against a deliberately different UID. It never writes data and passes only when both the user document and certificate path return PERMISSION_DENIED.")
          .foregroundStyle(.secondary)

        Button("Verify UID isolation") {
          Task { await verifyFirestoreIsolation() }
        }
        .buttonStyle(.borderedProminent)
        .tint(forest)
        .disabled(rulesProbeState.isRunning || !ControlledCloudWriter.isAllowedBuild)

        switch rulesProbeState {
        case .idle:
          if !ControlledCloudWriter.isAllowedBuild {
            Text("Locked: use the Debug app connected to the Firebase Dev project.")
              .font(.footnote)
              .foregroundStyle(.orange)
          }
        case .running:
          ProgressView("Checking server rules…")
            .font(.footnote)
        case .passed:
          Text("Passed: Firestore denied both foreign-UID reads.")
            .font(.footnote)
            .foregroundStyle(forest)
        case .failed(let message):
          Text(message)
            .font(.footnote)
            .foregroundStyle(.red)
        }
      }
      .cardStyle()
    }

    private func verifyFirestoreIsolation() async {
      rulesProbeState = .running
      guard ControlledCloudWriter.isAllowedBuild else {
        rulesProbeState = .failed("Rules check is available only in the Firebase Dev build.")
        return
      }

      let probeUserId = "codex_rules_foreign_uid_probe"
      guard probeUserId != userId else {
        rulesProbeState = .failed("Could not create a foreign-UID probe path.")
        return
      }

      let foreignUser = Firestore.firestore().collection("users").document(probeUserId)
      let references = [
        foreignUser,
        foreignUser.collection("certificates").document("rules_probe")
      ]

      for reference in references {
        do {
          _ = try await reference.getDocument(source: .server)
          rulesProbeState = .failed("Rules check failed: a foreign-UID path was readable. No data was written.")
          return
        } catch {
          let nsError = error as NSError
          guard
            nsError.domain == FirestoreErrorDomain,
            FirestoreErrorCode.Code(rawValue: nsError.code) == .permissionDenied
          else {
            rulesProbeState = .failed("Rules check could not finish online: \(error.localizedDescription)")
            return
          }
        }
      }

      rulesProbeState = .passed
    }
  #endif

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
    if case .loaded(_, _, let certificates, _, _) = reader.state {
      certificateStore.mergeCloud(certificates, userId: userId)
    }
  }

  private func performUpload(_ request: PendingCloudUpload) async {
    guard let result = await cloudWriter.uploadStats(
      userId: userId,
      localData: request.localData,
      expectedCloudExists: request.expectedCloudExists,
      expectedCloudFingerprint: request.expectedCloudFingerprint
    ) else {
      await reloadCloud()
      return
    }

    clearKeepLocalChoice()
    await reloadCloud()

    var localSaved = false
    var postWriteBackupCreated = false
    if case .loaded(let stats, _, let certificates, _, _) = reader.state, let stats {
      localSaved = localStore.restore(userId: userId, payload: stats)
      postWriteBackupCreated = backupStore.create(userId: userId, stats: stats, certificates: certificates)
    } else {
      localSaved = localStore.replaceAfterConfirmedUpload(userId: userId, encodedData: result.encodedData)
    }

    if localSaved, postWriteBackupCreated {
      statusMessage = "Local progress was uploaded, read back from Firestore, and saved with a new post-write backup."
    } else if localSaved {
      statusMessage = "Firestore confirmed the upload and local progress was saved. Read-back or post-write backup needs another online retry."
    } else {
      statusMessage = "Firestore confirmed the upload, but the refreshed local snapshot could not be saved. Read the local storage warning below."
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

#if DEBUG
  private enum RulesProbeState {
    case idle
    case running
    case passed
    case failed(String)

    var isRunning: Bool {
      if case .running = self { return true }
      return false
    }
  }
#endif

private struct PendingCloudUpload {
  let localData: Data
  let expectedCloudExists: Bool
  let expectedCloudFingerprint: String?
}

private extension ControlledCloudWriter {
  var isWriting: Bool {
    if case .writing = state { return true }
    return false
  }
}

private extension View {
  func cardStyle() -> some View {
    padding(18)
      .frame(maxWidth: .infinity, alignment: .leading)
      .background(.white, in: RoundedRectangle(cornerRadius: 18))
  }
}
