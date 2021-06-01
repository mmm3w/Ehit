package com.mitsuki.ehit.ui.start.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentLoginBinding
import com.mitsuki.ehit.ui.main.MainActivity
import com.mitsuki.ehit.ui.temp.adapter.*
import com.mitsuki.ehit.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 登录
 * cookie与账号密码登录合并
 * web登录用新activity
 */
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val mViewModel: LoginViewModel
            by createViewModelLazy(LoginViewModel::class, { viewModelStore })

    private val mAccountAdapter by lazy { LoginAccountAdapter() }
    private val mCookieAdapter by lazy { LoginCookieAdapter() }
    private val mDomain by lazy { LoginDomain() }
    private val mExtend by lazy { LoginExtend() }

    private val mAdapter by lazy {
        ConcatAdapter(
            mAccountAdapter,
            mCookieAdapter,
            mDomain,
            mExtend
        )
    }

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.loginUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.nextEvent.observe(viewLifecycleOwner) { goAhead() }

        mViewModel.toastEvent.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
        mExtend.onSwitch.observe(viewLifecycleOwner) {
            mAccountAdapter.isEnable = !it
            mCookieAdapter.isEnable = it
        }

        mExtend.onSkip.observe(viewLifecycleOwner) { goAhead() }

        mAccountAdapter.onLogin.observe(viewLifecycleOwner) {
            it?.apply { mViewModel.login(account, password) }
        }

        mCookieAdapter.onLogin.observe(viewLifecycleOwner) {
            it?.apply { mViewModel.login(memberId, passHash, igneous) }
        }
    }

    private fun goAhead() {
        ShareData.spFirstOpen = false
        (requireActivity() as MainActivity).navDestination(R.id.nav_stack_main, null)
    }


}