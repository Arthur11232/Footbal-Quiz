package com.arthuralexandryan.footballquiz.models

import android.content.Context
import com.arthuralexandryan.footballquiz.R

class GetQuestions(private val context: Context) {

    private val allQuestions: MutableList<Harcer> = mutableListOf()

    private fun buildHarcer(place: String, questionsId: Int, answersId: Int, rightsId: Int): Harcer {
        val questions = context.resources.getStringArray(questionsId)
        val answers = context.resources.getStringArray(answersId)
        val rights = context.resources.getStringArray(rightsId)
        return Harcer(place, buildHarcList(questions, answers, rights)).also { allQuestions.add(it) }
    }

    fun getFrance() = buildHarcer("France", R.array.france_questions, R.array.france_answers, R.array.france_right_answers)
    fun getGermany() = buildHarcer("Germany", R.array.germany_questions, R.array.germany_answers, R.array.germany_right_answers)
    fun getItaly() = buildHarcer("Italy", R.array.italy_questions, R.array.italy_answers, R.array.italy_right_answers)
    fun getEnglish() = buildHarcer("English", R.array.english_questions, R.array.english_answers, R.array.english_right_answers)
    fun getSpain() = buildHarcer("Spain", R.array.spain_questions, R.array.spain_answers, R.array.spain_right_answers)
    fun getSuperCup() = buildHarcer("Super Cup", R.array.super_cup_questions, R.array.super_cup_answers, R.array.super_cup_right_answers)
    fun getEuropaLeague() = buildHarcer("Europa League", R.array.europa_league_questions, R.array.europa_league_answers, R.array.europa_league_right_answers)
    fun getChampionsLeague() = buildHarcer("Champions League", R.array.champions_league_questions, R.array.champions_league_answers, R.array.champions_league_right_answers)
    fun getEuropeanChampionship() = buildHarcer("European Championship", R.array.european_cup_questions, R.array.european_cup_answers, R.array.european_cup_right_answers)
    fun getWorldChampionship() = buildHarcer("World Championship", R.array.world_cup_questions, R.array.world_cup_answers, R.array.world_cup_right_answers)
    fun getVSRM() = buildHarcer("vsRM", R.array.ron_mes_questions, R.array.ron_mes_answers, R.array.ron_mes_right_answers)
    fun getVSRB() = buildHarcer("vsRB", R.array.real_barce_questions, R.array.real_barce_answers, R.array.real_barce_right_answers)

    fun getAllQuestions(): List<Harcer> {
        getFrance(); getGermany(); getItaly(); getEnglish(); getSpain()
        getSuperCup(); getEuropaLeague(); getChampionsLeague(); getEuropeanChampionship()
        getWorldChampionship(); getVSRM(); getVSRB()
        return allQuestions
    }

    private fun buildHarcList(questions: Array<String>, answers: Array<String>, rights: Array<String>): MutableList<Harc> {
        var count = 0
        return questions.mapIndexed { i, q ->
            Harc(
                harc = q,
                patasxanner = Patasxanner(
                    a = answers[count],
                    b = answers[++count],
                    c = answers[++count],
                    d = answers[++count]
                ).also { count++ },
                chist = rights[i]
            )
        }.toMutableList()
    }
}
