package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.ui.adapter.GalleryDetailAdapter
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_gallery_detail.*
import kotlinx.coroutines.launch

class GalleryDetailFragment : BaseFragment<MainViewModel>() {

    override val mViewModel: MainViewModel by activityViewModels()
    private val mAdapter = GalleryDetailAdapter()

    private val mSpanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position < 5) 3 else 1
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mViewModel.galleryDetail()?.observe(this@GalleryDetailFragment, Observer {
                mAdapter.submitData(lifecycle, it)
            })
        }
    }

    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_detail, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        galleryDetailList.adapter = mAdapter
        galleryDetailList.layoutManager =
            GridLayoutManager(activity, 3).apply { spanSizeLookup = mAdapter.mSpanSizeLookup }
    }
}