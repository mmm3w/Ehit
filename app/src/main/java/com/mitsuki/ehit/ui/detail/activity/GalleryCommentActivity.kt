package com.mitsuki.ehit.ui.detail.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityGalleryBinding
import com.mitsuki.ehit.databinding.ActivityGalleryCommentBinding
import com.mitsuki.ehit.ui.detail.adapter.GalleryCommentAdapter
import com.mitsuki.ehit.viewmodel.GalleryCommentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryCommentActivity : BaseActivity() {

    private val mViewModel by viewModels<GalleryCommentViewModel>()

    private val controller by windowController()

    private val binding by viewBinding(ActivityGalleryCommentBinding::inflate)

    private val mAdapter by lazy { GalleryCommentAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller.window(
            navigationBarLight = true,
            statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )

        binding.topBar.topBarLayout.elevation = dp2px(4f)
        binding.topBar.topBarLayout.setBackgroundColor(Color.WHITE)
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarText.setText(R.string.text_more_comments)

        mViewModel.initData(intent)

        lifecycleScope.launchWhenCreated {
            mViewModel.commentDataFlow.collect {
                mAdapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.loadStateFlow.collect {
                binding.commentRefresh.isRefreshing = it
            }
        }

        binding.commentSend.setOnClickListener {

        }

        binding.commentRefresh.setOnRefreshListener {
            lifecycleScope.launch { mViewModel.loadComment(false) }
        }

        binding.commentList.apply {
            layoutManager = LinearLayoutManager(this@GalleryCommentActivity)
            adapter = mAdapter
        }

        lifecycleScope.launchWhenCreated { mViewModel.loadComment(false) }
    }
}