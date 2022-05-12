package com.mitsuki.ehit.ui.setting

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.extensions.string
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingDownloadFragment : PreferenceFragmentCompat() {

    private val selectableMultiThread = arrayOf("1", "3", "5", "7", "11")

    @Inject
    lateinit var shareData: ShareData

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_download, rootKey)


        findPreference<ListPreference>(ShareData.SP_DOWNLOAD_THREAD)?.apply {
            entries = selectableMultiThread
            entryValues = Array(selectableMultiThread.size) { it.toString() }
            summary =
                string(R.string.text_setting_download_thread_summery).format(shareData.spDownloadThread.toString())
            setOnPreferenceChangeListener { _, newValue ->
                val index = newValue.toString().toInt()
                summary = string(R.string.text_setting_download_thread_summery).format(
                    selectableMultiThread[index]
                )
                true
            }
        }

        findPreference<Preference>("clear_download_cache")?.setOnPreferenceClickListener { clearDownloadCache() }
    }

    private fun clearDownloadCache(): Boolean {
        //TODO 查询数据库并删除缓存 等 Repository 内容整理后再处理
        return true
    }
}