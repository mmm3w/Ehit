package com.mitsuki.ehit.ui.setting.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ActivitySettingBinding
import com.mitsuki.ehit.ui.setting.fragment.SettingRootFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : BaseActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val binding by viewBinding(ActivitySettingBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appBar)
        setTitle(R.string.text_menu_setting)
        supportFragmentManager.commit { replace(R.id.settings_container, SettingRootFragment()) }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        supportFragmentManager.commit {
            val fragment =
                supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment ?: "")
            fragment.arguments = pref.extras
            setCustomAnimations(
                R.anim.nav_default_enter_anim,
                R.anim.nav_default_exit_anim,
                R.anim.nav_default_pop_enter_anim,
                R.anim.nav_default_pop_exit_anim
            )
            replace(R.id.settings_container, fragment)
            addToBackStack(pref.fragment)
            title = pref.title
        }
        keepBarStates()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.findFragmentById(R.id.settings_container) is SettingRootFragment) {
            //TODO title的切换最好加入动画
            setTitle(R.string.text_menu_setting)
        }
        keepBarStates()
    }

    private fun keepBarStates() {
        val isNightMode =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }
        onUiMode(isNightMode)
    }
}


