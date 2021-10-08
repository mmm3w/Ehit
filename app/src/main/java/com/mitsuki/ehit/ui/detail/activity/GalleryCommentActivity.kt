package com.mitsuki.ehit.ui.detail.activity

import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityGalleryBinding
import com.mitsuki.ehit.databinding.ActivityGalleryCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryCommentActivity : BaseActivity() {

    private val controller by windowController()

    private val binding by viewBinding(ActivityGalleryCommentBinding::inflate)

    private val mAdapter by lazy { ConcatAdapter() }


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

        binding.commentSend.setOnClickListener {

        }

        binding.commentList.apply {
            layoutManager = LinearLayoutManager(this@GalleryCommentActivity)
        }
    }
}