package com.mitsuki.ehit.core.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.being.ShareData
import com.mitsuki.ehit.being.extend.debug
import com.mitsuki.ehit.core.crutch.whiteStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //may be crash, not sure
    private val navController: NavController by lazy { findNavController(R.id.main_nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        whiteStyle()

        main_navigation.setupWithNavController(navController)
        main_navigation.apply { post { setCheckedItem(R.id.nav_home) } }
        main_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    debug("home")
                }
                R.id.nav_subscription -> {
                    debug("subscription")
                }
                R.id.nav_popular -> {
                    debug("popular")
                }
                R.id.nav_collection -> {
                    debug("collection")
                }
                R.id.nav_history -> {
                    debug("history")
                }
                R.id.nav_download -> {
                    debug("download")
                }
                R.id.nav_setting -> startActivity(Intent(this, SettingActivity::class.java))
            }
            true
        }

        when {
            ShareData.spFirstOpen -> Navigation.findNavController(this, R.id.main_nav_fragment)
                .navigate(R.id.action_main_fragment_to_disclaimer_fragment)
            ShareData.spSecurity -> Navigation.findNavController(this, R.id.main_nav_fragment)
                .navigate(R.id.action_main_fragment_to_security_fragment)
            else -> Navigation.findNavController(this, R.id.main_nav_fragment)
                .navigate(R.id.action_main_fragment_to_gallery_list_fragment)
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()
}
