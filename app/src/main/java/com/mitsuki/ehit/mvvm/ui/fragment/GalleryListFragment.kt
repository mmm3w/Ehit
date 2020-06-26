package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.mitsuki.armory.TransparentDivider
import com.mitsuki.armory.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.ui.adapter.GalleryAdapter
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryListFragment : BaseFragment<MainViewModel>() {

    override val mViewModel: MainViewModel by viewModels()
    private val mAdapter: GalleryAdapter = GalleryAdapter()

    override fun initView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_list, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        galleryList.layoutManager = LinearLayoutManager(activity)
        galleryList.adapter = mAdapter
        galleryList.addItemDecoration(TransparentDivider(dp2px(2f)))

        lifecycleScope.launch {
            mViewModel.data().collectLatest {
                mAdapter.submitData(it)
            }
        }

    }

}