package com.mitsuki.ehit.ui.setting

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extensions.string
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingReadFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

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
                shareData.screenOrientation = newValue.toString().toInt()
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_READ_ORIENTATION)?.apply {
            entries = arrayOf(
                string(R.string.text_read_rtl),
                string(R.string.text_read_ltr),
                string(R.string.text_read_ttb),
            )
            entryValues = Array(3) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                shareData.readOrientation = newValue.toString().toInt()
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_IMAGE_ZOOM)?.apply {
            entries = arrayOf(
                string(R.string.text_zoom_adapt_screen),
                string(R.string.text_zoom_adapt_width),
                string(R.string.text_zoom_adapt_height),
                string(R.string.text_zoom_original),
                string(R.string.text_zoom_fix_scale),
            )
            entryValues = Array(5) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                shareData.imageZoom = newValue.toString().toInt()
                true
            }
        }
    }

}
