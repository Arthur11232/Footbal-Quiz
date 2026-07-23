package com.arthuralexandryan.footballquiz.presentation.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.data.repository.PendingRewardRepository
import com.arthuralexandryan.footballquiz.data.repository.PlaySessionRepository
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.usecase.notifications.ShowRewardReadyNotificationUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.progression.RefreshProgressionUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.reward.ConsumePendingRewardsAfterAuthUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.reward.SyncIssuedCertificatesToCloudUseCase
import com.arthuralexandryan.footballquiz.domain.reward.PendingRewardRecord
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.presentation.progression.ProgressionUiModelMapper
import com.arthuralexandryan.footballquiz.utils.CategoryType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlayViewModel(
    private val playSessionRepository: PlaySessionRepository,
    private val authRepository: AuthRepository,
    private val refreshProgressionUseCase: RefreshProgressionUseCase,
    private val certificateRepository: CertificateRepository,
    private val pendingRewardRepository: PendingRewardRepository,
    private val showRewardReadyNotificationUseCase: ShowRewardReadyNotificationUseCase,
    private val consumePendingRewardsAfterAuthUseCase: ConsumePendingRewardsAfterAuthUseCase,
    private val syncIssuedCertificatesToCloudUseCase: SyncIssuedCertificatesToCloudUseCase
) : ViewModel() {

    private val random = Random.Default
    private val _uiState = MutableStateFlow(PlayUiState())
    val uiState: StateFlow<PlayUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<PlayEffect>()
    val effects: SharedFlow<PlayEffect> = _effects.asSharedFlow()

    private var questions: MutableList<GameObjectSerializable> = mutableListOf()
    private var placeScores: GameObjectScores = GameObjectScores()
    private var categoryScores: GameObjectScores = GameObjectScores()
    private var categoryKey: String = ""
    private var sessionQuestionType: String = ""
    private var sessionCategoryType: CategoryType? = null
    private var forceReloadBarrier: Int = 0
    private var currentQuestionIndex: Int = -1
    private var answersSinceInterstitial: Int = 0
    private var initialized = false
    private var pendingAchievementCertificate: AchievementCertificatePayload? = null
    private var pendingRewardReadyNotification: AchievementCertificatePayload? = null
    private var rewardReadyNotificationSent: Boolean = false

    fun initialize(args: PlaySessionArgs) {
        if (initialized) return
        initialized = true
        questions = args.questions.toMutableList()
        placeScores = args.placeScores.copy()
        categoryScores = args.categoryScores.copy()
        categoryKey = args.categoryKey
        sessionQuestionType = args.questions.firstOrNull()?.type.orEmpty()
        sessionCategoryType = args.questions.firstOrNull()?.categoryType
        forceReloadBarrier = placeScores.place_score / 2

        _uiState.update {
            it.copy(
                isInitialized = true,
                titleText = args.titleText,
                categoryScoreText = categoryScores.category_score.orEmpty()
            )
        }
        consumePendingRewardsIfSignedIn()
        prepareNextQuestion()
    }

    fun onAnswerSelected(answer: String) {
        val currentIndex = currentQuestionIndex
        if (!_uiState.value.isAnswerEnabled || currentIndex !in questions.indices) return

        val currentQuestion = questions[currentIndex]
        val isCorrect = currentQuestion.right_answer.equals(answer, ignoreCase = true)

        questions[currentIndex] = currentQuestion.copy(isAnswered = true)
        if (isCorrect) {
            categoryScores.place_score_answer += 1
            placeScores.place_score_answer += 1
        }
        placeScores.place_score -= 1

        val totalScore = playSessionRepository.persistAnswer(
            question = currentQuestion,
            placeScores = placeScores.copy(),
            isCorrect = isCorrect
        )
        val progressionSnapshot = refreshProgressionUseCase()
        val currentUser = authRepository.getCurrentUser()
        val recipientName = resolveRecipientName(currentUser)
        val newlyCompletedStages = progressionSnapshot.stages
            .filter { it.isCurrentlyCompleted && it.achievedAtMillis != null }
            .sortedBy { it.achievedAtMillis }

        val newlyIssuedCertificates = mutableListOf<IssuedCertificateRecord>()
        val newlyPendingRewards = mutableListOf<PendingRewardRecord>()

        newlyCompletedStages.forEach { status ->
            val achievedAtMillis = status.achievedAtMillis ?: return@forEach

            val certificateId = buildCertificateId(
                stageKey = status.stageId.value,
                issuedAtMillis = achievedAtMillis
            )
            if (currentUser != null) {
                if (certificateRepository.getIssuedCertificate(status.stageId) != null) return@forEach
                newlyIssuedCertificates += IssuedCertificateRecord(
                    stageId = status.stageId,
                    certificateId = certificateId,
                    issuedAtMillis = achievedAtMillis,
                    recipientName = recipientName
                )
            } else {
                if (pendingRewardRepository.getPendingReward(status.stageId) != null) return@forEach
                newlyPendingRewards += PendingRewardRecord(
                    stageId = status.stageId,
                    certificateId = certificateId,
                    earnedAtMillis = achievedAtMillis,
                    recipientNameSnapshot = recipientName,
                    createdAsGuest = true
                )
            }
        }

        if (newlyIssuedCertificates.isNotEmpty()) {
            certificateRepository.saveIssuedCertificates(newlyIssuedCertificates)
            pendingAchievementCertificate = buildAchievementCertificatePayload(
                records = newlyIssuedCertificates.toIssuedRewardPreviewRecords(),
                recipientName = recipientName,
                isPendingAuthClaim = false
            )
            pendingRewardReadyNotification = pendingAchievementCertificate
            rewardReadyNotificationSent = false

            if (currentUser != null) {
                viewModelScope.launch {
                    certificateRepository.uploadIssuedCertificates(
                        userId = currentUser.uid,
                        recipientName = recipientName,
                        userEmail = currentUser.email,
                        records = newlyIssuedCertificates
                    )
                }
            }
        } else if (newlyPendingRewards.isNotEmpty()) {
            pendingRewardRepository.savePendingRewards(newlyPendingRewards)
            pendingAchievementCertificate = buildAchievementCertificatePayload(
                records = newlyPendingRewards.toPendingRewardPreviewRecords(),
                recipientName = recipientName,
                isPendingAuthClaim = true
            )
            pendingRewardReadyNotification = null
            rewardReadyNotificationSent = false
        } else {
            pendingAchievementCertificate = null
            pendingRewardReadyNotification = null
            rewardReadyNotificationSent = false
        }

        val resetEnabled = forceReloadBarrier > placeScores.place_score
        _uiState.update {
            it.copy(
                isAnswerEnabled = false,
                placeQuestionText = placeScores.placeScoreText,
                placeAnswerText = placeScores.placeScoreAnswerText,
                categoryAnswersText = categoryScores.placeScoreAnswerText,
                totalGoalText = totalScore.toString(),
                isResetEnabled = resetEnabled,
                forceResetAlpha = if (resetEnabled) 1f else 0.2f,
                answerFeedbackResId = if (isCorrect) R.string.answer_correct else R.string.answer_incorect,
                isAnswerCorrect = isCorrect
            )
        }

        answersSinceInterstitial += 1
        viewModelScope.launch {
            _effects.emit(PlayEffect.AnimateAnswer(isCorrect))
        }
    }

    fun onAnswerTransitionFinished() {
        val shouldShowInterstitial = answersSinceInterstitial == 5
        val achievementCertificate = pendingAchievementCertificate
        pendingAchievementCertificate = null
        if (achievementCertificate != null) {
            viewModelScope.launch {
                _effects.emit(PlayEffect.ShowAchievementCertificate(achievementCertificate))
            }
            if (shouldShowInterstitial) {
                answersSinceInterstitial = 0
            }
        } else if (shouldShowInterstitial) {
            answersSinceInterstitial = 0
            viewModelScope.launch {
                _effects.emit(PlayEffect.ShowInterstitialAd)
            }
        }
        prepareNextQuestion()
    }

    fun onResetApplied(placeScore: ScoreboardModel, isForce: Boolean) {
        questions = questions.map { it.copy(isAnswered = false) }.toMutableList()
        placeScores = GameObjectScores(
            place_score = placeScore.placeScore,
            place_score_answer = placeScore.placeAnswered,
            category_score = placeScore.categoryScore.toString()
        )
        categoryScores = GameObjectScores(
            place_score = categoryScores.place_score,
            place_score_answer = placeScore.categoryAnswered,
            category_score = placeScore.categoryScore.toString()
        )
        forceReloadBarrier = placeScores.place_score / 2
        currentQuestionIndex = -1
        if (!isForce) {
            _uiState.update {
                it.copy(
                    showFinishScreen = false
                )
            }
        }
        prepareNextQuestion()
    }

    fun resetSession(isForce: Boolean) {
        val categoryType = sessionCategoryType ?: return
        if (sessionQuestionType.isBlank()) return
        val resetScoreboard = playSessionRepository.resetSession(
            questionType = sessionQuestionType,
            categoryType = categoryType,
            answeredCount = getAnsweredCountForReset()
        )
        onResetApplied(resetScoreboard, isForce)
    }

    fun persistCurrentQuestionOnStop() {
        val question = questions.getOrNull(currentQuestionIndex) ?: return
        if (_uiState.value.showFinishScreen || placeScores.place_score <= 0) return
        playSessionRepository.saveCurrentQuestionIndex(
            categoryKey = categoryKey,
            placeType = question.type.orEmpty(),
            index = currentQuestionIndex
        )
    }

    fun syncProgressIfSignedIn() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            playSessionRepository.syncProgressIfSignedIn(user)
        }
    }

    fun onAchievementCertificateOpened() {
        pendingRewardReadyNotification = null
        rewardReadyNotificationSent = false
    }

    fun onHostStopped() {
        if (rewardReadyNotificationSent) return
        if (authRepository.getCurrentUser() == null) return
        val achievementCertificate = pendingRewardReadyNotification ?: return
        showRewardReadyNotificationUseCase(achievementCertificate)
        rewardReadyNotificationSent = true
    }

    fun getAnsweredCountForReset(): Int = placeScores.place_score_answer

    private fun prepareNextQuestion() {
        val unansweredIndexes = questions.indices.filter { !questions[it].isAnswered }
        if (unansweredIndexes.isEmpty()) {
            finishSession(0)
            return
        }

        if (placeScores.place_score <= 0) {
            finishSession(playSessionRepository.getTotalScoreText().toIntOrNull() ?: 0)
            return
        }

        if (placeScores.place_score > unansweredIndexes.size) {
            placeScores.place_score = unansweredIndexes.size
            questions.firstOrNull()?.type?.let { questionType ->
                playSessionRepository.persistPlaceProgress(questionType, placeScores.copy())
            }
        }

        val placeType = questions.firstOrNull()?.type.orEmpty()
        val savedIndex = playSessionRepository.consumeSavedQuestionIndex(categoryKey, placeType)
        currentQuestionIndex = if (savedIndex != null && savedIndex in unansweredIndexes) {
            savedIndex
        } else {
            unansweredIndexes[random.nextInt(unansweredIndexes.size)]
        }

        val question = questions[currentQuestionIndex]
        val options = mutableListOf(
            question.answer_A.orEmpty(),
            question.answer_B.orEmpty(),
            question.answer_C.orEmpty(),
            question.answer_D.orEmpty()
        ).shuffled(random).mapIndexed { index, text ->
            PlayAnswerOption(id = (index + 1).toString(), text = text)
        }

        _uiState.update {
            it.copy(
                isInitialized = true,
                isAnswerEnabled = true,
                showFinishScreen = false,
                questionText = question.question.orEmpty(),
                questionNumberText = placeScores.placeScoreText,
                placeQuestionText = placeScores.placeScoreText,
                placeAnswerText = placeScores.placeScoreAnswerText,
                categoryAnswersText = categoryScores.placeScoreAnswerText,
                categoryScoreText = categoryScores.category_score.orEmpty(),
                answerOptions = options,
                answerFeedbackResId = null,
                isAnswerCorrect = null,
                isResetEnabled = forceReloadBarrier > placeScores.place_score,
                forceResetAlpha = if (forceReloadBarrier > placeScores.place_score) 1f else 0.2f
            )
        }
    }

    private fun consumePendingRewardsIfSignedIn() {
        val currentUser = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            val recipientName = resolveRecipientName(currentUser)
            consumePendingRewardsAfterAuthUseCase(
                userId = currentUser.uid,
                recipientName = recipientName,
                userEmail = currentUser.email
            )
            syncIssuedCertificatesToCloudUseCase(
                userId = currentUser.uid,
                recipientName = recipientName,
                userEmail = currentUser.email
            )
        }
    }

    private fun finishSession(totalScore: Int) {
        _uiState.update {
            it.copy(
                isAnswerEnabled = false,
                showFinishScreen = true,
                totalGoalText = if (totalScore > 0) totalScore.toString() else playSessionRepository.getTotalScoreText(),
                answerFeedbackResId = null,
                isAnswerCorrect = null
            )
        }
    }

    private fun buildAchievementCertificatePayload(
        records: List<RewardPreviewRecord>,
        recipientName: String,
        isPendingAuthClaim: Boolean
    ): AchievementCertificatePayload? {
        val stages = records.map { record ->
            ProgressionUiModelMapper.buildFreshlyCompletedStageUiModel(
                stageId = record.stageId,
                achievedAtMillis = record.earnedAtMillis
            )
        }
        val primaryStage = stages.firstOrNull() ?: return null
        val primaryRecord = records.firstOrNull() ?: return null

        return AchievementCertificatePayload(
            primaryStageId = primaryStage.stageId.value,
            primaryStageTitleResId = primaryStage.titleResId,
            additionalStageTitleResIds = stages.drop(1).map { it.titleResId },
            recipientName = recipientName,
            issuedAtMillis = primaryRecord.earnedAtMillis,
            certificateId = primaryRecord.certificateId,
            isPendingAuthClaim = isPendingAuthClaim
        )
    }

    private fun resolveRecipientName(user: com.google.firebase.auth.FirebaseUser? = authRepository.getCurrentUser()): String {
        return user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
            ?: "Guest"
    }

    private fun buildCertificateId(stageKey: String, issuedAtMillis: Long): String {
        val normalizedStageKey = stageKey
            .replace("_", "-")
            .uppercase()
            .take(18)
        val timeSuffix = issuedAtMillis.toString().takeLast(6)
        return "FQ-$normalizedStageKey-$timeSuffix"
    }

    private fun List<IssuedCertificateRecord>.toIssuedRewardPreviewRecords(): List<RewardPreviewRecord> =
        map { record ->
            RewardPreviewRecord(
                stageId = record.stageId,
                certificateId = record.certificateId,
                earnedAtMillis = record.issuedAtMillis
            )
        }

    private fun List<PendingRewardRecord>.toPendingRewardPreviewRecords(): List<RewardPreviewRecord> =
        map { record ->
            RewardPreviewRecord(
                stageId = record.stageId,
                certificateId = record.certificateId,
                earnedAtMillis = record.earnedAtMillis
            )
        }

    private data class RewardPreviewRecord(
        val stageId: com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId,
        val certificateId: String,
        val earnedAtMillis: Long
    )
}
