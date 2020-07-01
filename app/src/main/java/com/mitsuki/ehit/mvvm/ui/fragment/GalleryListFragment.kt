package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager

import com.mitsuki.armory.TransparentDivider
import com.mitsuki.armory.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.ui.adapter.GalleryAdapter
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryListFragment : BaseFragment<MainViewModel>() {

    override val mViewModel: MainViewModel by activityViewModels()
    private val mAdapter: GalleryAdapter = GalleryAdapter()
    private val divider = TransparentDivider(dp2px(2f))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //在使用navigation的时候
        //框架为了减少内存占用会销毁View但不会销毁fragment实例
        //view相关以外的初始化工作应该放在此处
        mAdapter.observeItemEvent().observe(this@GalleryListFragment, Observer { gallery ->
            mViewModel.mCurrentGallery = gallery
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(R.id.action_galleryListFragment_to_galleryDetailFragment)
        })

        lifecycleScope.launch {
            mViewModel.galleryList().observe(this@GalleryListFragment, Observer {
                mAdapter.submitData(lifecycle, it)
            })
        }
    }

    override fun initView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_list, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        galleryList.adapter = mAdapter
        galleryList.layoutManager = LinearLayoutManager(activity)
        galleryList.addItemDecoration(divider)
    }
}