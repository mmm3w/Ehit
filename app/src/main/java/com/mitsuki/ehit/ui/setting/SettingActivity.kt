package com.mitsuki.ehit.ui.setting

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val binding by viewBinding(ActivitySettingBinding::inflate)

    private val controller by windowController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller.window(
            navigationBarLight = true, statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )

        setSupportActionBar(binding.appBar)
        supportFragmentManager.commit { replace(R.id.settings_container, SettingRootFragment()) }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        supportFragmentManager.commit {
            val fragment =
                supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment ?: "")
            fragment.arguments = pref.extras
            replace(R.id.settings_container, fragment)
            setCustomAnimations(
                androidx.navigation.ui.R.animator.nav_default_enter_anim,
                androidx.navigation.ui.R.animator.nav_default_exit_anim,
                androidx.navigation.ui.R.animator.nav_default_pop_enter_anim,
                androidx.navigation.ui.R.animator.nav_default_pop_exit_anim
            )
            addToBackStack(pref.fragment)
        }
        return true
    }
}


