package com.mitsuki.ehit.ui.comment.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.uils.InitialGate
import com.mitsuki.ehit.databinding.ActivityGalleryCommentBinding
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.ui.comment.adapter.GalleryCommentAdapter
import com.mitsuki.ehit.ui.comment.adapter.LoadAllCommentAdapter
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import com.mitsuki.ehit.viewmodel.GalleryCommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryCommentActivity :
    BindingActivity<ActivityGalleryCommentBinding>(ActivityGalleryCommentBinding::inflate) {

    private val mViewModel: GalleryCommentViewModel by viewModels()


    private val mMainAdapter by lazy { GalleryCommentAdapter() }
    private val mStatesAdapter by lazy { ListStatesAdapter { mViewModel.loadComment(false) } }
    private val mLoadActionAdapter by lazy {
        LoadAllCommentAdapter { mViewModel.loadComment(true) }
    }

    private val mAdapter by lazy { ConcatAdapter(mStatesAdapter, mMainAdapter, mLoadActionAdapter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.topBar.topBarLayout.elevation = dp2px(4f)
        binding.topBar.topBarLayout.setBackgroundColor(color(R.color.background_color_general))
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarText.apply {
            text = intent?.getStringExtra(DataKey.GALLERY_NAME) ?: text(R.string.text_more_comments)
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
            setPadding(0, 0, dp2px(16F).toInt(), 0)
        }

        mViewModel.initData(intent)


        mMainAdapter.receiver<Pair<Int, Comment>>("VoteUp").observe(this) {
            mViewModel.voteComment(it.first, it.second, 1)
        }

        mMainAdapter.receiver<Pair<Int, Comment>>("VoteDown").observe(this) {
            mViewModel.voteComment(it.first, it.second, -1)
        }

        mViewModel.receiver<NotifyData<Comment>>("vote").observe(this) {
            mMainAdapter.updateData(lifecycle, it)
        }

        mViewModel.viewStates.observe(this) {
            binding.commentRefresh.isRefreshing = it.refreshState
            mStatesAdapter.listState = it.listState
            mLoadActionAdapter.loadState = it.loadAllState
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.commentDataFlow.collect {
                if (it.isEmpty()) {
                    mStatesAdapter.listState =
                        ListStatesAdapter.ListState.Message(string(R.string.text_no_comments))
                } else {
                    mStatesAdapter.listState = ListStatesAdapter.ListState.None
                }
                mMainAdapter.submitData(it)
            }
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