package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mitsuki.ehit.R
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * 登录
 * cookie与账号密码登录合并
 * web登录用新activity
 */
class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        login_skip_btn.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigate(R.id.action_login_fragment_to_gallery_list_fragment)
        }
    }
}