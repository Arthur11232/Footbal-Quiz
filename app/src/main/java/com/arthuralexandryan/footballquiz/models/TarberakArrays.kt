package com.arthuralexandryan.footballquiz.models

import android.content.Context
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.models.placeDescription.DescriptionModel
import com.arthuralexandryan.footballquiz.models.primeryColorModel.ColorModel

object TarberakArrays {

    @JvmField val top5_image = intArrayOf(
        R.drawable.france_flag, R.drawable.german_flag, R.drawable.italy_flag,
        R.drawable.england_flag, R.drawable.spain_flag_gold
    )

    @JvmField val top5_bgs = intArrayOf(
        R.drawable.france_bg, R.drawable.german_bg, R.drawable.italy_bg,
        R.drawable.england_bg, R.drawable.spain_bg
    )

    @JvmField val top5_text = arrayOf("fransia", "germania", "italia", "anglia", "ispania")

    @JvmField val ufa_image = intArrayOf(
        R.drawable.super_cup_logo, R.drawable.europe_league_logo,
        R.drawable.champions_league, R.drawable.uefa_euro_logo
    )

    @JvmField val ufa_bgs = intArrayOf(
        R.drawable.super_cup_bg, R.drawable.eauropeleague_bg,
        R.drawable.champions_league_bg, R.drawable.euro_bg
    )

    @JvmField val ufa_text = arrayOf("supercup", "evropaliga", "ligachemp", "kubki")

    @JvmField val world_image = intArrayOf(R.drawable.fifa_logo)
    @JvmField val world_bg = intArrayOf(R.drawable.fifa_bg)
    @JvmField val world_text = arrayOf("kubok_mira")

    @JvmField val rm_image = intArrayOf(R.drawable.ronaldo, R.drawable.messi)
    @JvmField val rb_image = intArrayOf(R.drawable.real_madrid_logo, R.drawable.barcelona_logo)
    @JvmField val rm_bg = intArrayOf(R.drawable.vs_bg)
    @JvmField val rm_text = arrayOf("ronmes")

    @JvmField val leftPlaceTop5 = intArrayOf(
        0, R.drawable.france_left, R.drawable.german_left, R.drawable.italy_left, R.drawable.england_left
    )

    @JvmField val rightPlaceTop5 = intArrayOf(
        R.drawable.german_right, R.drawable.itali_right, R.drawable.england_right, R.drawable.spain_right, 0
    )

    @JvmField val leftPlaceUFA = intArrayOf(
        0, R.drawable.supercup_left, R.drawable.europ_left, R.drawable.champ_left
    )

    @JvmField val rightPlaceUFA = intArrayOf(
        R.drawable.europ_right, R.drawable.champ_right_arrow, R.drawable.euro_right_arrow, 0
    )

    fun getPrimaryColorTop5() = listOf(
        ColorModel(255, 28, 53, 127),
        ColorModel(255, 163, 27, 31),
        ColorModel(255, 28, 53, 127),
        ColorModel(255, 192, 14, 75),
        ColorModel(255, 166, 27, 31)
    )

    fun getPrimaryColorUEFA() = listOf(
        ColorModel(255, 0, 69, 32),
        ColorModel(255, 9, 18, 14),
        ColorModel(255, 11, 19, 54),
        ColorModel(255, 17, 92, 107)
    )

    fun getPrimaryColorWorld() = ColorModel(255, 155, 156, 156)

    fun getTop5Descriptions(context: Context) = listOf(
        getFranceDes(context), getGermanyDes(context), getItalyDes(context),
        getEnglandDes(context), getSpainDes(context)
    )

    private fun getFranceDes(context: Context) = DescriptionModel(
        context.getString(R.string.franc_text_position),
        context.getString(R.string.franc_text_confederation),
        context.getString(R.string.franc_text_federation),
        context.getString(R.string.franc_text_league),
        context.getString(R.string.franc_text_date)
    )

    private fun getGermanyDes(context: Context) = DescriptionModel(
        context.getString(R.string.germany_text_position),
        context.getString(R.string.germany_text_confederation),
        context.getString(R.string.germany_text_federation),
        context.getString(R.string.germany_text_league),
        context.getString(R.string.germany_text_date)
    )

    private fun getItalyDes(context: Context) = DescriptionModel(
        context.getString(R.string.italy_text_position),
        context.getString(R.string.italy_text_confederation),
        context.getString(R.string.italy_text_federation),
        context.getString(R.string.italy_text_league),
        context.getString(R.string.italy_text_date)
    )

    private fun getEnglandDes(context: Context) = DescriptionModel(
        context.getString(R.string.england_text_position),
        context.getString(R.string.england_text_confederation),
        context.getString(R.string.england_text_federation),
        context.getString(R.string.england_text_league),
        context.getString(R.string.england_text_date)
    )

    private fun getSpainDes(context: Context) = DescriptionModel(
        context.getString(R.string.spain_text_position),
        context.getString(R.string.spain_text_confederation),
        context.getString(R.string.spain_text_federation),
        context.getString(R.string.spain_text_league),
        context.getString(R.string.spain_text_date)
    )

    fun getBFicons() = listOf(R.drawable.ron_messi, R.drawable.real_barce, R.drawable.dinamo_spartak)
}
