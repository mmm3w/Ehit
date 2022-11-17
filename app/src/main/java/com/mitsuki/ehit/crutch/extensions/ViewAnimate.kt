package com.mitsuki.ehit.crutch.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.isVisible

fun View.animate(animate: ViewPropertyAnimatorCompat.() -> Unit) {
    clearAnimation()
    ViewCompat.animate(this).apply(animate).start()
}

fun View.fadeInit(visible: Boolean = false) {
    alpha = if (visible) 1f else 0f
    isVisible = visible
}

fun ViewPropertyAnimatorCompat.fadeIn() {
    alpha(1f)
    setListener(object : ViewPropertyAnimatorListener {
        override fun onAnimationStart(view: View) {
            view.isVisible = true
        }

        override fun onAnimationEnd(view: View) {
            view.isVisible = true
        }

        override fun onAnimationCancel(view: View) {

        }
    })
}

fun ViewPropertyAnimatorCompat.fadeOut() {
    alpha(0f)
    setListener(object : ViewPropertyAnimatorListener {
        override fun onAnimationStart(view: View) {
            view.isVisible = true
        }

        override fun onAnimationEnd(view: View) {
            view.isVisible = false
        }

        override fun onAnimationCancel(view: View) {

        }
    })
}