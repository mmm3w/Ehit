package com.mitsuki.ehit.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.ui.adapter.GalleryFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_gallery.*

@AndroidEntryPoint
class GalleryActivity : BaseActivity() {

    private var mIndex: Int = 0
    private var mId: Long = -1
    private var mPage: Int = 0
    private lateinit var mToken: String

    private lateinit var mViewPagerAdapter: GalleryFragmentAdapter

    private var isReverse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        mIndex = intent.getIntExtra(DataKey.GALLERY_INDEX, 0)
        mPage = intent.getIntExtra(DataKey.GALLERY_PAGE, 0)
        mId = intent.getLongExtra(DataKey.GALLERY_ID, -1)
        mToken = intent.getStringExtra(DataKey.GALLERY_TOKEN) ?: throw Exception("Missing token")

        updateIndex(mIndex)

        mViewPagerAdapter = GalleryFragmentAdapter(this, isReverse, mId, mToken, mPage)
        gallery_view_pager?.apply {
            adapter = mViewPagerAdapter
            setCurrentItem(if (isReverse) mPage - mIndex - 1 else mIndex, false)
            offscreenPageLimit = 2
        }
        gallery_view_pager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndex(if (isReverse) mPage - position -1 else position)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateIndex(index:Int){
        gallery_index?.text = "${index + 1}/$mPage"
    }
}