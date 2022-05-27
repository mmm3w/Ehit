package com.mitsuki.ehit.ui.download.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.*
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityDownloadBinding
import com.mitsuki.ehit.model.activityresult.ExportZipActivityResultCallback
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.ui.common.dialog.BottomMenuDialogFragment
import com.mitsuki.ehit.ui.download.ControlAnimate
import com.mitsuki.ehit.ui.download.adapter.DownloadAdapter
import com.mitsuki.ehit.ui.download.adapter.ListItemTouchCallback
import com.mitsuki.ehit.service.download.DownloadBroadcast
import com.mitsuki.ehit.service.download.DownloadService
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import com.mitsuki.ehit.ui.main.MainActivity
import com.mitsuki.ehit.viewmodel.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadActivity : BaseActivity() {

    private val binding by viewBinding(ActivityDownloadBinding::inflate)

    private val mViewModel by viewModels<DownloadViewModel>()

    private val controlAnimate by lazy { ControlAnimate() }

    private val mStatesAdapter by lazy { ListStatesAdapter() }
    private val mMainAdapter by lazy { DownloadAdapter() }
    private val mAdapter by lazy { ConcatAdapter(mStatesAdapter, mMainAdapter) }


    private val mExportZip = ExportZipActivityResultCallback(this, activityResultRegistry)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.getDefaultNightMode()
        binding.topBar.topBarText.text = getText(R.string.text_download)
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarStart.setOnClickListener {
//            DownloadService.startAllDownload(this)
        }
        binding.topBar.topBarPause.setOnClickListener {
//            DownloadBroadcast.sendStopAll()
        }

        mMainAdapter.receiver<DownloadListInfo>("option")
            .isClick()
            .observe(this, this::onItemOption)
        mMainAdapter.receiver<DownloadListInfo>("detail")
            .isClick()
            .observe(this, this::openDetail)
        mMainAdapter.receiver<DownloadListInfo>("read")
            .isClick()
            .observe(this, this::openRead)

        ItemTouchHelper(ListItemTouchCallback {
            mMainAdapter.item(it).apply {
                DownloadBroadcast.sendStop(gid, token)
                mViewModel.deleteDownload(gid, token)
            }
        }).attachToRecyclerView(binding.downloadList)

        lifecycleScope.launchWhenCreated {
            binding.downloadList.apply {
                layoutManager = LinearLayoutManager(this@DownloadActivity)
                adapter = mAdapter
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.downloadList().collect {
                mStatesAdapter.listState =
                    if (it.isEmpty()) ListStatesAdapter.ListState.Message(text(R.string.text_content_empty)) else ListStatesAdapter.ListState.None
                mMainAdapter.submitData(it)
            }
        }

    }

    private fun onItemOption(info: DownloadListInfo) {
        BottomMenuDialogFragment(
            intArrayOf(
                R.string.text_read,
                R.string.text_detail,
                R.string.text_export_zip,
                R.string.text_export_image,
                R.string.text_delete,
            )
        ) {
            when (it) {
                0 -> openRead(info)
                1 -> openDetail(info)
                2 -> mExportZip.pack(info.title, info.gid, info.token)
                3 -> {
                    //TODO
                }
                4 -> with(info) {
                    DownloadBroadcast.sendStop(gid, token)
                    mViewModel.deleteDownload(gid, token)
                }
            }
            true
        }.show(supportFragmentManager, "option")
    }

    private fun openDetail(info: DownloadListInfo) {
        startActivity(Intent(this, MainActivity::class.java)
            .apply { putExtra(DataKey.GALLERY_INFO, info.toGallery()) })
    }

    private fun openRead(info: DownloadListInfo) {
        //TODO
    }
}