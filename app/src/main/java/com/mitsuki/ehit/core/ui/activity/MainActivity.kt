package com.mitsuki.ehit.core.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.SharedElementCallback
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //may be crash, not sure
    private val navController: NavController by lazy { findNavController(R.id.main_nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = statusBarColorStyle()

        main_navigation.setupWithNavController(navController)

        main_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_setting -> startActivity(Intent(this, SettingActivity::class.java))
            }
            true
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

    private fun statusBarColorStyle(): Int {
        var tag = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) tag =
            tag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) tag =
            tag or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        return tag
    }
}
