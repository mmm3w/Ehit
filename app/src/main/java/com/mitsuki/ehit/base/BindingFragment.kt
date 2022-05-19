package com.mitsuki.ehit.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.mitsuki.ehit.crutch.extensions.viewBinding

abstract class BindingFragment<VB : ViewBinding>(@LayoutRes layout: Int, inflate: (View) -> VB) :
    BaseFragment(layout) {
    val binding by viewBinding(inflate)

    fun requireBinding(): VB {
        return binding ?: throw IllegalArgumentException()
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewCreated(requireBinding(), view, savedInstanceState)
    }

    abstract fun onViewCreated(innBinding: VB, view: View, savedInstanceState: Bundle?)
}