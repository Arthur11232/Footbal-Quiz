package com.arthuralexandryan.footballquiz.presentation.achievement

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId

data class CertificateTheme(
    @param:ColorRes val screenColorResId: Int,
    @param:StringRes val captionResId: Int,
    @param:StringRes val subheadlineResId: Int,
    @param:ColorInt val cardFillColor: Int,
    @param:ColorInt val cardStrokeColor: Int,
    @param:ColorInt val primaryTextColor: Int,
    @param:ColorInt val secondaryTextColor: Int,
    @param:ColorInt val accentColor: Int,
    @param:ColorInt val emphasisColor: Int,
    @param:ColorInt val stageChipFillColor: Int,
    @param:ColorInt val stageChipTextColor: Int,
    @param:ColorInt val buttonFillColor: Int,
    @param:ColorInt val buttonTextColor: Int,
    val isMajorAchievement: Boolean
)

object CertificateThemeMapper {

    fun themeFor(stageId: ProgressionStageId?): CertificateTheme {
        return when (stageId) {
            ProgressionStageId.TOP5_FRANCE_COMPLETE -> franceTheme()
            ProgressionStageId.TOP5_GERMANY_COMPLETE -> germanyTheme()
            ProgressionStageId.TOP5_ITALY_COMPLETE -> italyTheme()
            ProgressionStageId.TOP5_ENGLAND_COMPLETE -> englandTheme()
            ProgressionStageId.TOP5_SPAIN_COMPLETE -> spainTheme()
            ProgressionStageId.TOP5_COMPLETE -> top5Theme()
            ProgressionStageId.UEFA_SUPER_CUP_COMPLETE -> superCupTheme()
            ProgressionStageId.UEFA_EUROPA_LEAGUE_COMPLETE -> europaTheme()
            ProgressionStageId.UEFA_CHAMPIONS_LEAGUE_COMPLETE -> championsTheme()
            ProgressionStageId.UEFA_EUROPEAN_CHAMPIONSHIP_COMPLETE -> euroTheme()
            ProgressionStageId.UEFA_COMPLETE -> uefaTheme()
            ProgressionStageId.WORLD_COMPLETE -> worldTheme()
            ProgressionStageId.VERSUS_VSRM_COMPLETE,
            ProgressionStageId.VERSUS_VSRB_COMPLETE -> versusStageTheme()
            ProgressionStageId.VERSUS_COMPLETE -> versusTheme()
            ProgressionStageId.APP_COMPLETE,
            null -> appCompleteTheme()
        }
    }

