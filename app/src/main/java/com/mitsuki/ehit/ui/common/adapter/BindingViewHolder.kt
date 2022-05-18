package com.mitsuki.ehit.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mitsuki.ehit.crutch.extensions.viewBinding

open class BindingViewHolder<VB : ViewBinding>(
    parent: ViewGroup,
    @LayoutRes layout: Int,
    bind: (View) -> VB
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layout, parent, false)
) {
    val binding by viewBinding(bind)
}