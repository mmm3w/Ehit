package com.mitsuki.ehit.ui.setting.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.ValueFinder
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.activityresult.ExportDataActivityResultContract
import com.mitsuki.ehit.ui.common.dialog.TextDialogFragment
import com.mitsuki.ehit.ui.common.dialog.show
import com.mitsuki.ehit.ui.setting.activity.SettingActivity
import com.mitsuki.ehit.ui.setting.dialog.ProxyInputDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notifyAll
import javax.inject.Inject
import kotlin.IllegalArgumentException

@AndroidEntryPoint
class SettingOtherFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    private val mExportData =
        registerForActivityResult(ExportDataActivityResultContract()) {
            it?.apply {

            } ?: kotlin.run {
                //操作取消
            }
        }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_other, rootKey)

        findPreference<Preference>(ShareData.SP_PROXY_MODE)?.apply {
            summary = proxySummary(shareData.spProxyMode)
            setOnPreferenceClickListener {
                ProxyInputDialog { index, host ->
                    summary = "${string(ValueFinder.proxySummary(index))} $host"
                }.show(childFragmentManager, "proxy")
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_THEME)?.apply {
            entries = arrayOf(
                string(R.string.text_theme_follow_system),
                string(R.string.text_theme_night_no),
                string(R.string.text_theme_night_yes)
            )
            entryValues = Array(3) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                val data = (newValue as? String)?.toIntOrNull() ?: ValueFinder.THEME_SYSTEM
                memoryData.theme = data
                when (data) {
                    ValueFinder.THEME_NORMAL -> AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    ValueFinder.THEME_NIGHT -> AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                true
            }
        }

        findPreference<Preference>("export_data")?.apply {
            setOnPreferenceClickListener {
                showExportHintDialog()
                true
            }

        }
        findPreference<Preference>("import_data")?.apply {
            setOnPreferenceClickListener {

                true
            }
        }

        preferenceManager?.preferenceScreen?.icon
    }

    private fun proxySummary(index: Int): String {
        val extendInfo = when (index) {
            0, 1 -> ""
            2, 3 -> " ${shareData.spProxyIp}:${shareData.spProxyPort}"
            else -> throw  IllegalArgumentException()
        }
        return string(ValueFinder.proxySummary(index)) + extendInfo
    }

    private fun showExportHintDialog() {
        TextDialogFragment().show(childFragmentManager, "logout") {
            title(res = R.string.text_export_data)
            message(res = R.string.text_export_desc)
            positiveBtn(res = R.string.text_export) {
                mExportData.launch(arrayOf())
            }
        }
    }


    private fun exportData() {


    }

}