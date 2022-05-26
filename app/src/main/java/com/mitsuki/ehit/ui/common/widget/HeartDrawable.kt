package com.mitsuki.ehit.ui.common.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

class HeartDrawable : Drawable(), Animatable {

    private val mCoreAnimator: Animator
    private val mEffect: Effect = Effect()

    companion object {
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
        private const val ANIMATION_DURATION = 700

    }

    init {
        val effect: Effect = mEffect
        mCoreAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
            animator.interpolator = LINEAR_INTERPOLATOR
            animator.duration = ANIMATION_DURATION.toLong()
            animator.addUpdateListener { animation ->
                effect.interpolatedTime = animation.animatedValue as Float
                invalidateSelf()
            }
        }
    }

    var size: Float
        get() = mEffect.size
        set(value) {
            if (value != mEffect.size) {
                mEffect.size = value
                invalidateSelf()
            }
        }

    var heartColor: Int
        get() = mEffect.heartColor
        set(value) {
            if (value != mEffect.heartColor) {
                mEffect.heartColor = value
                invalidateSelf()
            }
        }

    var heartHintColor :Int
        get() = mEffect.heartHintColor
        set(value) {
            if (value != mEffect.heartHintColor) {
                mEffect.heartHintColor = value
                invalidateSelf()
            }
        }

    var heartHintStroke :Float
        get() = mEffect.heartHintStroke
        set(value) {
            if (value != mEffect.heartHintStroke) {
                mEffect.heartHintStroke = value
                invalidateSelf()
            }
        }

    fun setChecked(check: Boolean, withAnimator: Boolean = true) {
        if (!check != mEffect.fixed) {
            mEffect.fixed = !check
            if (check && withAnimator) {
                start()
            } else {
                mCoreAnimator.cancel()
                if (check) {
                    mEffect.interpolatedTime = 1.0f
                }
                invalidateSelf()
            }
        }
    }

    override fun start() {
        mCoreAnimator.cancel()
        mCoreAnimator.start()
    }

    override fun stop() {
        mCoreAnimator.cancel()
    }

    override fun isRunning(): Boolean {
        return mCoreAnimator.isRunning
    }


    override fun setAlpha(alpha: Int) {
        mEffect.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }


    override fun draw(canvas: Canvas) {
        val bounds = bounds
        canvas.save()
        mEffect.draw(canvas, bounds)
        canvas.restore()

    }


    private class Effect {
        var fixed = true

        var alpha = 255
        var heartColor: Int = 0xffeb3b89.toInt()
        var heartHintColor: Int = 0xff222222.toInt()
        var heartHintStroke: Float = 2f

        var circleStartColor: Int = 0xffd54ed8.toInt()
        var circleEndColor: Int = 0xffcfa6f0.toInt()
        var particleSaturation = 0.7f

        var size: Float = -1f

        val particleColor = Array<Float>(PARTICLE_COUNT) { 0f }

        fun size(bounds: Rect): Float {
            return if (size < 0) {
                bounds.width().coerceAtMost(bounds.height()).toFloat()
            } else {
                size
            }
        }

        private val mPaint = Paint().apply { isAntiAlias = true }
        private val mPath = Path()

        var interpolatedTime = 0F
            set(value) {
                val real = value.coerceIn(0f, 1f)
                if (real != field) {
                    field = real
                }
            }

        companion object {
            const val CIRCLE_SIZE = 0.5f
            const val HEART_SIZE = 0.34f
            const val HEART_BEAT_SCALE = 0.1f
            const val PARTICLE_COUNT = 7
            const val PARTICLE_SIZE = 0.015f
            const val PARTICLE_DEFLECTION_A = 12
            const val PARTICLE_DEFLECTION_B = 20

            const val PARTICLE_START_SCOPE_A = 0.48f
            const val PARTICLE_MIDDLE_SCOPE_A = 0.5f
            const val PARTICLE_END_SCOPE_A = 0.75f
            const val PARTICLE_SIZE_SCALE_A = 0.5f

            const val PARTICLE_START_SCOPE_B = 0.40f
            const val PARTICLE_MIDDLE_SCOPE_B = 0.5f
            const val PARTICLE_END_SCOPE_B = 0.9f
            const val PARTICLE_SIZE_SCALE_B = 3f

        }

        fun draw(c: Canvas, bounds: Rect) {
            mPaint.alpha = alpha
            drawParticle(c, bounds)
            drawCircle(c, bounds)
            drawHeart(c, bounds)
        }

        private fun drawHeart(canvas: Canvas, bounds: Rect) {
            mPath.reset()

            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()
            val heartSize = size(bounds) * HEART_SIZE
            if (fixed) {
                mPaint.style = Paint.Style.STROKE
                mPaint.strokeWidth = heartHintStroke
                mPaint.color = heartHintColor
                mPath.heart(
                    centerX,
                    centerY,
                    heartSize - heartHintStroke
                )
                canvas.drawPath(mPath, mPaint)
            } else {
                mPaint.style = Paint.Style.FILL
                mPaint.color = heartColor
                val node1 = 0.25f
                val node2 = 0.5f
                val node3 = 0.7f
                when (interpolatedTime) {
                    in node1..node2 -> {
                        val size = interpolatedTime.realPercent(node1, node2) * heartSize
                        mPath.heart(centerX, centerY, size)
                        canvas.drawPath(mPath, mPaint)
                    }
                    in node2..node3 -> {
                        val size = heartSize *
                                (1 - interpolatedTime.realPercent(node2, node3) * HEART_BEAT_SCALE)
                        mPath.heart(centerX, centerY, size)
                        canvas.drawPath(mPath, mPaint)
                    }
                    in node3..1f -> {
                        val size = heartSize * (1 - (1 -
                                interpolatedTime.realPercent(node3, 1f)) * HEART_BEAT_SCALE)
                        mPath.heart(centerX, centerY, size)
                        canvas.drawPath(mPath, mPaint)
                    }
                }
            }
        }

        private fun drawCircle(canvas: Canvas, bounds: Rect) {
            if (fixed) return
            mPath.reset()

            val node1 = 0.25f
            val node2 = 0.5f
            val circleSize = size(bounds) * CIRCLE_SIZE / 2
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()

            when (interpolatedTime) {
                in 0f..node1 -> {
                    val realPercent = interpolatedTime.realPercent(0f, node1)
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = colorChange(
                        realPercent,
                        circleStartColor,
                        circleEndColor
                    )
                    val size = realPercent * circleSize
                    mPath.addCircle(centerX, centerY, size, Path.Direction.CW)
                    canvas.drawPath(mPath, mPaint)
                }
                in node1..node2 -> {
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = circleEndColor
                    mPath.addCircle(centerX, centerY, circleSize, Path.Direction.CW)
                    val size = interpolatedTime.realPercent(node1, node2) * circleSize
                    mPath.addCircle(centerX, centerY, size, Path.Direction.CCW)
                    canvas.drawPath(mPath, mPaint)
                }
            }
        }

        private fun drawParticle(canvas: Canvas, bounds: Rect) {
            if (fixed) return
            mPaint.style = Paint.Style.FILL

            val node1 = 0.15f
            val node2 = 0.5f
            val node3 = 0.7f
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()

            val currentSize = size(bounds)
            val particleSize = currentSize * PARTICLE_SIZE / 2

            when (interpolatedTime) {
                0f -> {
                    for (i in 0 until PARTICLE_COUNT) {
                        particleColor[i] = (0..1530).random().toFloat() / 1530f
                    }
                }
                in 0.1f..node1 -> {
                    val realPercent = interpolatedTime.realPercent(0f, node1)

                    val particleScopeRadiusA = (PARTICLE_START_SCOPE_A * currentSize + realPercent *
                            (PARTICLE_MIDDLE_SCOPE_A - PARTICLE_START_SCOPE_A) * currentSize) / 2
                    val particleScopeRadiusB = (PARTICLE_START_SCOPE_B * currentSize + realPercent *
                            (PARTICLE_MIDDLE_SCOPE_B - PARTICLE_START_SCOPE_B) * currentSize) / 2
                    for (i in 0 until PARTICLE_COUNT) {
                        mPaint.color = pickColor(particleColor[i], 1f - particleSaturation)
                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadiusA,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadiusA,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            particleSize,
                            mPaint
                        )

                        mPaint.color = pickColor(particleColor[i] + 0.1f, 1f - particleSaturation)
                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadiusB,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadiusB,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            particleSize,
                            mPaint
                        )
                    }
                }
                in node1..node2 -> {
                    val realPercent = interpolatedTime.realPercent(node1, node2)
                    val newParticleSizeA = particleSize * (1 + PARTICLE_SIZE_SCALE_A * realPercent)
                    val particleScopeRadiusA =
                        (PARTICLE_MIDDLE_SCOPE_A * currentSize + realPercent *
                                (PARTICLE_END_SCOPE_A - PARTICLE_MIDDLE_SCOPE_A) * currentSize) / 2
                    val newParticleSizeB = particleSize * (1 + PARTICLE_SIZE_SCALE_B * realPercent)
                    val particleScopeRadiusB =
                        (PARTICLE_MIDDLE_SCOPE_B * currentSize + realPercent *
                                (PARTICLE_END_SCOPE_B - PARTICLE_MIDDLE_SCOPE_B) * currentSize) / 2
                    for (i in 0..6) {
                        mPaint.color = pickColor(particleColor[i], 1f - particleSaturation)

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadiusA,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadiusA,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            newParticleSizeA,
                            mPaint
                        )

                        mPaint.color = pickColor(particleColor[i] + 0.1f, 1f - particleSaturation)

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadiusB,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadiusB,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            newParticleSizeB,
                            mPaint
                        )
                    }

                }
                in node2..node3 -> {
                    val realPercent = interpolatedTime.realPercent(node2, node3)
                    val newParticleSizeA =
                        particleSize * (1 + PARTICLE_SIZE_SCALE_A) * (1 - realPercent)
                    val particleScopeRadiusA = PARTICLE_END_SCOPE_A * currentSize / 2

                    val newParticleSizeB =
                        particleSize * (1 + PARTICLE_SIZE_SCALE_B) * (1 - realPercent)
                    val particleScopeRadiusB = PARTICLE_END_SCOPE_B * currentSize / 2

                    for (i in 0..6) {
                        mPaint.color = pickColor(particleColor[i], 1f - particleSaturation)

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadiusA,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadiusA,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            newParticleSizeA,
                            mPaint
                        )
                        mPaint.color = pickColor(particleColor[i] + 0.1f, 1f - particleSaturation)
                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadiusB,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadiusB,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            newParticleSizeB,
                            mPaint
                        )
                    }
                }
            }
        }

        private fun Path.heart(
            centerX: Float,
            centerY: Float,
            size: Float,
            clockwise: Boolean = false
        ) {

            val startY = centerY - size * 0.33429f

            val verticalLine1 = centerY - size * 0.58286f
            val verticalLine2 = centerY - size * 0.50857f
            val verticalLine3 = centerY - size * 0.18f
            val verticalLine4 = centerY + size * 0.11143f
            val verticalLine5 = centerY + size * 0.46f

            val horizontalLine1 = centerX - size / 2
            val horizontalLine2 = centerX - size * 0.16286f
            val horizontalLine3 = centerX - size * 0.12286f
            val horizontalLine4 = centerX + size * 0.12286f
            val horizontalLine5 = centerX + size * 0.16286f
            val horizontalLine6 = centerX + size / 2

            moveTo(centerX, startY)

            if (clockwise) {
                cubicTo(
                    horizontalLine4, verticalLine1,
                    horizontalLine6, verticalLine2,
                    horizontalLine6, verticalLine3
                )
                cubicTo(
                    horizontalLine6, verticalLine4,
                    horizontalLine5, verticalLine5,
                    centerX, verticalLine5
                )
                cubicTo(
                    horizontalLine2, verticalLine5,
                    horizontalLine1, verticalLine4,
                    horizontalLine1, verticalLine3
                )
                cubicTo(
                    horizontalLine1, verticalLine2,
                    horizontalLine3, verticalLine1,
                    centerX, startY
                )
            } else {
                cubicTo(
                    horizontalLine3, verticalLine1,
                    horizontalLine1, verticalLine2,
                    horizontalLine1, verticalLine3
                )
                cubicTo(
                    horizontalLine1, verticalLine4,
                    horizontalLine2, verticalLine5,
                    centerX, verticalLine5
                )
                cubicTo(
                    horizontalLine5, verticalLine5,
                    horizontalLine6, verticalLine4,
                    horizontalLine6, verticalLine3
                )
                cubicTo(
                    horizontalLine6, verticalLine2,
                    horizontalLine4, verticalLine1,
                    centerX, startY
                )
            }

            close()
        }


        private fun circleX(x: Float, r: Float, angle: Int): Double {
            return x - r * sin(Math.PI * (angle - 90) / 180)
        }

        private fun circleY(y: Float, r: Float, angle: Int): Double {
            return y + r * cos(Math.PI * (angle - 90) / 180)
        }

        private fun Float.realPercent(offset: Float, end: Float): Float {
            return (this - offset) / (end - offset)
        }

        private fun colorChange(p: Float, start: Int, end: Int): Int {
            val startA = start shr 24 and 0xff
            val startR = start shr 16 and 0xff
            val startG = start shr 8 and 0xff
            val startB = start and 0xff
            val endA = end shr 24 and 0xff
            val endR = end shr 16 and 0xff
            val endG = end shr 8 and 0xff
            val endB = end and 0xff

            return ((startA + (p * (endA - startA)).toInt()) shl 24) or
                    ((startR + (p * (endR - startR)).toInt()) shl 16) or
                    ((startG + (p * (endG - startG)).toInt()) shl 8) or
                    (startB + (p * (endB - startB)).toInt())
        }

        private fun pickColor(p: Float, saturation: Float = 0.0f): Int {
            val level = floor(p * 6).roundToInt() % 6
            val percent = p * 6 % 1
            return when (level) {
                0 -> (0xff shl 24) or
                        (0xff shl 16) or
                        ((255 * saturation).toInt() shl 8) or
                        (255 * (percent + saturation - percent * saturation)).toInt()

                1 -> (0xff shl 24) or
                        ((255 * (1 - percent + percent * saturation)).toInt() shl 16) or
                        ((255 * saturation).toInt() shl 8) or
                        0xff

                2 -> (0xff shl 24) or
                        ((255 * saturation).toInt() shl 16) or
                        ((255 * (percent + saturation - percent * saturation)).toInt() shl 8) or
                        0xff
                3 -> (0xff shl 24) or
                        ((255 * saturation).toInt() shl 16) or
                        (0xff shl 8) or
                        (255 * (1 - percent + percent * saturation)).toInt()
                4 -> (0xff shl 24) or
                        ((255 * (percent + saturation - percent * saturation)).toInt() shl 16) or
                        (0xff shl 8) or
                        (255 * saturation).toInt()
                5 -> (0xff shl 24) or
                        (0xff shl 16) or
                        ((255 * (1 - percent + percent * saturation)).toInt() shl 8) or
                        (255 * saturation).toInt()
                else -> throw  IllegalArgumentException()
            }

        }
    }
}