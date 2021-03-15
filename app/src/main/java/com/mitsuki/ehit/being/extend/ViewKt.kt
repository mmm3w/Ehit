package com.mitsuki.ehit.being.extend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

fun ViewGroup.createItemView(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)

