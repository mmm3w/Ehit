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
import kotlin.collections.indices as indices

class HeartDrawable : Drawable(), Animatable {

    private val mCoreAnimator: Animator
    private val mEffect: Effect = Effect()

    companion object {
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
        private const val ANIMATION_DURATION = 1000

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

    var heartHintColor: Int
        get() = mEffect.heartHintColor
        set(value) {
            if (value != mEffect.heartHintColor) {
                mEffect.heartHintColor = value
                invalidateSelf()
            }
        }

    var heartHintStroke: Float
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

    fun setParticleColor(color: (Int, Int) -> Int) {
        mEffect.setParticleColor(color)
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

        var circleStartColor: Int = 0xffca4c86.toInt()
        var circleEndColor: Int = 0xffc193ee.toInt()

        var size: Float = -1f

        private val particleColor = Array(PARTICLE_COUNT * 4) { 0 }
        private val mPaint = Paint().apply { isAntiAlias = true }
        private val mPath = Path()

        var interpolatedTime = 0F
            set(value) {
                val real = value.coerceIn(0f, 1f)
                if (real != field) {
                    field = real
                }
            }

        init {
            var group: Int = -1
            setParticleColor { index, _ ->
                if (index % 2 == 0) {
                    group = (0 until (PARTICLE_FIXED_COLLOCATION.size / 2)).random()
                    PARTICLE_FIXED_COLLOCATION[group]
                } else {
                    PARTICLE_FIXED_COLLOCATION[group + 1]
                }
            }
        }

        companion object {
            const val CIRCLE_SIZE = 0.55f
            const val HEART_SIZE = 0.37f
            const val HEART_BEAT_SCALE = 0.1f
            const val PARTICLE_COUNT = 7
            const val PARTICLE_SIZE = 0.05f

            const val PARTICLE_DEFLECTION_A = 12
            const val PARTICLE_START_SCOPE_A = 0.51f
            const val PARTICLE_MIDDLE_SCOPE_A = 0.67f
            const val PARTICLE_END_SCOPE_A = 0.72f

            const val PARTICLE_DEFLECTION_B = 23
            const val PARTICLE_START_SCOPE_B = 0.51f
            const val PARTICLE_MIDDLE_SCOPE_B = 0.79f
            const val PARTICLE_END_SCOPE_B = 0.90f

            val PARTICLE_FIXED_COLLOCATION = intArrayOf(
                0xffb2cbe2.toInt(), 0xffc7ace8.toInt(),
                0xffbc99de.toInt(), 0xffe3bc7c.toInt(),
                0xffb1e0cb.toInt(), 0xffd0c3ac.toInt(),
                0xffe0a46c.toInt(), 0xff7ba1cd.toInt(),
                0xffb2cbe2.toInt(), 0xffc394cb.toInt(),
                0xffbc99de.toInt(), 0xffd2eed2.toInt(),
            )
        }

        fun setParticleColor(color: (Int, Int) -> Int) {
            for (i in particleColor.indices) {
                particleColor[i] = color(i, particleColor[i])
            }
        }

        fun draw(c: Canvas, bounds: Rect) {
            mPaint.alpha = alpha
            drawParticleA(c, bounds)
            drawParticleB(c, bounds)
            drawCircle(c, bounds)
            drawHeart(c, bounds)
        }

        private fun size(bounds: Rect): Float {
            return if (size < 0) {
                bounds.width().coerceAtMost(bounds.height()).toFloat()
            } else {
                size
            }
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
                val node1 = 0.18f
                val node2 = 0.36f
                val node3 = 0.58f
                val node4 = 1f
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
                    in node3..node4 -> {
                        val size = heartSize * (1 - (1 -
                                interpolatedTime.realPercent(node3, node4)) * HEART_BEAT_SCALE)
                        mPath.heart(centerX, centerY, size)
                        canvas.drawPath(mPath, mPaint)
                    }
                }
            }
        }

