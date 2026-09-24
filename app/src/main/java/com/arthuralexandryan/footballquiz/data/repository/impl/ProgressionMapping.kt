package com.arthuralexandryan.footballquiz.data.repository.impl

import com.arthuralexandryan.footballquiz.data.repository.ProfileProgressSnapshot
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.progression.StageProgress

internal fun ProfileProgressSnapshot.toStageProgress(): List<StageProgress> = listOf(
    StageProgress(ProgressionStageId.TOP5_FRANCE_COMPLETE, franceAnswered, franceTotal),
    StageProgress(ProgressionStageId.TOP5_GERMANY_COMPLETE, germanyAnswered, germanyTotal),
    StageProgress(ProgressionStageId.TOP5_ITALY_COMPLETE, italyAnswered, italyTotal),
    StageProgress(ProgressionStageId.TOP5_ENGLAND_COMPLETE, englandAnswered, englandTotal),
    StageProgress(ProgressionStageId.TOP5_SPAIN_COMPLETE, spainAnswered, spainTotal),
    StageProgress(ProgressionStageId.TOP5_COMPLETE, top5Answered, top5Total),
    StageProgress(ProgressionStageId.UEFA_SUPER_CUP_COMPLETE, superCupAnswered, superCupTotal),
    StageProgress(ProgressionStageId.UEFA_EUROPA_LEAGUE_COMPLETE, europaLeagueAnswered, europaLeagueTotal),
    StageProgress(ProgressionStageId.UEFA_CHAMPIONS_LEAGUE_COMPLETE, championsLeagueAnswered, championsLeagueTotal),
    StageProgress(
        ProgressionStageId.UEFA_EUROPEAN_CHAMPIONSHIP_COMPLETE,
        europeanChampionshipAnswered,
        europeanChampionshipTotal
    ),
    StageProgress(ProgressionStageId.UEFA_COMPLETE, uefaAnswered, uefaTotal),
    StageProgress(ProgressionStageId.WORLD_COMPLETE, worldAnswered, worldTotal),
    StageProgress(ProgressionStageId.VERSUS_VSRM_COMPLETE, vsrmAnswered, vsrmTotal),
    StageProgress(ProgressionStageId.VERSUS_VSRB_COMPLETE, vsrbAnswered, vsrbTotal),
    StageProgress(ProgressionStageId.VERSUS_COMPLETE, versusAnswered, versusTotal),
    StageProgress(ProgressionStageId.APP_COMPLETE, totalAnswered, totalQuestions)
)
