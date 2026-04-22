package com.arthuralexandryan.footballquiz.utils

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

object SystemBarStyleHelper {

    fun applySolidColor(
        fragment: Fragment,
        @ColorInt color: Int,
        lightSystemBarIcons: Boolean = false
    ) {
        applyColors(
            fragment = fragment,
            statusBarColor = color,
            navigationBarColor = color,
            lightSystemBarIcons = lightSystemBarIcons
        )
    }

    fun applySolidColorRes(
        fragment: Fragment,
        @ColorRes colorResId: Int,
        lightSystemBarIcons: Boolean = false
    ) {
        val context = fragment.context ?: return
        applySolidColor(
            fragment = fragment,
            color = ContextCompat.getColor(context, colorResId),
            lightSystemBarIcons = lightSystemBarIcons
        )
    }

    fun applySampledDrawable(
        fragment: Fragment,
        @DrawableRes drawableResId: Int,
        @ColorInt fallbackColor: Int = Color.BLACK,
        matchNavigationToStatus: Boolean = false,
        lightSystemBarIcons: Boolean = false
    ) {
        val statusBarColor = resolveDrawableStripColor(
            fragment = fragment,
            drawableResId = drawableResId,
            sampleFromTop = true,
            fallbackColor = fallbackColor
        )
        val navigationBarColor = if (matchNavigationToStatus) {
            statusBarColor
        } else {
            resolveDrawableStripColor(
                fragment = fragment,
                drawableResId = drawableResId,
                sampleFromTop = false,
                fallbackColor = fallbackColor
            )
        }

        applyColors(
            fragment = fragment,
            statusBarColor = statusBarColor,
            navigationBarColor = navigationBarColor,
            lightSystemBarIcons = lightSystemBarIcons
        )
    }

    private fun applyColors(
        fragment: Fragment,
        @ColorInt statusBarColor: Int,
        @ColorInt navigationBarColor: Int,
        lightSystemBarIcons: Boolean
    ) {
        val window = fragment.activity?.window ?: return
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightSystemBarIcons
            isAppearanceLightNavigationBars = lightSystemBarIcons
        }
    }

    @ColorInt
    private fun resolveDrawableStripColor(
        fragment: Fragment,
        @DrawableRes drawableResId: Int,
        sampleFromTop: Boolean,
        @ColorInt fallbackColor: Int
    ): Int {
        val resources = fragment.resources
        val bitmap = BitmapFactory.decodeResource(resources, drawableResId) ?: return fallbackColor

        return try {
            val density = resources.displayMetrics.density
            val stripHeight = minOf(bitmap.height, maxOf(1, (24 * density).toInt()))
            val startY = if (sampleFromTop) {
                0
            } else {
                maxOf(0, bitmap.height - stripHeight)
            }
            val endYExclusive = minOf(bitmap.height, startY + stripHeight)
            val stepX = maxOf(1, bitmap.width / 48)
            val stepY = maxOf(1, stripHeight / 6)

            var red = 0L
            var green = 0L
            var blue = 0L
            var count = 0L

            var y = startY
            while (y < endYExclusive) {
                var x = 0
                while (x < bitmap.width) {
                    val pixel = bitmap.getPixel(x, y)
                    red += Color.red(pixel)
                    green += Color.green(pixel)
                    blue += Color.blue(pixel)
                    count++
                    x += stepX
                }
                y += stepY
            }

            if (count == 0L) {
                fallbackColor
            } else {
                Color.rgb((red / count).toInt(), (green / count).toInt(), (blue / count).toInt())
            }
        } finally {
            bitmap.recycle()
        }
    }
}
