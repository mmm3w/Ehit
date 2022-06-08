package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.getSystemBrightness
import com.mitsuki.ehit.crutch.extensions.setBrightness
import com.mitsuki.ehit.crutch.extensions.showSelectMenu
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.databinding.DialogReadConfigBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class ReadConfigDialog(private val confirmAction: () -> Unit) :
    BindingDialogFragment<DialogReadConfigBinding>(
        R.layout.dialog_read_config,
        DialogReadConfigBinding::bind
    ) {

    init {
        title(text(R.string.text_setting_read))

        positiveBtn(text(R.string.text_confirm)) {
            binding?.apply {
                memoryData.keepBright = readConfigKeepBrightSwitch.isChecked
                memoryData.showTime = readConfigShowTimeSwitch.isChecked
                memoryData.showBattery = readConfigShowBatterySwitch.isChecked
                memoryData.showProgress = readConfigShowProgressSwitch.isChecked
                memoryData.showPagePadding = readConfigShowPagePaddingSwitch.isChecked
                memoryData.volumeButtonTurnPages =
                    readConfigVolumeButtonTurnPagesSwitch.isChecked
                memoryData.fullScreen = readConfigFullScreenSwitch.isChecked

                if (readConfigAutoBrightnessSwitch.isChecked) {
                    memoryData.customBrightness = -1f
                } else {
                    memoryData.customBrightness = readConfigBrightnessAdjust.progress / 100f
                }
            }
            memoryData.screenOrientation = screenOrientation
            memoryData.readOrientation = readOrientation
            memoryData.imageZoom = imageZoom

            confirmAction()
            dismiss()
        }
    }

    @Inject
    lateinit var memoryData: MemoryData

    private var screenOrientation = -1
        set(value) {
            if (value != field) {
                field = value
                binding?.readConfigScreenOrientationSelect?.text = screenOrientationText(field)
            }
        }

    private var readOrientation = -1
        set(value) {
            if (value != field) {
                field = value
                binding?.readConfigReadingDirectionSelect?.text = readOrientationText(field)
            }
        }

    private var imageZoom = -1
        set(value) {
            if (value != field) {
                field = value
                binding?.readConfigImageZoomSelect?.text = imageZoomText(field)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenOrientation = memoryData.screenOrientation
        readOrientation = memoryData.readOrientation
        imageZoom = memoryData.imageZoom

        binding?.apply {
            readConfigScreenOrientationSelect.setOnClickListener {
                showSelectMenu(requireContext(), it, R.menu.menu_screen_orientation) { menuId ->
                    when (menuId) {
                        R.id.screen_default -> screenOrientation = 0
                        R.id.screen_vertical -> screenOrientation = 1
                        R.id.screen_horizontal -> screenOrientation = 2
                        R.id.screen_auto -> screenOrientation = 3
                    }
                }
            }
            readConfigReadingDirectionSelect.setOnClickListener {
                showSelectMenu(requireContext(), it, R.menu.menu_reading_direction) { menuId ->
                    when (menuId) {
                        R.id.read_rtl -> readOrientation = 0
                        R.id.read_ltr -> readOrientation = 1
                        R.id.read_ttb -> readOrientation = 2
                    }
                }
            }
            readConfigImageZoomSelect.setOnClickListener {
                showSelectMenu(requireContext(), it, R.menu.menu_image_zoom) { menuId ->
                    when (menuId) {
                        R.id.zoom_adapt_screen -> imageZoom = 0
                        R.id.zoom_adapt_width -> imageZoom = 1
                        R.id.zoom_adapt_height -> imageZoom = 2
//                    R.id.zoom_original -> imageZoom = 3
//                    R.id.zoom_fix_scale -> imageZoom = 4
                    }
                }
            }

            readConfigKeepBrightSwitch.isChecked = memoryData.keepBright
            readConfigShowTimeSwitch.isChecked = memoryData.showTime
            readConfigShowBatterySwitch.isChecked = memoryData.showBattery
            readConfigShowProgressSwitch.isChecked = memoryData.showProgress
            readConfigShowPagePaddingSwitch.isChecked = memoryData.showPagePadding
            readConfigVolumeButtonTurnPagesSwitch.isChecked = memoryData.volumeButtonTurnPages
            readConfigFullScreenSwitch.isChecked = memoryData.fullScreen


            readConfigAutoBrightnessSwitch.apply {
                isChecked = memoryData.customBrightness == -1f
                setOnCheckedChangeListener { _, isChecked ->
                    readConfigBrightnessAdjust.isEnabled = !isChecked
                }
            }
            readConfigBrightnessAdjust.apply {
                isEnabled = memoryData.customBrightness != -1f
                progress = if (memoryData.customBrightness == -1f) {
                    (requireContext().getSystemBrightness() * 100).roundToInt()
                } else {
                    (memoryData.customBrightness * 100).roundToInt()
                }
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) {
                            requireActivity().setBrightness(progress / 100f)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }

        }


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