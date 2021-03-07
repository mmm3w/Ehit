package com.mitsuki.ehit.being.extend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.createItemView(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)
