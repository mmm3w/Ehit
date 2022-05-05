package com.mitsuki.ehit.ui.detail.activity

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityGalleryBinding
import com.mitsuki.ehit.ui.detail.adapter.GalleryFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : BaseActivity() {

    private var mIndex: Int = 0
    private var mId: Long = -1
    private var mPage: Int = 0
    private lateinit var mToken: String

    private lateinit var mViewPagerAdapter: GalleryFragmentAdapter

    private var isReverse = true

    private val controller by windowController()

    private val binding by viewBinding(ActivityGalleryBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller.window(statusBarHide = true, navigationBarHide = true, barFit = false)

        mIndex = intent.getIntExtra(DataKey.GALLERY_INDEX, 0)
        mPage = intent.getIntExtra(DataKey.GALLERY_PAGE, 0)
        mId = intent.getLongExtra(DataKey.GALLERY_ID, -1)
        mToken = intent.getStringExtra(DataKey.GALLERY_TOKEN)
            ?: throw IllegalStateException("Missing token")

        mViewPagerAdapter = GalleryFragmentAdapter(this, isReverse, mId, mToken, mPage)
        binding.galleryViewPager.apply {
            adapter = mViewPagerAdapter
            setCurrentItem(if (isReverse) mPage - mIndex - 1 else mIndex, false)
            offscreenPageLimit = 3
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateIndex(if (isReverse) mPage - position - 1 else position)
                }
            })
        }

        updateIndex(mIndex)
        //屏幕方向，跟随系统、横屏、竖屏、自动旋转
        //阅读方向，流式（这个很麻烦，影响界面实现的基本设计了）、从左至右、从右至左（日漫）
        //缩放：原始尺寸、匹配宽度、匹配高度、自动匹配、固定缩放（所有图片跟随同一个缩放）
        //开页位置，不明的配置，先扔着
        //屏幕常亮
        //时钟、电量、进度
        //页面间隔、音量键翻页
        //全屏
        //手动亮度
    }

    private fun updateIndex(index: Int) {
        binding.galleryIndex.text =
            String.format(string(R.string.page_separate), index + 1, mPage)
    }

    fun nextPage() {
        val current = binding.galleryViewPager.currentItem
        val targetPage = if (isReverse) current - 1 else current + 1

        if ((isReverse && targetPage < 0) || (!isReverse && targetPage >= mPage)) {
            //最后一页了
            return
        }
        binding.galleryViewPager.setCurrentItem(targetPage, false)
    }

    fun previousPage() {
        val current = binding.galleryViewPager.currentItem
        val targetPage = if (isReverse) current + 1 else current - 1

        if ((isReverse && targetPage >= mPage) || (!isReverse && targetPage < 0)) {
            //第一页了
            return
        }
        binding.galleryViewPager.setCurrentItem(targetPage, false)
    }

}