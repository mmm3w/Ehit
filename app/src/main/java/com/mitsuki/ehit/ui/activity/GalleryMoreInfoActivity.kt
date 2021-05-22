package com.mitsuki.ehit.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extend.whiteStyle
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.model.entity.GalleryDetail
import com.mitsuki.ehit.ui.adapter.MoreInfoAdapter
import kotlinx.android.synthetic.main.activity_more_info.*
import kotlinx.android.synthetic.main.item_more_info.*
import kotlinx.android.synthetic.main.top_bar_normal_ver.*

class GalleryMoreInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)
        whiteStyle()

        top_bar_back?.setOnClickListener { onBackPressed() }
        top_bar_text?.text = getText(R.string.text_more_information)


        lifecycleScope.launchWhenCreated {
            val gid = intent?.getLongExtra(DataKey.GALLERY_ID, -1) ?: throw IllegalStateException()
            val token =
                intent?.getStringExtra(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
            val data = RoomData.galleryDao.queryGalleryDetail(gid, token, false)
                ?: throw IllegalStateException()
            val infoAdapter = MoreInfoAdapter(data)
            more_info_target?.apply {
                layoutManager = LinearLayoutManager(this@GalleryMoreInfoActivity)
                adapter = infoAdapter
            }
        }


    }
}