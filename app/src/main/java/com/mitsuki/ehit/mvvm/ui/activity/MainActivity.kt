package com.mitsuki.ehit.mvvm.ui.activity

import android.os.Bundle
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.viewmodel.MainViewModel
import com.mitsuki.mvvm.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel>() {

    override val mViewModel: MainViewModel
        get() = TODO("Not yet implemented")

    override fun initView(savedInstanceState: Bundle?): Int = R.layout.activity_main

    override fun initData(savedInstanceState: Bundle?) {

    }


}
