package com.mitsuki.ehit.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.WindowController
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivitySettingBinding
import com.mitsuki.ehit.ui.fragment.setting.SettingRootFragment

class SettingActivity : BaseActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val binding by viewBinding(ActivitySettingBinding::inflate)

    private val controller by windowController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        controller.window(
            navigationBarLight = true, statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )

        setSupportActionBar(binding.appBar)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingRootFragment())
            .commit()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        )
        fragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }
}


