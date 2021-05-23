package com.mitsuki.ehit.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.WindowController
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ActivityMoreInfoBinding
import com.mitsuki.ehit.ui.adapter.MoreInfoAdapter

class GalleryMoreInfoActivity : BaseActivity() {
    private val binding by viewBinding(ActivityMoreInfoBinding::inflate)
    private val controller by lazy { WindowController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)
        controller.window(
            navigationBarLight = true, statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )

        binding.topBar.topBarBack.setOnClickListener { onBackPressed() }
        binding.topBar.topBarText.text = getText(R.string.text_more_information)

        lifecycleScope.launchWhenCreated {
            val gid = intent?.getLongExtra(DataKey.GALLERY_ID, -1) ?: throw IllegalStateException()
            val token =
                intent?.getStringExtra(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
            val data = RoomData.galleryDao.queryGalleryDetail(gid, token, false)
                ?: throw IllegalStateException()
            val infoAdapter = MoreInfoAdapter(data)
            binding.moreInfoTarget.apply {
                layoutManager = LinearLayoutManager(this@GalleryMoreInfoActivity)
                adapter = infoAdapter
            }
        }
    }
}