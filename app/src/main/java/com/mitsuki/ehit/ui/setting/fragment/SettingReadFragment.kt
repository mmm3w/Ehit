package com.mitsuki.ehit.ui.setting.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.save.MemoryData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingReadFragment : PreferenceFragmentCompat() {

    private val selectablePreload = arrayOf("3", "5", "7", "9")

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_read, rootKey)


        findPreference<ListPreference>(ShareData.SP_SCREEN_ORIENTATION)?.apply {
            entries = arrayOf(
                string(R.string.text_screen_default),
                string(R.string.text_screen_vertical),
                string(R.string.text_screen_horizontal),
                string(R.string.text_screen_auto),
            )
            entryValues = Array(4) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                memoryData.screenOrientation = newValue.toString().toInt()
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_READING_DIRECTION)?.apply {
            entries = arrayOf(
                string(R.string.text_read_rtl),
                string(R.string.text_read_ltr),
                string(R.string.text_read_ttb),
            )
            entryValues = Array(3) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                memoryData.readOrientation = newValue.toString().toInt()
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_IMAGE_ZOOM)?.apply {
            entries = arrayOf(
                string(R.string.text_zoom_adapt_screen),
                string(R.string.text_zoom_adapt_width),
                string(R.string.text_zoom_adapt_height),
//                string(R.string.text_zoom_original),
//                string(R.string.text_zoom_fix_scale),
            )
            entryValues = Array(3) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                memoryData.imageZoom = newValue.toString().toInt()
                true
            }
        }


        findPreference<ListPreference>(ShareData.SP_PRELOAD_IMAGE)?.apply {
            entries = selectablePreload
            entryValues = Array(selectablePreload.size) { it.toString() }
            summary =
                string(R.string.text_setting_preload_image_summery).format(shareData.spPreloadImage)
            setOnPreferenceChangeListener { _, newValue ->
                val index = newValue.toString().toInt()
                summary = string(R.string.text_setting_preload_image_summery).format(
                    selectablePreload[index].toInt()
                )
                true
            }
        }
    }

}
