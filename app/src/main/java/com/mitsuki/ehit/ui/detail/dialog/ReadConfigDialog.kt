package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extensions.text
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
            shareData.keepBright = binding.readConfigKeepBrightSwitch.isChecked
            shareData.showTime = binding.readConfigShowTimeSwitch.isChecked
            shareData.showBattery = binding.readConfigShowBatterySwitch.isChecked
            shareData.showProgress = binding.readConfigShowProgressSwitch.isChecked
            shareData.showPagePadding = binding.readConfigShowPagePaddingSwitch.isChecked
            shareData.volumeButtonTurnPages =
                binding.readConfigVolumeButtonTurnPagesSwitch.isChecked
            shareData.fullScreen = binding.readConfigFullScreenSwitch.isChecked
            shareData.screenOrientation = screenOrientation
            shareData.readOrientation = readOrientation
            shareData.imageZoom = imageZoom

            dismiss()
        }
    }

    @Inject
    lateinit var shareData: ShareData

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
                binding.readConfigReadOrientationSelect.text = readOrientationText(field)
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
        screenOrientation = shareData.screenOrientation
        readOrientation = shareData.readOrientation
        imageZoom = shareData.imageZoom

        binding.readConfigScreenOrientationSelect.setOnClickListener {
            showSelectMenu(it, R.menu.menu_screen_orientation) { menuId ->
                when (menuId) {
                    R.id.screen_default -> screenOrientation = 0
                    R.id.screen_vertical -> screenOrientation = 1
                    R.id.screen_horizontal -> screenOrientation = 2
                    R.id.screen_auto -> screenOrientation = 3
                }
            }
        }
        binding.readConfigReadOrientationSelect.setOnClickListener {
            showSelectMenu(it, R.menu.menu_read_orientation) { menuId ->
                when (menuId) {
                    R.id.read_rtl -> readOrientation = 0
                    R.id.read_ltr -> readOrientation = 1
                    R.id.read_ttb -> readOrientation = 2
                }
            }
        }
        binding.readConfigImageZoomSelect.setOnClickListener {
            showSelectMenu(it, R.menu.menu_image_zoom) { menuId ->
                when (menuId) {
                    R.id.zoom_adapt_screen -> imageZoom = 0
                    R.id.zoom_adapt_width -> imageZoom = 1
                    R.id.zoom_adapt_height -> imageZoom = 2
                    R.id.zoom_original -> imageZoom = 3
                    R.id.zoom_fix_scale -> imageZoom = 4
                }
            }
        }

        binding.readConfigKeepBrightSwitch.isChecked = shareData.keepBright
        binding.readConfigShowTimeSwitch.isChecked = shareData.showTime
        binding.readConfigShowBatterySwitch.isChecked = shareData.showBattery
        binding.readConfigShowProgressSwitch.isChecked = shareData.showProgress
        binding.readConfigShowPagePaddingSwitch.isChecked = shareData.showPagePadding
        binding.readConfigVolumeButtonTurnPagesSwitch.isChecked = shareData.volumeButtonTurnPages
        binding.readConfigFullScreenSwitch.isChecked = shareData.fullScreen
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


    private fun showSelectMenu(view: View, menu: Int, click: (Int) -> Unit) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            it?.itemId?.apply(click)
            true
        }

    }

}