package com.arthuralexandryan.footballquiz.models

import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.google.firebase.auth.FirebaseUser

data class UserStatsDTO(
        val email: String = "",
        val gameState: GameState = GameState(),
        val name: String = "",
        val regId: String = ""
)

data class GameState(
        var id: Int = 0,
        var total: Int = 0,
        var top5: Int = 0,
        var top5_answered: Int = 0,
        var ufa: Int = 0,
        var ufa_answered: Int = 0,
        var world: Int = 0,
        var world_answered: Int = 0,
        var vsRM: Int = 0,
        var vsRM_answered: Int = 0,
        var vsRB: Int = 0,
        var vsRB_answered: Int = 0,
        var top5Scores: PlacesTop5DTO? = null,
        var ufaScores: PlacesUFADTO? = null,
        var lastSynced: Long = System.currentTimeMillis()
)

data class PlacesTop5DTO(
    var france_score: Int = 0,
    var france_answered: Int = 0,
    var germany_score: Int = 0,
    var germany_answered: Int = 0,
    var italy_score: Int = 0,
    var italy_answered: Int = 0,
    var england_score: Int = 0,
    var england_answered: Int = 0,
    var spain_score: Int = 0,
    var spain_answered: Int = 0
)

data class PlacesUFADTO(
    var superCup_score: Int = 0,
    var superCup_answered: Int = 0,
    var europaLeague_score: Int = 0,
    var europaLeague_answered: Int = 0,
    var europaChampionship_score: Int = 0,
    var europaChampionship_answered: Int = 0,
    var championsLeague_score: Int = 0,
    var championsLeague_answered: Int = 0
)

fun FQ_Scores.toDTO(user: FirebaseUser): UserStatsDTO {
    return UserStatsDTO(
            email = user.email.toString(),
            GameState(
        id = this.id,
        total = this.fq_all,
        top5 = this.top5,
        top5_answered = this.top5_answered,
        ufa = this.ufa,
        ufa_answered = this.ufa_answered,
        world = this.world,
        world_answered = this.world_answered,
        vsRM = this.vsRM,
        vsRM_answered = this.vsRM_answered,
        vsRB = this.vsRB,
        vsRB_answered = this.vsRB_answered,
        top5Scores = this.top5Scores?.let {
            PlacesTop5DTO(
                it.france_score, it.france_answered,
                it.germany_score, it.germany_answered,
                it.italy_score, it.italy_answered,
                it.england_score, it.england_answered,
                it.spain_score, it.spain_answered
            )
        },
        ufaScores = this.ufaScores?.let {
            PlacesUFADTO(
                it.superCup_score, it.superCup_answered,
                it.europaLeague_score, it.europaLeague_answered,
                it.europaChampionship_score, it.europaChampionship_answered,
                it.championsLeague_score, it.championsLeague_answered
            )
        }
                    ),
            name = user.displayName.toString(),
            regId = user.uid
    )
}
