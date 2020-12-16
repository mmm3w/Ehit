package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.ui.adapter.*
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * 登录
 * cookie与账号密码登录合并
 * web登录用新activity
 */
class LoginFragment : Fragment(R.layout.fragment_login) {

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
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(R.id.action_login_fragment_to_gallery_list_fragment)
        })

        mAccountAdapter.onLogin.observe(viewLifecycleOwner, Observer {
            //TODO:账号登录
        })

        mCookieAdapter.onLogin.observe(viewLifecycleOwner, Observer {
            //TODO:Cookie登录
        })
    }
}