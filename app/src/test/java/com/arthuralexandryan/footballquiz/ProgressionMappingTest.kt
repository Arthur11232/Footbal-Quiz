package com.arthuralexandryan.footballquiz

import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.data.repository.impl.toStageProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressionMappingTest {
    @Test
    fun mapsEveryProfileCounterAndThresholdToItsPersistedStageId() {
        val profile = ProfileProgressSnapshot(
            franceAnswered = 1, germanyAnswered = 2, italyAnswered = 3,
            englandAnswered = 4, spainAnswered = 5, superCupAnswered = 6,
            europaLeagueAnswered = 7, championsLeagueAnswered = 8,
            europeanChampionshipAnswered = 9, vsrmAnswered = 10, vsrbAnswered = 11,
            top5Answered = 12, uefaAnswered = 13, worldAnswered = 14,
            versusAnswered = 15, totalAnswered = 16,
            franceTotal = 101, germanyTotal = 102, italyTotal = 103,
            englandTotal = 104, spainTotal = 105, superCupTotal = 106,
            europaLeagueTotal = 107, championsLeagueTotal = 108,
            europeanChampionshipTotal = 109, vsrmTotal = 110, vsrbTotal = 111,
            top5Total = 112, uefaTotal = 113, worldTotal = 114,
            versusTotal = 115, totalQuestions = 116,
            progressPercent = 99, totalScore = 999
        )
        val expected = mapOf(
            "top5_france_complete" to (1 to 101),
            "top5_germany_complete" to (2 to 102),
            "top5_italy_complete" to (3 to 103),
            "top5_england_complete" to (4 to 104),
            "top5_spain_complete" to (5 to 105),
            "uefa_super_cup_complete" to (6 to 106),
            "uefa_europa_league_complete" to (7 to 107),
            "uefa_champions_league_complete" to (8 to 108),
            "uefa_european_championship_complete" to (9 to 109),
            "versus_vsrm_complete" to (10 to 110),
            "versus_vsrb_complete" to (11 to 111),
            "top5_complete" to (12 to 112),
            "uefa_complete" to (13 to 113),
            "world_complete" to (14 to 114),
            "versus_complete" to (15 to 115),
            "app_complete" to (16 to 116)
        )
        val stages = profile.toStageProgress()
        assertEquals(16, stages.size)
        assertEquals(expected, stages.associate { it.stageId.value to (it.answered to it.total) })
    }
}
