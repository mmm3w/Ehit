package com.mitsuki.ehit.ui.download

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

class ControlAnimate {

    private var lastLeft = 0
    private var parentWidth = 0
    private var lastAnimator: AnimatorSet? = null

    fun trigger(view: View) {
        if (view.isSelected) {
            view.isSelected = false
            //展开
            cancelAnimate()
        } else {
            view.isSelected = true
            //收缩
            //先是其他View全部Invisible
            val parent = view.parent as? ViewGroup
            parent?.apply {
                children.forEach { if (it != view) it.isInvisible = true }
                parentWidth = measuredWidth
            }
            //然后强制移动view，以及改变父View的宽度
            lastLeft = view.left

            cancelAnimate()

            lastAnimator = AnimatorSet().apply {
                duration = 3000
                addListener(
                    onEnd = {
                        parent?.apply {
                            children.forEach {
                                if (it != view) it.isVisible = false
                            }
                        }
                    }
                )
                playTogether(
                    ObjectAnimator.ofFloat(view, "translationX", 0f, 0f - lastLeft),
                    ValueAnimator.ofInt(parent?.measuredWidth ?: 0, view.measuredWidth).apply {
                        addUpdateListener {
                            (it.animatedValue as Int).apply {
                                val lp = parent?.layoutParams
                                lp?.width = this
                                parent?.layoutParams = lp
                            }
                        }
                    }
                )
            }
        }
    }

    private fun cancelAnimate() {
        if (lastAnimator?.isStarted == true && lastAnimator?.isRunning == true) {
            lastAnimator?.cancel()
        }
    }
}