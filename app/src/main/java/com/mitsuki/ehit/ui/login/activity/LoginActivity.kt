package com.mitsuki.ehit.ui.login.activity

import android.os.Bundle
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    private val binding by viewBinding(ActivityLoginBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.fragmentContainerView
    }
}