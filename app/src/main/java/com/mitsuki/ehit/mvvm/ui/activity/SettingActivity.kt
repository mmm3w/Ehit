package com.mitsuki.ehit.mvvm.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.ui.fragment.SettingRootFragment
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_setting)
        setSupportActionBar(appBar)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingRootFragment())
            .commit()
    }
}


