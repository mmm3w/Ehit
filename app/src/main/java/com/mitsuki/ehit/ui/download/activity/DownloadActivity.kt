package com.mitsuki.ehit.ui.download.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityDownloadBinding
import com.mitsuki.ehit.ui.download.adapter.ControlAdapter
import com.mitsuki.ehit.ui.download.adapter.DownloadAdapter
import com.mitsuki.ehit.viewmodel.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadActivity : BaseActivity() {

    private val binding by viewBinding(ActivityDownloadBinding::inflate)
    private val controller by windowController()
    private val mViewModel by viewModels<DownloadViewModel>()

    private val mControlAdapter by lazy { ControlAdapter() }
    private val mMainAdapter by lazy { DownloadAdapter() }
    private val mAdapter by lazy { ConcatAdapter(mControlAdapter, mMainAdapter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller.window(
            navigationBarLight = true,
            statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )
        binding.topBar.topBarText.text = getText(R.string.text_download)

        lifecycleScope.launchWhenCreated {
            binding.downloadList.apply {
                layoutManager = LinearLayoutManager(this@DownloadActivity)
                adapter = mAdapter
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.downloadList().collect { mMainAdapter.submitData(it) }
        }
    }


}