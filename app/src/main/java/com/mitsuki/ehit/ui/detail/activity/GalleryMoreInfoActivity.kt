package com.mitsuki.ehit.ui.detail.activity

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityMoreInfoBinding
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.ui.detail.adapter.MoreInfoAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryMoreInfoActivity : BaseActivity() {
    private val binding by viewBinding(ActivityMoreInfoBinding::inflate)

    @Inject
    lateinit var galleryDao: GalleryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.topBar.topBarLayout.elevation = dp2px(4f)
        binding.topBar.topBarLayout.setBackgroundColor(color(R.color.background_color_general))
        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarText.text = getText(R.string.text_more_information)

        lifecycleScope.launchWhenCreated {
            val gid = intent?.getLongExtra(DataKey.GALLERY_ID, -1) ?: throw IllegalStateException()
            val token =
                intent?.getStringExtra(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
            val data = galleryDao.queryGalleryDetail(gid, token, false)
                ?: throw IllegalStateException()
            val infoAdapter = MoreInfoAdapter(data)
            binding.moreInfoTarget.apply {
                layoutManager = LinearLayoutManager(this@GalleryMoreInfoActivity)
                adapter = infoAdapter
            }
        }
    }
}