    private fun franceTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorFranc,
        captionResId = R.string.certificate_caption_top5_stage,
        subheadlineResId = R.string.certificate_subheadline_top5_stage,
        cardFillColor = color("#13204A"),
        cardStrokeColor = color("#D9515A"),
        primaryTextColor = color("#F7F2E8"),
        secondaryTextColor = color("#D8E0E9"),
        accentColor = color("#D9515A"),
        emphasisColor = color("#F0C77D"),
        stageChipFillColor = color("#F7D68D"),
        stageChipTextColor = color("#0B1336"),
        buttonFillColor = color("#F0C77D"),
        buttonTextColor = color("#0B1336"),
        isMajorAchievement = false
    )

    private fun germanyTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorGermany,
        captionResId = R.string.certificate_caption_top5_stage,
        subheadlineResId = R.string.certificate_subheadline_top5_stage,
        cardFillColor = color("#491315"),
        cardStrokeColor = color("#1F1B1B"),
        primaryTextColor = color("#FFF5EE"),
        secondaryTextColor = color("#E3D6CF"),
        accentColor = color("#1F1B1B"),
        emphasisColor = color("#F2C14E"),
        stageChipFillColor = color("#F2C14E"),
        stageChipTextColor = color("#2A0D0F"),
        buttonFillColor = color("#F2C14E"),
        buttonTextColor = color("#2A0D0F"),
        isMajorAchievement = false
    )

    private fun italyTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorItaly,
        captionResId = R.string.certificate_caption_top5_stage,
        subheadlineResId = R.string.certificate_subheadline_top5_stage,
        cardFillColor = color("#16306B"),
        cardStrokeColor = color("#2E8B57"),
        primaryTextColor = color("#F9F5EB"),
        secondaryTextColor = color("#D6E0EF"),
        accentColor = color("#2E8B57"),
        emphasisColor = color("#E8C96A"),
        stageChipFillColor = color("#E8C96A"),
        stageChipTextColor = color("#10224F"),
        buttonFillColor = color("#E8C96A"),
        buttonTextColor = color("#10224F"),
        isMajorAchievement = false
    )

    private fun englandTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorEngland,
        captionResId = R.string.certificate_caption_top5_stage,
        subheadlineResId = R.string.certificate_subheadline_top5_stage,
        cardFillColor = color("#5A1833"),
        cardStrokeColor = color("#A62557"),
        primaryTextColor = color("#FFF7F0"),
        secondaryTextColor = color("#E7D6D8"),
        accentColor = color("#A62557"),
        emphasisColor = color("#EFC36A"),
        stageChipFillColor = color("#EFC36A"),
        stageChipTextColor = color("#4A1024"),
        buttonFillColor = color("#EFC36A"),
        buttonTextColor = color("#4A1024"),
        isMajorAchievement = false
    )

    private fun spainTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorSpain,
        captionResId = R.string.certificate_caption_top5_stage,
        subheadlineResId = R.string.certificate_subheadline_top5_stage,
        cardFillColor = color("#591618"),
        cardStrokeColor = color("#8B1E22"),
        primaryTextColor = color("#FFF7ED"),
        secondaryTextColor = color("#E8DCCB"),
        accentColor = color("#8B1E22"),
        emphasisColor = color("#F0B323"),
        stageChipFillColor = color("#F0B323"),
        stageChipTextColor = color("#4B130F"),
        buttonFillColor = color("#F0B323"),
        buttonTextColor = color("#4B130F"),
        isMajorAchievement = false
    )

    private fun top5Theme() = CertificateTheme(
        screenColorResId = R.color.fq_colorBlack,
        captionResId = R.string.certificate_caption_top5_complete,
        subheadlineResId = R.string.certificate_subheadline_top5_complete,
        cardFillColor = color("#1D1F24"),
        cardStrokeColor = color("#C38A1E"),
        primaryTextColor = color("#FFF8EE"),
        secondaryTextColor = color("#DCD4C8"),
        accentColor = color("#C38A1E"),
        emphasisColor = color("#F5C96A"),
        stageChipFillColor = color("#F5C96A"),
        stageChipTextColor = color("#231A0C"),
        buttonFillColor = color("#F5C96A"),
        buttonTextColor = color("#231A0C"),
        isMajorAchievement = true
    )

    private fun superCupTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorSuperCup,
        captionResId = R.string.certificate_caption_uefa_stage,
        subheadlineResId = R.string.certificate_subheadline_uefa_stage,
        cardFillColor = color("#113624"),
        cardStrokeColor = color("#2D8A52"),
        primaryTextColor = color("#F4F7EE"),
        secondaryTextColor = color("#C9D9CC"),
        accentColor = color("#2D8A52"),
        emphasisColor = color("#E7C66D"),
        stageChipFillColor = color("#E7C66D"),
        stageChipTextColor = color("#0D2416"),
        buttonFillColor = color("#E7C66D"),
        buttonTextColor = color("#0D2416"),
        isMajorAchievement = false
    )

    private fun europaTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorEuropa,
        captionResId = R.string.certificate_caption_uefa_stage,
        subheadlineResId = R.string.certificate_subheadline_uefa_stage,
        cardFillColor = color("#151B18"),
        cardStrokeColor = color("#E67E22"),
        primaryTextColor = color("#F4F0E8"),
        secondaryTextColor = color("#CEC7B8"),
        accentColor = color("#E67E22"),
        emphasisColor = color("#F4B860"),
        stageChipFillColor = color("#F4B860"),
        stageChipTextColor = color("#1B140E"),
        buttonFillColor = color("#F4B860"),
        buttonTextColor = color("#1B140E"),
        isMajorAchievement = false
    )

    private fun championsTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorChampions,
        captionResId = R.string.certificate_caption_uefa_stage,
        subheadlineResId = R.string.certificate_subheadline_uefa_stage,
        cardFillColor = color("#14204B"),
        cardStrokeColor = color("#4D79FF"),
        primaryTextColor = color("#F5F7FF"),
        secondaryTextColor = color("#D0DBEF"),
        accentColor = color("#4D79FF"),
        emphasisColor = color("#FFD166"),
        stageChipFillColor = color("#FFD166"),
        stageChipTextColor = color("#101D49"),
        buttonFillColor = color("#FFD166"),
        buttonTextColor = color("#101D49"),
        isMajorAchievement = false
    )

    private fun euroTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorEuro,
        captionResId = R.string.certificate_caption_uefa_stage,
        subheadlineResId = R.string.certificate_subheadline_uefa_stage,
        cardFillColor = color("#0F4048"),
        cardStrokeColor = color("#4FD3D7"),
        primaryTextColor = color("#F1FBFB"),
        secondaryTextColor = color("#C7E6E8"),
        accentColor = color("#4FD3D7"),
        emphasisColor = color("#FFD166"),
        stageChipFillColor = color("#FFD166"),
        stageChipTextColor = color("#083039"),
        buttonFillColor = color("#FFD166"),
        buttonTextColor = color("#083039"),
        isMajorAchievement = false
    )

    private fun uefaTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorChampions,
        captionResId = R.string.certificate_caption_uefa_complete,
        subheadlineResId = R.string.certificate_subheadline_uefa_complete,
        cardFillColor = color("#141E46"),
        cardStrokeColor = color("#54B8FF"),
        primaryTextColor = color("#F3F7FF"),
        secondaryTextColor = color("#D0DBEF"),
        accentColor = color("#54B8FF"),
        emphasisColor = color("#FFD166"),
        stageChipFillColor = color("#FFD166"),
        stageChipTextColor = color("#102049"),
        buttonFillColor = color("#FFD166"),
        buttonTextColor = color("#102049"),
        isMajorAchievement = true
    )

    private fun worldTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorWorld,
        captionResId = R.string.certificate_caption_world_complete,
        subheadlineResId = R.string.certificate_subheadline_world_complete,
        cardFillColor = color("#2E343B"),
        cardStrokeColor = color("#7FB3D5"),
        primaryTextColor = color("#F5F7FA"),
        secondaryTextColor = color("#D4DBE3"),
        accentColor = color("#7FB3D5"),
        emphasisColor = color("#F4C56A"),
        stageChipFillColor = color("#F4C56A"),
        stageChipTextColor = color("#1A232D"),
        buttonFillColor = color("#F4C56A"),
        buttonTextColor = color("#1A232D"),
        isMajorAchievement = true
    )

    private fun versusStageTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorAccent,
        captionResId = R.string.certificate_caption_versus_stage,
        subheadlineResId = R.string.certificate_subheadline_versus_stage,
        cardFillColor = color("#3A1022"),
        cardStrokeColor = color("#F45B69"),
        primaryTextColor = color("#FFF6F8"),
        secondaryTextColor = color("#E6CCD6"),
        accentColor = color("#F45B69"),
        emphasisColor = color("#FFD166"),
        stageChipFillColor = color("#FFD166"),
        stageChipTextColor = color("#3A1022"),
        buttonFillColor = color("#FFD166"),
        buttonTextColor = color("#3A1022"),
        isMajorAchievement = false
    )

    private fun versusTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorAccent,
        captionResId = R.string.certificate_caption_versus_complete,
        subheadlineResId = R.string.certificate_subheadline_versus_complete,
        cardFillColor = color("#3A1022"),
        cardStrokeColor = color("#F45B69"),
        primaryTextColor = color("#FFF6F8"),
        secondaryTextColor = color("#E6CCD6"),
        accentColor = color("#F45B69"),
        emphasisColor = color("#FFD166"),
        stageChipFillColor = color("#FFD166"),
        stageChipTextColor = color("#3A1022"),
        buttonFillColor = color("#FFD166"),
        buttonTextColor = color("#3A1022"),
        isMajorAchievement = true
    )

    private fun appCompleteTheme() = CertificateTheme(
        screenColorResId = R.color.fq_colorPrimary,
        captionResId = R.string.certificate_caption_app_complete,
        subheadlineResId = R.string.certificate_subheadline_app_complete,
        cardFillColor = color("#0F4024"),
        cardStrokeColor = color("#3BC06A"),
        primaryTextColor = color("#F2FFF6"),
        secondaryTextColor = color("#D0E8D8"),
        accentColor = color("#3BC06A"),
        emphasisColor = color("#F8C85A"),
        stageChipFillColor = color("#F8C85A"),
        stageChipTextColor = color("#10331D"),
        buttonFillColor = color("#F8C85A"),
        buttonTextColor = color("#10331D"),
        isMajorAchievement = true
    )

    @ColorInt
    private fun color(value: String): Int = Color.parseColor(value)
}
