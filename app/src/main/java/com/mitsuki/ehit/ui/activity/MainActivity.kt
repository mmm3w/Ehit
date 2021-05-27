package com.mitsuki.ehit.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityMainBinding
import com.mitsuki.ehit.model.page.GalleryListPageIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //在使用FragmentContainerView作为容器的情况下需要以下面的形式来获取NavController实例
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment).navController
    }

    private val controller by windowController()

    private lateinit var drawerView: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = true
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerView = findViewById(R.id.main_drawer)

        controller.window(navigationBarLight = true, statusBarLight = true, barFit = false)

        //通过ViewBinding构建的NavigationView可能无法让navigation组件正常工作
        findViewById<NavigationView>(R.id.main_navigation).apply {
            //NavigationUI提供的setup方法无法满足需求
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        Navigation.findNavController(this@MainActivity, R.id.main_nav_fragment)
                            .navigate(
                                R.id.action_gallery_list_fragment_self,
                                bundleOf(DataKey.GALLERY_LIST_TYPE to GalleryListPageIn.Type.NORMAL),
                                null,
                                null
                            )
                        drawerView.close()
                    }
                    R.id.nav_subscription -> {
                        Navigation.findNavController(this@MainActivity, R.id.main_nav_fragment)
                            .navigate(
                                R.id.action_gallery_list_fragment_self,
                                bundleOf(DataKey.GALLERY_LIST_TYPE to GalleryListPageIn.Type.SUBSCRIPTION),
                                null,
                                null
                            )
                        drawerView.close()
                    }
                    R.id.nav_popular -> {
                        Navigation.findNavController(this@MainActivity, R.id.main_nav_fragment)
                            .navigate(
                                R.id.action_gallery_list_fragment_self,
                                bundleOf(DataKey.GALLERY_LIST_TYPE to GalleryListPageIn.Type.WHATS_HOT),
                                null,
                                null
                            )
                        drawerView.close()
                    }
                    R.id.nav_favourite -> {
                        startActivity(
                            Intent(this@MainActivity, FavouriteActivity::class.java)
                        )
                        drawerView.close()
                    }
                    R.id.nav_history -> {
                        startActivity(
                            Intent(this@MainActivity, HistoryActivity::class.java)
                        )
                        drawerView.close()
                    }
                    R.id.nav_download -> {
                        startActivity(
                            Intent(this@MainActivity, DownloadActivity::class.java)
                        )
                        drawerView.close()
                    }
                    R.id.nav_setting -> {
                        startActivity(
                            Intent(this@MainActivity, SettingActivity::class.java)
                        )
                        drawerView.close()
                    }
                }
                true
            }
        }

        when {
            ShareData.spFirstOpen -> navController.setGraph(R.navigation.navigation_main_with_first_open)
            ShareData.spSecurity -> navController.setGraph(R.navigation.navigation_main_with_authority)
            else -> navController.setGraph(R.navigation.navigation_main_normal)
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

}
