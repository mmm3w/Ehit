package com.mitsuki.ehit.core.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.core.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val mViewModel: MainViewModel by viewModels()

    //may be crash, not sure
    private val navController: NavController by lazy { findNavController(R.id.main_nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        main_navigation.setupWithNavController(navController)

        main_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_setting -> startActivity(Intent(this, SettingActivity::class.java))
            }
            true
        }
    }


    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

}
