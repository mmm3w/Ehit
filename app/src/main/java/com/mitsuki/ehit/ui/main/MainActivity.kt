package com.mitsuki.ehit.ui.main

import android.os.Bundle
import android.view.Window
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityMainBinding
import com.mitsuki.ehit.model.page.GalleryPageSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //在使用FragmentContainerView作为容器的情况下需要以下面的形式来获取NavController实例
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment).navController
    }

    private val controller by windowController()
    private val binding by viewBinding(ActivityMainBinding::inflate)

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = true
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
        }
        super.onCreate(savedInstanceState)

        controller.window(navigationBarLight = true, statusBarLight = true, barFit = false)


        //NavigationUI提供的setup方法无法满足需求
        binding.mainNavigation.setNavigationItemSelectedListener {
            binding.mainDrawer.close()
            when (it.itemId) {
                R.id.nav_home -> {
                    navDestination(
                        R.id.nav_stack_main,
                        bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.DEFAULT_NORMAL)
                    )
                    true
                }
                R.id.nav_subscription -> {
                    navDestination(
                        R.id.nav_stack_subscription,
                        bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.DEFAULT_SUBSCRIPTION)
                    )
                    true
                }
                R.id.nav_popular -> {
                    navDestination(
                        R.id.nav_stack_popular,
                        bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.POPULAR)
                    )
                    true
                }
//                R.id.nav_favourite -> {
//                    navController.navigate(
//                        R.id.action_global_favourite_fragment,
//                        null,
//                        null,
//                        null
//                    )
//                    binding.mainDrawer.close()
//                    true
//                }
//                R.id.nav_history -> {
//                    startActivity(
//                        Intent(this@MainActivity, HistoryActivity::class.java)
//                    )
//                    binding.mainDrawer.close()
//                    true
//                }
//                R.id.nav_download -> {
//                    startActivity(
//                        Intent(this@MainActivity, DownloadActivity::class.java)
//                    )
//                    binding.mainDrawer.close()
//                    true
//                }
//                R.id.nav_setting -> {
//                    startActivity(
//                        Intent(this@MainActivity, SettingActivity::class.java)
//                    )
//                    binding.mainDrawer.close()
//                    true
//                }
                else -> false
            }
        }

        navController.setGraph(R.navigation.nav_graph)
    }


    private fun navDestination(navID: Int, args: Bundle?) {
        val builder = NavOptions.Builder().setLaunchSingleTop(true)
        builder.setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
            .setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
            .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
            .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
        //直接弹出整个栈
        navController.currentDestination?.parent?.apply { builder.setPopUpTo(id, true) }
        navController.navigate(navID, args, builder.build(), null)
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

}
