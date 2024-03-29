package com.mitsuki.ehit.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extensions.showSnackBar
import com.mitsuki.ehit.crutch.extensions.showToast
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ActivityMainBinding
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.ui.setting.activity.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    //在使用FragmentContainerView作为容器的情况下需要以下面的形式来获取NavController实例
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment).navController
    }

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private var mExitTimestamp = 0L

    @Inject
    lateinit var galleryDao: GalleryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = true
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
        }
        super.onCreate(savedInstanceState)
        AppHolder.lock()
        //NavigationUI提供的setup方法无法满足需求
        binding.mainNavigation.setNavigationItemSelectedListener {
            var handle = true
            when (it.itemId) {
                R.id.nav_home -> navController.navigate(R.id.action_global_gallery_list_fragment)
                R.id.nav_subscription ->
                    navController.navigate(
                        R.id.action_global_gallery_list_fragment,
                        bundleOf(DataKey.GALLERY_TYPE_PART to "watched")
                    )
                R.id.nav_popular ->
                    navController.navigate(
                        R.id.action_global_gallery_list_fragment,
                        bundleOf(DataKey.GALLERY_TYPE_PART to "popular")
                    )
                R.id.nav_favourite -> navController.navigate(R.id.action_global_favourite_fragment)
                R.id.nav_history -> navController.navigate(R.id.action_global_history_fragment)
                R.id.nav_download -> navController.navigate(R.id.action_global_download_fragment)
                R.id.nav_setting ->
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                else -> handle = false
            }
            if (handle) binding.mainDrawer.close()
            handle
        }

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.gallery_list_fragment -> {
                    when (arguments?.get(DataKey.GALLERY_TYPE_PART)) {
                        "watched" -> binding.mainNavigation.setCheckedItem(R.id.nav_subscription)
                        "popular" -> binding.mainNavigation.setCheckedItem(R.id.nav_popular)
                        else -> binding.mainNavigation.setCheckedItem(R.id.nav_home)
                    }
                }
                R.id.download_fragment -> binding.mainNavigation.setCheckedItem(R.id.nav_download)
                R.id.favourite_fragment -> binding.mainNavigation.setCheckedItem(R.id.nav_favourite)
                R.id.history_fragment -> binding.mainNavigation.setCheckedItem(R.id.nav_history)
            }
        }
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                galleryDao.deleteGalleryCache(50)
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

    override fun onBackPressed() {
        //不确定这样使用是否正确
        if (navController.backQueue.size <= 2) {
            val current = System.currentTimeMillis()
            if (current - mExitTimestamp > 2000) {
                mExitTimestamp = current
                window.decorView.showSnackBar(text(R.string.hint_exit_by_next_back))
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun setDrawerEnable(enable: Boolean) {
        if (enable) {
            binding.mainDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.mainDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }
}
