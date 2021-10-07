package com.mitsuki.ehit.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.armory.base.extend.toast
import com.mitsuki.armory.base.permission.Tool
import com.mitsuki.armory.base.permission.readStorePermissionLauncher
import com.mitsuki.armory.base.permission.writeStorePermissionLauncher
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.*
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ActivityMainBinding
import com.mitsuki.ehit.crutch.zip.ZipPacker
import com.mitsuki.ehit.crutch.zip.ZipReader
import com.mitsuki.ehit.dev.overlay.OverlayTool
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.ui.setting.SettingActivity
import com.mitsuki.ehit.ui.temp.activity.DownloadActivity
import com.mitsuki.ehit.ui.temp.activity.HistoryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //在使用FragmentContainerView作为容器的情况下需要以下面的形式来获取NavController实例
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment).navController
    }
    private val controller by windowController()
    private val binding by viewBinding(ActivityMainBinding::inflate)


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

                R.id.nav_history ->
                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))

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

        when {
            OpenGate.open -> navDestination(R.id.nav_stack_open_gate, null)
            ShareData.spSecurity -> navDestination(R.id.nav_stack_authority, null)
        }
//        readStorePermissionLauncher.launch {  }
//        writeStorePermissionLauncher.launch {  }
        onCreateDev()
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

    private val lastPressedTime: Long = 0

    override fun onBackPressed() {
        Log.debug("${onBackPressedDispatcher.hasEnabledCallbacks()}")
        super.onBackPressed()
//        if (System.currentTimeMillis() - lastPressedTime < 2000) {
//            ActivityKeep.out()
//        } else {
//        Snackbar.make(
//            binding.mainDrawer,
//            R.string.hint_exit_by_next_back,
//            Snackbar.LENGTH_SHORT
//        ).show()
//        }
    }

    /** dev 用 *************************************************************************************/
    private val readStorePermissionLauncher = readStorePermissionLauncher()
    private val writeStorePermissionLauncher = writeStorePermissionLauncher()

    private lateinit var packer: ZipPacker
    private lateinit var reader: ZipReader

    private fun onCreateDev() {
        lifecycle.addObserver(OverlayTool)
        OverlayTool.panelAction(this::onDevPanel)
        OverlayTool.permission(this)?.apply { startActivity(this) }

        packer = ZipPacker(
            this,
            activityResultRegistry,
            RoomData.dbFolder,
            RoomData.storeFileArray
        ) { RoomData.storeSaveFileName() }
        reader = ZipReader(this, activityResultRegistry, RoomData.dbFolder)
    }

    private fun onDevPanel(id: Int) {
        when (id) {
            R.id.dev_overlay_db_import -> {
                //导入，需要读取权限
                if (Tool.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    reader.read()
                } else {
                    readStorePermissionLauncher.launch { if (it) reader.read() else toast("缺少写入权限") }
                }
            }
            R.id.dev_overlay_db_export -> {
                //导出，需要写入权限
                if (Tool.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    packer.pack()
                } else {
                    writeStorePermissionLauncher.launch { if (it) packer.pack() else toast("缺少写入权限") }
                }
            }
        }
    }

}
