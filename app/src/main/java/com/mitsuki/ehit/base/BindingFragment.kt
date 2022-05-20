package com.mitsuki.ehit.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.mitsuki.ehit.crutch.extensions.viewBinding

abstract class BindingFragment<VB : ViewBinding>(
    @LayoutRes layout: Int,
    val inflate: (View) -> VB
) :
    BaseFragment(layout) {
    var binding: VB? = null
        private set

    fun requireBinding(): VB {
        return binding ?: throw IllegalArgumentException()
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
            binding = inflate(this)
        }
    }

    @CallSuper
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}