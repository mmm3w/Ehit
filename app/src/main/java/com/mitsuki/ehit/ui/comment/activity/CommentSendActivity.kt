package com.mitsuki.ehit.ui.comment.activity

import android.os.Bundle
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.base.BindingActivity
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.databinding.ActivityCommentSendBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 承担评论发送和编辑功能
 */
@AndroidEntryPoint
class CommentSendActivity :
    BindingActivity<ActivityCommentSendBinding>(ActivityCommentSendBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.topBar.topBarLayout.elevation = dp2px(4f)
        binding.topBar.topBarLayout.setBackgroundColor(color(R.color.background_color_general))
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }




    }
}