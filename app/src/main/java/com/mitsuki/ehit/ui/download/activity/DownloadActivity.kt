package com.mitsuki.ehit.ui.download.activity

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityDownloadBinding
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.ui.download.ControlAnimate
import com.mitsuki.ehit.ui.download.adapter.DownloadAdapter
import com.mitsuki.ehit.ui.download.adapter.ListItemTouchCallback
import com.mitsuki.ehit.ui.download.service.DownloadBroadcast
import com.mitsuki.ehit.ui.download.service.DownloadService
import com.mitsuki.ehit.viewmodel.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadActivity : BaseActivity() {

    private val binding by viewBinding(ActivityDownloadBinding::inflate)
    private val controller by windowController()
    private val mViewModel by viewModels<DownloadViewModel>()

    private val controlAnimate by lazy { ControlAnimate() }

    private val mMainAdapter by lazy { DownloadAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller.window(
            navigationBarLight = true,
            statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )
        binding.topBar.topBarText.text = getText(R.string.text_download)
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarStart.setOnClickListener {
            DownloadService.startAllDownload(this)
        }
        binding.topBar.topBarPause.setOnClickListener {
            DownloadBroadcast.sendStopAll()
        }

        mMainAdapter.receiver<DownloadListInfo>("option").observe(this, this::onItemOption)

        ItemTouchHelper(ListItemTouchCallback {
            mMainAdapter.item(it).apply {
                DownloadBroadcast.sendStop(gid, token)
                mViewModel.deleteDownload(gid, token)
            }
        }).attachToRecyclerView(binding.downloadList)

        lifecycleScope.launchWhenCreated {
            binding.downloadList.apply {
                layoutManager = LinearLayoutManager(this@DownloadActivity)
                adapter = mMainAdapter
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.downloadList().collect {
                mMainAdapter.submitData(it)
            }
        }

    }

    fun onItemOption(info: DownloadListInfo) {

    }

}