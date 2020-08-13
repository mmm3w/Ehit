package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.toast
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.core.ui.adapter.GalleryAdapter
import com.mitsuki.ehit.core.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list) {

    private val mViewModel: MainViewModel by activityViewModels()
    private val mAdapter: GalleryAdapter by lazy { GalleryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //在使用navigation的时候
        //框架为了减少内存占用会销毁View但不会销毁fragment实例
        //view相关以外的初始化工作应该放在此处
        mAdapter.currentItem.observe(this@GalleryListFragment, Observer { gallery ->
            mViewModel.mCurrentGallery = gallery
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(R.id.action_gallery_list_fragment_to_gallery_detail_fragment)
        })

        lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest {
                if (it.refresh !is LoadState.Loading) gallery_list?.isRefreshing = false
                gallery_list?.setLoading(
                    it.append is LoadState.Loading,
                    it.append is LoadState.Error
                )
            }
        }


        mAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> toast("刷新失败")
            }

            when (it.append) {
                is LoadState.Error -> toast("加载失败")
            }
        }

        mViewModel.galleryList.observe(this, Observer { mAdapter.submitData(lifecycle, it) })

        securityCheck()
        installCheck()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gallery_list?.apply {
            //配置recycleView
            recyclerView {
                layoutManager = LinearLayoutManager(activity)
                adapter = mAdapter
            }

            refreshListener = { mAdapter.refresh() }
            loadListener = { mAdapter.retry() }
        }

    }

    @Suppress("ConstantConditionIf")
    private fun securityCheck() {
        //包含应用锁定，导航向锁定界面
        if (false) Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(R.id.action_gallery_list_fragment_to_security_fragment)
    }

    @Suppress("ConstantConditionIf")
    private fun installCheck() {
        //首次安装，导航向引导
        if (false) Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(R.id.action_gallery_list_fragment_to_disclaimer_fragment)
    }
}