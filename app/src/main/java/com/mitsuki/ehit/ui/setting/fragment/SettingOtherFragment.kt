package com.mitsuki.ehit.ui.setting.fragment

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.ValueFinder
import com.mitsuki.ehit.crutch.di.AsCookieManager
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.toJson
import com.mitsuki.ehit.model.activityresult.ExportDataActivityResultContract
import com.mitsuki.ehit.ui.common.dialog.LoadingDialogFragment
import com.mitsuki.ehit.ui.common.dialog.TextDialogFragment
import com.mitsuki.ehit.ui.common.dialog.show
import com.mitsuki.ehit.ui.setting.activity.SettingActivity
import com.mitsuki.ehit.ui.setting.dialog.DataConfirmDialog
import com.mitsuki.ehit.ui.setting.dialog.ProxyInputDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.internal.notifyAll
import javax.inject.Inject
import kotlin.IllegalArgumentException

@AndroidEntryPoint
class SettingOtherFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    @Inject
    @AsCookieManager
    lateinit var cookieManager: CookieManager

    private val mLoading by lazy { LoadingDialogFragment() }

    @Suppress("BlockingMethodInNonBlockingContext")
    private val mExportData =
        registerForActivityResult(ExportDataActivityResultContract()) { result ->
            result?.apply {
                val scope = first
                CoroutineScope(Dispatchers.Default).launch {
                    withContext(Dispatchers.Main) { mLoading.show(childFragmentManager, "loading") }
                    val exportData: MutableMap<String, Any> = hashMapOf()
                    scope.forEach {
                        when (it) {
                            DataConfirmDialog.DATA_COOKIE ->
                                exportData["cookies"] = cookieManager.cookieSummary()
                            DataConfirmDialog.DATA_QUICK_SEARCH -> {

                            }
                        }
                    }

                    requireContext().contentResolver.openOutputStream(second)?.use { outputStream ->
                        outputStream.write(exportData.toJson().toByteArray())
                    }
                    withContext(Dispatchers.Main) { mLoading.dismiss() }
                }
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
                DataConfirmDialog { mExportData.launch(it) }.show(
                    childFragmentManager,
                    "data_confirm"
                )
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

}