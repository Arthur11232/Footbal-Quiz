package com.arthuralexandryan.footballquiz.utils

import android.os.Build
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.arthuralexandryan.footballquiz.R

object SystemBarStyleHelper {

    fun applySolidColor(
        fragment: Fragment,
        @ColorInt color: Int,
        applyInsetsToRoot: Boolean = true,
        lightSystemBarIcons: Boolean = false
    ) {
        applyColors(
            fragment = fragment,
            statusBarColor = color,
            navigationBarColor = color,
            rootBackgroundColor = color,
            applyInsetsToRoot = applyInsetsToRoot,
            lightSystemBarIcons = lightSystemBarIcons
        )
    }

    fun applySolidColorRes(
        fragment: Fragment,
        @ColorRes colorResId: Int,
        applyInsetsToRoot: Boolean = true,
        lightSystemBarIcons: Boolean = false
    ) {
        val context = fragment.context ?: return
        applySolidColor(
            fragment = fragment,
            color = ContextCompat.getColor(context, colorResId),
            applyInsetsToRoot = applyInsetsToRoot,
            lightSystemBarIcons = lightSystemBarIcons
        )
    }

    fun applySampledDrawable(
        fragment: Fragment,
        @DrawableRes drawableResId: Int,
        @ColorInt fallbackColor: Int = Color.BLACK,
        matchNavigationToStatus: Boolean = false,
        applyDrawableToRoot: Boolean = true,
        applyInsetsToRoot: Boolean = true,
        lightSystemBarIcons: Boolean = false
    ) {
        if (applyDrawableToRoot) {
            fragment.view?.setBackgroundResource(drawableResId)
        }

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
            applyInsetsToRoot = applyInsetsToRoot,
            lightSystemBarIcons = lightSystemBarIcons
        )
    }

    private fun applyColors(
        fragment: Fragment,
        @ColorInt statusBarColor: Int,
        @ColorInt navigationBarColor: Int,
        @ColorInt rootBackgroundColor: Int? = null,
        applyInsetsToRoot: Boolean = true,
        lightSystemBarIcons: Boolean
    ) {
        val window = fragment.activity?.window ?: return
        val root = fragment.view ?: return

        if (applyInsetsToRoot) {
            applyInsetsToRoot(root)
        }
        rootBackgroundColor?.let(root::setBackgroundColor)

        if (Build.VERSION.SDK_INT >= 35) {
            // Android 15+ ignores statusBarColor for edge-to-edge apps.
            // Keep system bars transparent so the fragment background can
            // extend behind them, and only style the icons here.
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = navigationBarColor
            if (Build.VERSION.SDK_INT >= 29) {
                window.isNavigationBarContrastEnforced = false
            }
        } else {
            window.statusBarColor = statusBarColor
            window.navigationBarColor = navigationBarColor
        }

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightSystemBarIcons
            isAppearanceLightNavigationBars = lightSystemBarIcons
        }
    }

    fun applySystemBarPadding(
        view: android.view.View,
        applyLeft: Boolean = false,
        applyTop: Boolean = false,
        applyRight: Boolean = false,
        applyBottom: Boolean = false
    ) {
        val initialPadding = (view.getTag(R.id.system_bar_initial_padding) as? IntArray)
            ?: intArrayOf(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
                .also { view.setTag(R.id.system_bar_initial_padding, it) }

        ViewCompat.setOnApplyWindowInsetsListener(view) { target, insets ->
            val systemBars = insets.getInsets(Type.systemBars())
            target.setPadding(
                initialPadding[0] + if (applyLeft) systemBars.left else 0,
                initialPadding[1] + if (applyTop) systemBars.top else 0,
                initialPadding[2] + if (applyRight) systemBars.right else 0,
                initialPadding[3] + if (applyBottom) systemBars.bottom else 0
            )
            insets
        }
        ViewCompat.requestApplyInsets(view)
    }

    private fun applyInsetsToRoot(root: android.view.View) {
        applySystemBarPadding(
            view = root,
            applyLeft = true,
            applyTop = true,
            applyRight = true,
            applyBottom = true
        )
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
