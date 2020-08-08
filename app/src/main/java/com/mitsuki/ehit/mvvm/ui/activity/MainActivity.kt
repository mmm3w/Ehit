package com.mitsuki.ehit.mvvm.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel>() {

    override val mViewModel: MainViewModel by viewModels()

    //may be crash, not sure
    private val navController: NavController by lazy { findNavController(R.id.main_nav_fragment) }

    override fun initView(savedInstanceState: Bundle?): Int = R.layout.activity_main

    override fun initData(savedInstanceState: Bundle?) {
        //TODO:need to adapt api 21&22 and move code to another class
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        main_navigation.setupWithNavController(navController)

        main_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_setting -> startActivity(Intent(this, SettingActivity::class.java))
            }
            true
        }
        // main_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) 在打开应用锁，或者首次安装的时候，需要对侧滑菜单进行控制

    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

}
