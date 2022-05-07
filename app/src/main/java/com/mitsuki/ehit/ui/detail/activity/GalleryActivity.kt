package com.mitsuki.ehit.ui.detail.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityGalleryBinding
import com.mitsuki.ehit.ui.detail.adapter.GalleryFragmentAdapter
import com.mitsuki.ehit.ui.detail.dialog.ReadConfigDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

        enableReadConfig()


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

        showTips()
    }

    private fun updateIndex(index: Int) {
        binding.galleryShowProgress.text =
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

    fun showReadConfig() {
        ReadConfigDialog().show(supportFragmentManager, "config")
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun enableReadConfig() {
        //屏幕方向
        when (shareData.screenOrientation) {
            0 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            1 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            2 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            3 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }

        if (shareData.keepBright) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        binding.galleryShowTime.isVisible = shareData.showTime
        binding.galleryShowBattery.isVisible = shareData.showBattery
        binding.galleryShowProgress.isVisible = shareData.showProgress
    }

    private fun showTips() {
        if (!shareData.spGalleryTouchHotspotTips) {
            binding.galleryHotspotVisualization.visible {
                shareData.spGalleryTouchHotspotTips = true
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (shareData.volumeButtonTurnPages) {
            return when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    nextPage()
                    true
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    previousPage()
                    true
                }
                else -> super.onKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}