package com.mitsuki.ehit.crutch.extend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

fun ViewGroup.createItemView(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)


fun DialogFragment.requireWindow(): Window {
    return requireDialog().window
        ?: throw IllegalStateException("DialogFragment $this does not have a window.")
}