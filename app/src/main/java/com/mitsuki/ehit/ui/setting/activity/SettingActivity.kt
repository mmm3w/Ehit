package com.mitsuki.ehit.ui.setting.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ActivitySettingBinding
import com.mitsuki.ehit.ui.setting.fragment.SettingRootFragment
import com.mitsuki.ehit.ui.setting.widget.TransformView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : BaseActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val binding by viewBinding(ActivitySettingBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appBar)
        setTitle(R.string.text_menu_setting)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.settings_container,
                    SettingRootFragment()
                )
            }
        }
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
        setBarStates()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.findFragmentById(R.id.settings_container) is SettingRootFragment) {
            //TODO title的切换最好加入动画
            setTitle(R.string.text_menu_setting)
        }
        setBarStates()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val decorView = window.decorView
        val bitmap = Bitmap.createBitmap(
            decorView.width,
            decorView.height,
            Bitmap.Config.ARGB_8888
        ).apply {
            decorView.draw(Canvas(this))
        }
        TransformView(this, bitmap).start(this)

        binding.settingLayout.setBackgroundColor(color(R.color.background_color_general))
        binding.appBar.setTitleTextColor(color(R.color.text_color_general))
        setBarStates()
        if (supportFragmentManager.findFragmentById(R.id.settings_container) is SettingRootFragment) {
            supportFragmentManager.commit {
                replace(
                    R.id.settings_container,
                    SettingRootFragment()
                )
            }
        } else {
            supportFragmentManager.fragments.forEach {
                (it as? PreferenceFragmentCompat)?.listView?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onUiMode(isNightMode: Boolean) {
        controller.window(
            navigationBarLight = !isNightMode,
            statusBarLight = !isNightMode,
            navigationBarColor = Color.TRANSPARENT,
            statusBarColor = Color.TRANSPARENT,
            barFit = false
        )
    }
}


