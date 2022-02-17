package com.mitsuki.ehit.ui.download

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.core.view.isVisible

class ControlAnimate {

    private var lastLeft = 0
    private var parentWidth = 0
    private var lastAnimator: AnimatorSet? = null

    fun trigger(view: View):Boolean {
        if (lastAnimator?.isStarted == true && lastAnimator?.isRunning == true) return false

        if (view.isSelected) {
            view.isSelected = false
            val parent = view.parent as? ViewGroup
            lastAnimator = AnimatorSet().apply {
                duration = 300
                addListener(
                    onEnd = {
                        parent?.apply {
                            children.forEach {
                                if (it != view) it.isVisible = true
                                view.translationX = 0F
                            }
                        }
                    }
                )
                playTogether(
                    ObjectAnimator.ofFloat(
                        view,
                        "translationX",
                        view.translationX,
                        lastLeft.toFloat()
                    ),
                    ValueAnimator.ofInt(parent?.width ?: 0, parentWidth).apply {
                        addUpdateListener {
                            (it.animatedValue as Int).apply {
                                val lp = parent?.layoutParams
                                lp?.width = this
                                parent?.layoutParams = lp
                            }
                        }
                    }
                )
                start()
            }
            return true
        } else {
            view.isSelected = true
            //收缩
            //先是其他View全部Invisible
            val parent = view.parent as? ViewGroup
            parent?.apply {
                parentWidth = measuredWidth
                //先配置定值
                val lp = parent.layoutParams
                lp?.width = width
                parent.layoutParams = lp
                //然后直接隐藏其他View
                children.forEach { if (it != view) it.isVisible = false }
            }

            lastLeft = view.left
            view.translationX = view.left.toFloat()

            lastAnimator = AnimatorSet().apply {
                duration = 300
                addListener(
                    onEnd = {
                        parent?.apply {
                            children.forEach {
                                if (it != view) it.isVisible = false
                                view.translationX = 0F
                            }
                        }
                    }
                )
                playTogether(
                    ObjectAnimator.ofFloat(view, "translationX", view.translationX, 0f),
                    ValueAnimator.ofInt(parent?.width ?: 0, view.width).apply {
                        addUpdateListener {
                            (it.animatedValue as Int).apply {
                                val lp = parent?.layoutParams
                                lp?.width = this
                                parent?.layoutParams = lp
                            }
                        }
                    }
                )
                start()
            }
            return true
        }
    }
}