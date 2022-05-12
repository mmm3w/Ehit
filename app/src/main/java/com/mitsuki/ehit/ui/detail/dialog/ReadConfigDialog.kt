package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.showSelectMenu
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.databinding.DialogReadConfigBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ReadConfigDialog :
    BindingDialogFragment<DialogReadConfigBinding>(
        R.layout.dialog_read_config,
        DialogReadConfigBinding::bind
    ) {

    init {
        title(text(R.string.text_setting_read))

        positiveBtn(text(R.string.text_confirm)) {
            memoryData.keepBright = binding.readConfigKeepBrightSwitch.isChecked
            memoryData.showTime = binding.readConfigShowTimeSwitch.isChecked
            memoryData.showBattery = binding.readConfigShowBatterySwitch.isChecked
            memoryData.showProgress = binding.readConfigShowProgressSwitch.isChecked
            memoryData.showPagePadding = binding.readConfigShowPagePaddingSwitch.isChecked
            memoryData.volumeButtonTurnPages =
                binding.readConfigVolumeButtonTurnPagesSwitch.isChecked
            memoryData.fullScreen = binding.readConfigFullScreenSwitch.isChecked
            memoryData.screenOrientation = screenOrientation
            memoryData.readOrientation = readOrientation
            memoryData.imageZoom = imageZoom

            dismiss()
        }
    }

    @Inject
    lateinit var memoryData: MemoryData

    private var screenOrientation = -1
        set(value) {
            if (value != field) {
                field = value
                binding.readConfigScreenOrientationSelect.text = screenOrientationText(field)
            }
        }

    private var readOrientation = -1
        set(value) {
            if (value != field) {
                field = value
                binding.readConfigReadingDirectionSelect.text = readOrientationText(field)
            }
        }

    private var imageZoom = -1
        set(value) {
            if (value != field) {
                field = value
                binding.readConfigImageZoomSelect.text = imageZoomText(field)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenOrientation = memoryData.screenOrientation
        readOrientation = memoryData.readOrientation
        imageZoom = memoryData.imageZoom

        binding.readConfigScreenOrientationSelect.setOnClickListener {
            showSelectMenu(requireContext(), it, R.menu.menu_screen_orientation) { menuId ->
                when (menuId) {
                    R.id.screen_default -> screenOrientation = 0
                    R.id.screen_vertical -> screenOrientation = 1
                    R.id.screen_horizontal -> screenOrientation = 2
                    R.id.screen_auto -> screenOrientation = 3
                }
            }
        }
        binding.readConfigReadingDirectionSelect.setOnClickListener {
            showSelectMenu(requireContext(), it, R.menu.menu_reading_direction) { menuId ->
                when (menuId) {
                    R.id.read_rtl -> readOrientation = 0
                    R.id.read_ltr -> readOrientation = 1
                    R.id.read_ttb -> readOrientation = 2
                }
            }
        }
        binding.readConfigImageZoomSelect.setOnClickListener {
            showSelectMenu(requireContext(), it, R.menu.menu_image_zoom) { menuId ->
                when (menuId) {
                    R.id.zoom_adapt_screen -> imageZoom = 0
                    R.id.zoom_adapt_width -> imageZoom = 1
                    R.id.zoom_adapt_height -> imageZoom = 2
                    R.id.zoom_original -> imageZoom = 3
                    R.id.zoom_fix_scale -> imageZoom = 4
                }
            }
        }

        binding.readConfigKeepBrightSwitch.isChecked = memoryData.keepBright
        binding.readConfigShowTimeSwitch.isChecked = memoryData.showTime
        binding.readConfigShowBatterySwitch.isChecked = memoryData.showBattery
        binding.readConfigShowProgressSwitch.isChecked = memoryData.showProgress
        binding.readConfigShowPagePaddingSwitch.isChecked = memoryData.showPagePadding
        binding.readConfigVolumeButtonTurnPagesSwitch.isChecked = memoryData.volumeButtonTurnPages
        binding.readConfigFullScreenSwitch.isChecked = memoryData.fullScreen
    }

    private fun screenOrientationText(index: Int): CharSequence {
        val sid = when (index) {
            0 -> R.string.text_screen_default
            1 -> R.string.text_screen_vertical
            2 -> R.string.text_screen_horizontal
            3 -> R.string.text_screen_auto
            else -> throw IllegalArgumentException()
        }
        return text(sid)
    }

    private fun readOrientationText(index: Int): CharSequence {
        val sid = when (index) {
            0 -> R.string.text_read_rtl
            1 -> R.string.text_read_ltr
            2 -> R.string.text_read_ttb
            else -> throw IllegalArgumentException()
        }
        return text(sid)
    }

    private fun imageZoomText(index: Int): CharSequence {
        val sid = when (index) {
            0 -> R.string.text_zoom_adapt_screen
            1 -> R.string.text_zoom_adapt_width
            2 -> R.string.text_zoom_adapt_height
            3 -> R.string.text_zoom_original
            4 -> R.string.text_zoom_fix_scale
            else -> throw IllegalArgumentException()
        }
        return text(sid)
    }


}