        private fun drawCircle(canvas: Canvas, bounds: Rect) {
            if (fixed) return
            mPath.reset()

            val node1 = 0.15f
            val node2 = 0.33f
            val circleSize = size(bounds) * CIRCLE_SIZE / 2
            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()

            when (interpolatedTime) {
                in 0f..node1 -> {
                    mPaint.color = colorChange(
                        interpolatedTime.realPercent(0f, node2),
                        circleStartColor,
                        circleEndColor
                    )
                    val size = interpolatedTime.realPercent(0f, node1) * circleSize
                    mPath.addCircle(centerX, centerY, size, Path.Direction.CW)
                    canvas.drawPath(mPath, mPaint)
                }
                in node1..node2 -> {
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = colorChange(
                        interpolatedTime.realPercent(0f, node2),
                        circleStartColor,
                        circleEndColor
                    )
                    mPath.addCircle(centerX, centerY, circleSize, Path.Direction.CW)
                    val size = interpolatedTime.realPercent(node1, node2) * circleSize
                    mPath.addCircle(centerX, centerY, size, Path.Direction.CCW)
                    canvas.drawPath(mPath, mPaint)
                }
            }
        }

        private fun drawParticleA(canvas: Canvas, bounds: Rect) {
            if (fixed) return
            mPaint.style = Paint.Style.FILL

            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()

            val currentSize = size(bounds)
            val particleSize = currentSize * PARTICLE_SIZE / 2

            val node1 = 0.15f
            val node2 = 0.25f
            val node3 = 0.8f

            val colorStart = 0.16f

            when (interpolatedTime) {
                in node1..node2 -> {
                    val realPercent = interpolatedTime.realPercent(node1, node2)
                    val particleScopeRadius = (PARTICLE_START_SCOPE_A * currentSize + realPercent *
                            (PARTICLE_MIDDLE_SCOPE_A - PARTICLE_START_SCOPE_A) * currentSize) / 2
                    for (i in 0..6) {
                        mPaint.color = colorChange(
                            interpolatedTime.realPercent(colorStart, node3),
                            particleColor[i * 4],
                            particleColor[i * 4 + 1]
                        )

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            particleSize,
                            mPaint
                        )
                    }

                }
                in node2..node3 -> {
                    val realPercent = interpolatedTime.realPercent(node2, node3)
                    val newParticleSize = particleSize * (1 - realPercent)
                    val particleScopeRadius = (PARTICLE_MIDDLE_SCOPE_A * currentSize + realPercent *
                            (PARTICLE_END_SCOPE_A - PARTICLE_MIDDLE_SCOPE_A) * currentSize) / 2
                    for (i in 0..6) {
                        mPaint.color = colorChange(
                            interpolatedTime.realPercent(colorStart, node3),
                            particleColor[i * 4],
                            particleColor[i * 4 + 1]
                        )

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_A).toInt()
                            ).toFloat(),
                            newParticleSize,
                            mPaint
                        )
                    }
                }
            }
        }

        private fun drawParticleB(canvas: Canvas, bounds: Rect) {
            if (fixed) return
            mPaint.style = Paint.Style.FILL

            val centerX = bounds.centerX().toFloat()
            val centerY = bounds.centerY().toFloat()

            val currentSize = size(bounds)
            val particleSize = currentSize * PARTICLE_SIZE / 2

            val node1 = 0.20f
            val node2 = 0.44f
            val node3 = 1f

            val colorStart = 0.21f

            when (interpolatedTime) {
                in node1..node2 -> {
                    val realPercent = interpolatedTime.realPercent(node1, node2)
                    val particleScopeRadius = (PARTICLE_START_SCOPE_B * currentSize + realPercent *
                            (PARTICLE_MIDDLE_SCOPE_B - PARTICLE_START_SCOPE_B) * currentSize) / 2
                    for (i in 0..6) {
                        mPaint.color = colorChange(
                            interpolatedTime.realPercent(colorStart, node3),
                            particleColor[i * 4 + 2],
                            particleColor[i * 4 + 3]
                        )

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            particleSize,
                            mPaint
                        )
                    }

                }
                in node2..node3 -> {
                    val realPercent = interpolatedTime.realPercent(node2, node3)
                    val newParticleSize = particleSize * (1 - realPercent)
                    val particleScopeRadius = (PARTICLE_MIDDLE_SCOPE_B * currentSize + realPercent *
                            (PARTICLE_END_SCOPE_B - PARTICLE_MIDDLE_SCOPE_B) * currentSize) / 2
                    for (i in 0..6) {
                        mPaint.color = colorChange(
                            interpolatedTime.realPercent(colorStart, node3),
                            particleColor[i * 4 + 2],
                            particleColor[i * 4 + 3]
                        )

                        canvas.drawCircle(
                            circleX(
                                centerX,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            circleY(
                                centerY,
                                particleScopeRadius,
                                (360f / PARTICLE_COUNT * i + PARTICLE_DEFLECTION_B).toInt()
                            ).toFloat(),
                            newParticleSize,
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
            if (this <= offset) return 0f
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
    }
}