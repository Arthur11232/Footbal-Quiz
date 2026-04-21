package com.arthuralexandryan.footballquiz.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.RectF
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs
import kotlin.math.max
import java.util.Random

object BallFlightAnimationManager {

    data class FlightSpec(
        val startCenterX: Float,
        val startCenterY: Float,
        val targetRect: RectF,
        val durationMinMs: Long,
        val durationMaxMs: Long,
        val finalScaleMin: Float,
        val finalScaleMax: Float,
        val turnsMin: Float,
        val turnsMax: Float,
        val controlPointMin: Float,
        val controlPointMax: Float,
        val controlXOffsetMin: Float,
        val controlXOffsetMax: Float,
        val arcHeightBaseMin: Float,
        val arcHeightDistanceFactor: Float,
        val arcHeightExtraMin: Float,
        val arcHeightExtraMax: Float,
        val interpolator: TimeInterpolator = DecelerateInterpolator()
    )

    data class FlightPlan(
        val targetCenterX: Float,
        val targetCenterY: Float,
        val controlX: Float,
        val controlY: Float,
        val finalScale: Float,
        val rotationEnd: Float,
        val durationMs: Long
    )

    fun createTargetRect(centerX: Float, centerY: Float, spreadX: Float, spreadY: Float): RectF {
        return RectF(
            centerX - spreadX,
            centerY - spreadY,
            centerX + spreadX,
            centerY + spreadY
        )
    }

    fun buildPlan(spec: FlightSpec, random: Random = Random()): FlightPlan {
        val targetCenterX = randomRange(random, spec.targetRect.left, spec.targetRect.right)
        val targetCenterY = randomRange(random, spec.targetRect.top, spec.targetRect.bottom)
        val controlProgress = randomRange(random, spec.controlPointMin, spec.controlPointMax)
        val controlX =
            spec.startCenterX +
                (targetCenterX - spec.startCenterX) * controlProgress +
                randomRange(random, spec.controlXOffsetMin, spec.controlXOffsetMax)
        val arcHeight =
            max(
                spec.arcHeightBaseMin,
                abs(targetCenterX - spec.startCenterX) * spec.arcHeightDistanceFactor
            ) + randomRange(random, spec.arcHeightExtraMin, spec.arcHeightExtraMax)
        val controlY = minOf(spec.startCenterY, targetCenterY) - arcHeight
        val turns = randomRange(random, spec.turnsMin, spec.turnsMax) * 360f
        val rotationEnd = if (random.nextBoolean()) turns else -turns
        val finalScale = randomRange(random, spec.finalScaleMin, spec.finalScaleMax)
        val durationMs = randomRange(random, spec.durationMinMs, spec.durationMaxMs)

        return FlightPlan(
            targetCenterX = targetCenterX,
            targetCenterY = targetCenterY,
            controlX = controlX,
            controlY = controlY,
            finalScale = finalScale,
            rotationEnd = rotationEnd,
            durationMs = durationMs
        )
    }

    fun createAnimator(view: View, spec: FlightSpec, random: Random = Random()): AnimatorSet {
        return createAnimator(view, buildPlan(spec, random), spec.interpolator)
    }

    fun createAnimator(
        view: View,
        plan: FlightPlan,
        interpolator: TimeInterpolator = DecelerateInterpolator()
    ): AnimatorSet {
        view.scaleX = 1f
        view.scaleY = 1f
        view.rotation = 0f

        val startCenterX = view.x + view.width / 2f
        val startCenterY = view.y + view.height / 2f

        val flightAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = plan.durationMs
            this.interpolator = interpolator
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                val inverse = 1f - progress
                val currentCenterX =
                    inverse * inverse * startCenterX +
                        2f * inverse * progress * plan.controlX +
                        progress * progress * plan.targetCenterX
                val currentCenterY =
                    inverse * inverse * startCenterY +
                        2f * inverse * progress * plan.controlY +
                        progress * progress * plan.targetCenterY
                view.x = currentCenterX - view.width / 2f
                view.y = currentCenterY - view.height / 2f
            }
        }

        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, plan.finalScale).apply {
            duration = plan.durationMs
        }
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, plan.finalScale).apply {
            duration = plan.durationMs
        }
        val rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, plan.rotationEnd).apply {
            duration = plan.durationMs
        }

        return AnimatorSet().apply {
            playTogether(flightAnimator, scaleDownX, scaleDownY, rotate)
        }
    }

    private fun randomRange(random: Random, min: Float, max: Float): Float {
        if (min == max) return min
        return min + random.nextFloat() * (max - min)
    }

    private fun randomRange(random: Random, min: Long, max: Long): Long {
        if (min == max) return min
        val fraction = random.nextFloat()
        return (min + ((max - min) * fraction)).toLong()
    }
}
