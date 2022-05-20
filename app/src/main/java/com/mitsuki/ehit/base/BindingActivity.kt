package com.mitsuki.ehit.base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.mitsuki.ehit.crutch.extensions.viewBinding

open class BindingActivity<VB : ViewBinding>(inflate: (LayoutInflater) -> VB) : BaseActivity() {
    val binding by viewBinding(inflate)
}