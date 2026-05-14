package com.arthuralexandryan.footballquiz.data.repository.impl

import com.arthuralexandryan.footballquiz.constants.Constant.INIT_DB
import com.arthuralexandryan.footballquiz.data.repository.QuestionsRepository
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.Answers
import com.arthuralexandryan.footballquiz.models.FirestoreQuestionService
import com.arthuralexandryan.footballquiz.models.IsSetQuestions
import com.arthuralexandryan.footballquiz.models.QuestionModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class QuestionsRepositoryImpl(
    private val firestoreQuestionService: FirestoreQuestionService,
    private val settingsRepository: SettingsRepository,
    private val dbHelper: DB_Helper
) : QuestionsRepository {

    override suspend fun ensureLocalizationSync(isNew: Boolean): Result<Unit> {
        val selectedLocale = settingsRepository.getSelectedLocale()
        val questionsLocale = settingsRepository.getQuestionsLocale()
        val isInitialized = settingsRepository.isQuestionsInitialized()

        if (isInitialized && selectedLocale == questionsLocale) {
            return Result.success(Unit)
        }

        return suspendCancellableCoroutine { continuation ->
            firestoreQuestionService.getQuestions(selectedLocale) { success, questionsList ->
                if (!success || questionsList == null) {
                    continuation.resume(Result.failure(IllegalStateException("Failed to load questions from Firestore")))
                    return@getQuestions
                }

                val mappedQuestions = questionsList.map { question ->
                    QuestionModel().apply {
                        this.question = question.question ?: ""
                        place = question.type ?: ""
                        answered = question.isAnswered
                        answers = Answers().apply {
                            A = question.answer_A ?: ""
                            B = question.answer_B ?: ""
                            C = question.answer_C ?: ""
                            D = question.answer_D ?: ""
                            right = question.right_answer ?: ""
                        }
                    }
                }

                dbHelper.setQuestionsToDB(mappedQuestions, isNew, IsSetQuestions {
                    settingsRepository.setQuestionsInitialized(true)
                    settingsRepository.setQuestionsLocale(selectedLocale)
                    continuation.resume(Result.success(Unit))
                })
            }
        }
    }
}
