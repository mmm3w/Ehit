package com.mitsuki.ehit.ui.detail.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.uils.MinuteDelay
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ActivityGalleryBinding
import com.mitsuki.ehit.receiver.BatteryReceiver
import com.mitsuki.ehit.ui.detail.adapter.GalleryFragmentAdapter
import com.mitsuki.ehit.ui.detail.dialog.ReadConfigDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

@AndroidEntryPoint
class GalleryActivity : BaseActivity() {

    private var mIndex: Int = 0
    private var mId: Long = -1
    private var mPage: Int = 0
    private lateinit var mToken: String

    private lateinit var mViewPagerAdapter: GalleryFragmentAdapter

    private val mMinuteDelay by lazy { MinuteDelay(this) }

    @SuppressLint("SimpleDateFormat")
    private val mDateFormat = SimpleDateFormat("HH:mm");

    private val binding by viewBinding(ActivityGalleryBinding::inflate)

    private val mBatteryReceiver by lazy { BatteryReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIndex = intent.getIntExtra(DataKey.GALLERY_INDEX, 0)
        mPage = intent.getIntExtra(DataKey.GALLERY_PAGE, 0)
        mId = intent.getLongExtra(DataKey.GALLERY_ID, -1)
        mToken = intent.getStringExtra(DataKey.GALLERY_TOKEN)
            ?: throw IllegalStateException("Missing token")

        updateIndex(mIndex)
        enableReadConfig()

        mViewPagerAdapter = GalleryFragmentAdapter(this, mId, mToken, mPage)
        binding.galleryViewPager.apply {
            adapter = mViewPagerAdapter
            setCurrentItem(mIndex, false)
            offscreenPageLimit = shareData.spPreloadImage
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateIndex(position)
                }
            })
        }

        mMinuteDelay.receiver<Long>("timestamp").observe(this) {
            binding.galleryShowTime.text = mDateFormat.format(it)
        }

        mBatteryReceiver.receiver<Int>("battery").observe(this, this::onBattery)

        showTips()

        registerReceiver(
            mBatteryReceiver,
            BatteryReceiver.intentFilter()
        )?.apply { mBatteryReceiver.postIntentData(this) }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBatteryReceiver)
    }

    private fun updateIndex(index: Int) {
        binding.galleryShowProgress.text =
            String.format(string(R.string.page_separate), index + 1, mPage)
    }

    fun dyNextPage() {
        if (binding.galleryViewPager.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
            nextPage()
        } else {
            previousPage()
        }
    }

    fun dyPreviousPage() {
        if (binding.galleryViewPager.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
            previousPage()
        } else {
            nextPage()
        }
    }

    fun nextPage() {
        val current = binding.galleryViewPager.currentItem
        if (current + 1 >= mPage) {
            //最后一页了
            return
        }
        val targetPage = current + 1
        binding.galleryViewPager.setCurrentItem(targetPage, false)
    }

    fun previousPage() {
        val current = binding.galleryViewPager.currentItem
        if (current - 1 < 0) {
            //第一页了
            return
        }
        val targetPage = current - 1
        binding.galleryViewPager.setCurrentItem(targetPage, false)
    }

    fun showReadConfig() {
        ReadConfigDialog { enableReadConfig() }.show(supportFragmentManager, "config")
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun enableReadConfig() {
        when (memoryData.screenOrientation) {
            0 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            1 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            2 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            3 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }

        when (memoryData.readOrientation) {
            0 -> {
                binding.galleryViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                binding.galleryViewPager.layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            1 -> {
                binding.galleryViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                binding.galleryViewPager.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            2 -> {
                binding.galleryViewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
                binding.galleryViewPager.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
        }

        if (memoryData.keepBright) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        if (memoryData.showTime) {
            binding.galleryShowTime.isVisible = true
            mMinuteDelay.start()
        } else {
            binding.galleryShowTime.isVisible = false
            mMinuteDelay.stop()
        }

        binding.galleryShowBattery.isVisible = memoryData.showBattery
        binding.galleryShowProgress.isVisible = memoryData.showProgress

        if (memoryData.showPagePadding) {
            binding.galleryViewPager.setPageTransformer(MarginPageTransformer(dp2px(20f).roundToInt()))
        } else {
            binding.galleryViewPager.setPageTransformer(null)
        }

        onUiMode(false)
    }

    private fun showTips() {
        if (!shareData.spGalleryTouchHotspotTips) {
            binding.galleryHotspotVisualization.visible {
                shareData.spGalleryTouchHotspotTips = true
            }
        }
    }

    override fun onUiMode(isNightMode: Boolean) {
        if (memoryData.fullScreen) {
            controller.window(
                statusBarHide = true,
                statusBarLight = false,
                statusBarColor = Color.TRANSPARENT,
                navigationBarHide = true,
                navigationBarLight = false,
                navigationBarColor = Color.TRANSPARENT,
                barFit = false
            )
        } else {
            controller.window(
                statusBarHide = false,
                statusBarLight = false,
                statusBarColor = Color.TRANSPARENT,
                navigationBarHide = false,
                navigationBarLight = false,
                navigationBarColor = Color.TRANSPARENT,
                barFit = false
            )
        }
    }

    private fun onBattery(tag: Int) {
        when (tag) {
            BatteryReceiver.BATTERY_LEVEL_CHARGING -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_charging_14)
            BatteryReceiver.BATTERY_LEVEL_0 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_0_bar_14)
            BatteryReceiver.BATTERY_LEVEL_1 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_1_bar_14)
            BatteryReceiver.BATTERY_LEVEL_2 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_2_bar_14)
            BatteryReceiver.BATTERY_LEVEL_3 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_3_bar_14)
            BatteryReceiver.BATTERY_LEVEL_4 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_4_bar_14)
            BatteryReceiver.BATTERY_LEVEL_5 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_5_bar_14)
            BatteryReceiver.BATTERY_LEVEL_6 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_6_bar_14)
            BatteryReceiver.BATTERY_LEVEL_7 -> binding.galleryShowBattery.setImageResource(R.drawable.ic_baseline_battery_full_14)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (memoryData.volumeButtonTurnPages) {
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