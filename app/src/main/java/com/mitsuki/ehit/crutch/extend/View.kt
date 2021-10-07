package com.mitsuki.ehit.crutch.extend

import android.graphics.Outline
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

fun ViewGroup.createItemView(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)


fun DialogFragment.requireWindow(): Window {
    return requireDialog().window
        ?: throw IllegalStateException("DialogFragment $this does not have a window.")
}

fun View.oval() {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            view?.apply { outline?.setOval(0, 0, width, height); }
        }
    }
    clipToOutline = true
}

fun View.corners(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, width, height, radius)
        }
    }
    clipToOutline = true
}