package com.mitsuki.ehit.ui.comment.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityGalleryCommentBinding
import com.mitsuki.ehit.ui.comment.adapter.CommentLoadAdapter
import com.mitsuki.ehit.ui.comment.adapter.GalleryCommentAdapter
import com.mitsuki.ehit.viewmodel.GalleryCommentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GalleryCommentActivity : BaseActivity() {

    private val mViewModel: GalleryCommentViewModel by viewModels()

    private val controller by windowController()

    private val binding by viewBinding(ActivityGalleryCommentBinding::inflate)

    private val mInitAdapter by lazy { CommentLoadAdapter{ mViewModel.loadComment(false) } }
    private val mMainAdapter by lazy { GalleryCommentAdapter() }
    //TODO 还缺一个empty adapter
    private val mAdapter by lazy { ConcatAdapter(mInitAdapter, mMainAdapter) }

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
            mViewModel.loadStateFlow.collect {
                if (mInitAdapter.isOver) {
                    binding.commentRefresh.isRefreshing = it is LoadState.Loading
                } else {
                    mInitAdapter.loadState = it
                }
                binding.commentRefresh.isEnabled = mInitAdapter.isOver
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.commentDataFlow.collect { mMainAdapter.submitData(it) }
        }

        binding.commentRefresh.setOnRefreshListener {
            mViewModel.loadComment(false)
        }

        binding.commentList.apply {
            layoutManager = LinearLayoutManager(this@GalleryCommentActivity)
            adapter = mAdapter
        }

        mViewModel.loadComment(false)
    }
}