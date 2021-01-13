package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.ShareData
import com.mitsuki.ehit.core.ui.adapter.*
import com.mitsuki.ehit.core.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * 登录
 * cookie与账号密码登录合并
 * web登录用新activity
 */
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val mViewModel: LoginViewModel
            by createViewModelLazy(LoginViewModel::class, { viewModelStore })

    private val mHeader by lazy { LoginHeader() }
    private val mAccountAdapter by lazy { LoginAccountAdapter() }
    private val mCookieAdapter by lazy { LoginCookieAdapter() }
    private val mDomain by lazy { LoginDomain() }
    private val mExtend by lazy { LoginExtend() }


    private val mAdapter by lazy {
        ConcatAdapter(
            mHeader,
            mAccountAdapter,
            mCookieAdapter,
            mDomain,
            mExtend
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        login_ui?.layoutManager = LinearLayoutManager(requireContext())
        login_ui?.adapter = mAdapter

        mExtend.onSwitch.observe(viewLifecycleOwner, Observer {
            mAccountAdapter.isEnable = !it
            mCookieAdapter.isEnable = it
        })

        mExtend.onSkip.observe(viewLifecycleOwner, Observer {
            ShareData.spFirstOpen = false
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(R.id.action_login_fragment_to_gallery_list_fragment)
        })

        mAccountAdapter.onLogin.observe(viewLifecycleOwner, Observer {
            it?.apply {
                showLoading()
                mViewModel.login(account, password)
            }
        })

        mCookieAdapter.onLogin.observe(viewLifecycleOwner, Observer {
            it?.apply { mViewModel.login(memberId, passHash, igneous) }
        })
    }


    private fun showLoading() {

    }

    private fun dismissLoading() {
    }

}