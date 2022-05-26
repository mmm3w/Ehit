package com.mitsuki.ehit.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.*
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ActivityMainBinding
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.ui.setting.activity.SettingActivity
import com.mitsuki.ehit.ui.download.activity.DownloadActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    //在使用FragmentContainerView作为容器的情况下需要以下面的形式来获取NavController实例
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment).navController
    }
    private val binding by viewBinding(ActivityMainBinding::inflate)

    @Inject
    lateinit var openGate: OpenGate

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = true
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
        }
        super.onCreate(savedInstanceState)

        binding.mainDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        //NavigationUI提供的setup方法无法满足需求
        binding.mainNavigation.setNavigationItemSelectedListener {
            var handle = true
            when (it.itemId) {
                R.id.nav_home -> navDestination(
                    R.id.nav_stack_main,
                    bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.DEFAULT_NORMAL)
                )

                R.id.nav_subscription -> navDestination(
                    R.id.nav_stack_main,
                    bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.DEFAULT_SUBSCRIPTION)
                )

                R.id.nav_popular -> navDestination(
                    R.id.nav_stack_main,
                    bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.POPULAR)
                )

                R.id.nav_favourite -> navDestination(R.id.nav_stack_favourite, null)

//                R.id.nav_history ->
//                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))

                R.id.nav_download ->
                    startActivity(Intent(this@MainActivity, DownloadActivity::class.java))

                R.id.nav_setting ->
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))

                else -> handle = false
            }
            if (handle) binding.mainDrawer.close()
            handle
        }

        navController.setGraph(R.navigation.nav_graph)
        //TODO 还要做内部打开跳转相关的处理
        //针对url隐式意图打开的处理
        val uri = intent?.data
        val data = intent?.getParcelableExtra<Gallery>(DataKey.GALLERY_INFO)
        when {
            openGate.open -> navDestination(R.id.nav_stack_open_gate, null)
            shareData.spSecurity -> navDestination(R.id.nav_stack_authority, null)
            uri != null -> {
                when {
                    //画廊详情跳转
                    uri.path?.startsWith("/g") == true -> uri.path?.apply { onGalleryLink(this) }
                    //其他的一些列表跳转，依赖url中参数解析
                    else -> GalleryPageSource.createByUri(uri)?.apply { onListLink(this) }
                }
            }
            data != null -> onGalleryDetail(data)
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

    fun navDestination(navID: Int, args: Bundle?) {
        val builder = NavOptions.Builder().setLaunchSingleTop(true)
        builder.setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
            .setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
            .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
            .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
        //直接弹出整个栈
        navController.currentDestination?.parent?.apply { builder.setPopUpTo(id, true) }
        navController.navigate(navID, args, builder.build(), null)
    }

    fun navigate(id: Int) {
        navController.navigate(id)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun onGalleryLink(path: String) {
        //按/切割并获取对应位置参数
        path.split("/").apply {
            if (size >= 4) {
                onGalleryDetail(Gallery(this[2].toLongOrNull() ?: 0L, this[3]))
            }
        }
    }

    private fun onGalleryDetail(detail: Gallery) {
        navDestination(
            R.id.nav_stack_gallery,
            bundleOf(DataKey.GALLERY_INFO to detail)
        )
    }

    private fun onListLink(source: GalleryPageSource) {
        navDestination(R.id.nav_stack_main, bundleOf(DataKey.GALLERY_PAGE_SOURCE to source))
    }

    fun enableDrawer() {
        binding.mainDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }


}
