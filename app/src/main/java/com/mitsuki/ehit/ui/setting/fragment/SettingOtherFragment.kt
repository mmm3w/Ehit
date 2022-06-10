package com.mitsuki.ehit.ui.setting.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.ValueFinder
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.activityresult.ExportDataActivityResultContract
import com.mitsuki.ehit.ui.common.dialog.TextDialogFragment
import com.mitsuki.ehit.ui.common.dialog.show
import com.mitsuki.ehit.ui.setting.dialog.ProxyInputDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.IllegalArgumentException

@AndroidEntryPoint
class SettingOtherFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

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