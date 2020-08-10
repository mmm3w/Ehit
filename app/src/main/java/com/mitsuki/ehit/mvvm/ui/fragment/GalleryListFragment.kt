package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.ui.adapter.GalleryAdapter
import com.mitsuki.ehit.mvvm.ui.widget.FloatToolbarBehavior
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GalleryListFragment : BaseFragment<MainViewModel>(R.layout.fragment_gallery_list) {

    override val mViewModel: MainViewModel by activityViewModels()
    private val mAdapter: GalleryAdapter by lazy { GalleryAdapter() }

//    private val mBehavior: FloatToolbarBehavior by lazy { FloatToolbarBehavior() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //在使用navigation的时候
        //框架为了减少内存占用会销毁View但不会销毁fragment实例
        //view相关以外的初始化工作应该放在此处
        mAdapter.observeItemEvent().observe(this@GalleryListFragment, Observer { gallery ->
            mViewModel.mCurrentGallery = gallery
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(R.id.action_gallery_list_fragment_to_gallery_detail_fragment)
        })

        mViewModel.galleryList().observe(this@GalleryListFragment, Observer {
            mAdapter.submitData(lifecycle, it)
        })
//
//        //包含应用锁定，导航向锁定界面
//        if (false) Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
//            .navigate(R.id.action_gallery_list_fragment_to_security_fragment)
//
//        //首次安装，导航向引导
//        if (false) Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
//            .navigate(R.id.action_gallery_list_fragment_to_disclaimer_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        gallery_list?.apply {
//            layoutManager = LinearLayoutManager(activity)
//            adapter = mAdapter
//        }
//
//        //防止页面重建的时候控件位置异常
//        float_bar?.layoutParams?.apply {
//            if (this is CoordinatorLayout.LayoutParams && behavior != mBehavior) {
//                behavior = mBehavior
//            }
//        }

        gallery_list?.apply {
            //配置recycleView
            recyclerView {
                layoutManager = LinearLayoutManager(activity)
                adapter = mAdapter
            }


        }

        test_btn?.setOnClickListener {
            gallery_list.isRefreshing = !gallery_list.isRefreshing
        }
    }
}