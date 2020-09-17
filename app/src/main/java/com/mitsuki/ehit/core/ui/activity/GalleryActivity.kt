package com.mitsuki.ehit.core.ui.activity

import android.os.Bundle
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.core.ui.adapter.GalleryFragmentAdapter
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : BaseActivity() {

    private val mViewPagerAdapter by lazy { GalleryFragmentAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        gallery_view_pager?.apply {
            adapter = mViewPagerAdapter
        }
    }


}