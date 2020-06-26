package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseFragment

class GalleryDetailFragment : BaseFragment<MainViewModel>() {

    override val mViewModel: MainViewModel
        get() = TODO("Not yet implemented")

    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_detail, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}