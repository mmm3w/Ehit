package com.mitsuki.ehit.dev.overlay

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.armory.systemoverlay.OverlayManager
import com.mitsuki.armory.systemoverlay.OverlayView
import com.mitsuki.armory.systemoverlay.update
import com.mitsuki.armory.systemoverlay.windowType
import com.mitsuki.ehit.R

class PanelOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OverlayView {

    private lateinit var contentView: View

    init {
        LayoutInflater.from(context).inflate(R.layout.overlay_panel, this)
        contentView = findViewById<View>(R.id.overlay_out).apply {
            setOnClickListener { /* do nothing */ }
        }

    }

    override var isAdded: Boolean = false

    private var mAppearAnimation: ValueAnimator? = null
    private var mDisappearAnimation: ValueAnimator? = null

    private val mLayoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams().apply {
            type = windowType()
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.START
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = 0.5f
            x = 0
            y = 0
        }
    }

    override fun view(): View = this

    override fun layoutParams(): WindowManager.LayoutParams = mLayoutParams

    override fun appear() {
        isVisible = true
        update()
        post {
            if (!mAppearAnimation.isAnimationRunning()) {
                var start =
                    if (OverlayTool.triggerX > OverlayManager.screenWidth / 2) OverlayManager.screenWidth.toFloat() else -width.toFloat()
                val end =
                    if (OverlayTool.triggerX > OverlayManager.screenWidth / 2) (OverlayManager.screenWidth - width).toFloat() - dp2px(
                        8f
                    ) else (0f + dp2px(8f))

                contentView.also {
                    var marginTop = OverlayTool.triggerY
                    if (marginTop + it.height > OverlayManager.screenHeight) {
                        marginTop = OverlayManager.screenHeight - it.height
                    }
                    it.layoutParams = it.layoutParams.run {
                        (this as MarginLayoutParams).setMargins(0, marginTop, 0, 0)
                        (this as FrameLayout.LayoutParams).gravity =
                            if (OverlayTool.triggerX > OverlayManager.screenWidth / 2) Gravity.END else Gravity.START
                        this
                    }
                }

                if (mDisappearAnimation.isAnimationRunning()) {
                    mDisappearAnimation?.cancel()
                    start = translationX
                }

                mAppearAnimation = ValueAnimator.ofFloat(start, end).apply {
                    duration = 300
                    addUpdateListener {
                        translationX = it.animatedValue as Float
                        update()
                    }
                    start()
                }

            }
        }
    }

    override fun disappear() {
        if (!mDisappearAnimation.isAnimationRunning()) {
            val end =
                if (OverlayTool.triggerX > OverlayManager.screenWidth / 2) OverlayManager.screenWidth.toFloat() else -width.toFloat()

            mDisappearAnimation = ValueAnimator.ofFloat(translationX, end).apply {
                duration = 300
                addUpdateListener {
                    translationX = it.animatedValue as Float
                    update()
                }
                addListener(onEnd = {
                    isVisible = false
                    update()
                })
                start()
            }

            if (mAppearAnimation.isAnimationRunning())
                mAppearAnimation?.cancel()
        }
    }

    private fun ValueAnimator?.isAnimationRunning(): Boolean {
        if (this == null) return false
        return isStarted && isRunning
    }

}