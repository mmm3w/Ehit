package com.mitsuki.ehit.ui.setting.fragment

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.Setting
import com.mitsuki.ehit.crutch.di.AsCookieManager
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.moshi.fromJson
import com.mitsuki.ehit.crutch.moshi.toJson
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.activityresult.ExportDataActivityResultContract
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.ui.common.dialog.LoadingDialogFragment
import com.mitsuki.ehit.ui.setting.dialog.DataConfirmDialog
import com.mitsuki.ehit.ui.setting.dialog.ProxyInputDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class SettingOtherFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    @Inject
    lateinit var searchDao: SearchDao

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
                                exportData["quick_search"] = searchDao.querySimpleQuick()
                            }
                        }
                    }
                    requireContext().contentResolver.openOutputStream(second)?.use { outputStream ->
                        outputStream.write(exportData.toJson().toByteArray())
                    }
                    withContext(Dispatchers.Main) { mLoading.dismiss() }
                }
            } ?: kotlin.run { /*操作取消*/ }
        }

    @Suppress("BlockingMethodInNonBlockingContext")
    private val mImportData =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            result?.also { uri ->
                CoroutineScope(Dispatchers.Default).launch {
                    withContext(Dispatchers.Main) { mLoading.show(childFragmentManager, "loading") }
                    try {
                        requireContext()
                            .contentResolver
                            .openInputStream(uri)
                            ?.readBytes()
                            ?.run { String(this, StandardCharsets.UTF_8) }
                            ?.fromJson<Map<String, Any>>()?.also { result ->
                                val type: MutableList<Int> = arrayListOf()

                                result.entries.forEach {
                                    when (it.key) {
                                        "cookies" -> type.add(DataConfirmDialog.DATA_COOKIE)
                                        "quick_search" -> type.add(DataConfirmDialog.DATA_QUICK_SEARCH)
                                    }
                                }

                                val scope = suspendCoroutine<IntArray> {
                                    DataConfirmDialog(dl = type.toIntArray()) { scope ->
                                        it.resume(scope)
                                    }.show(childFragmentManager, "data_confirm")
                                }

                                scope.forEach {
                                    when (it) {
                                        DataConfirmDialog.DATA_COOKIE -> {
                                            with(cookieManager) {
                                                val node =
                                                    (result["cookies"] as? Map<*, *>) ?: return@with

                                                val id = (node["ipb_member_id"] as? String)
                                                    ?: return@with
                                                val hash = (node["ipb_pass_hash"] as? String)
                                                    ?: return@with
                                                val igneous =
                                                    (node["igneous"] as? String) ?: return@with

                                                cookieManager.buildNewCookie(id, hash, igneous)
                                            }
                                        }
                                        DataConfirmDialog.DATA_QUICK_SEARCH -> {
                                            (result["quick_search"] as? List<*>)?.apply {
                                                searchDao.mergeQuick(this)
                                            }
                                        }
                                    }
                                }


                            }
                    } catch (e: Exception) {
                    }
                    withContext(Dispatchers.Main) { mLoading.dismiss() }
                }
            } ?: kotlin.run { /*未选择*/ }
        }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_other, rootKey)

        findPreference<Preference>(ShareData.SP_PROXY_MODE)?.apply {
            summary = proxySummary(shareData.spProxyMode)
            setOnPreferenceClickListener {
                ProxyInputDialog { index, host ->
                    summary = "${string(Setting.proxySummary(index))} $host"
                }.show(childFragmentManager, "proxy")
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_THEME)?.apply {
            entries = Setting.themeText
            entryValues = Array(Setting.themeText.size) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                val data = (newValue as? String)?.toIntOrNull() ?: Setting.THEME_SYSTEM
                memoryData.theme = data
                when (data) {
                    Setting.THEME_NORMAL -> AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    Setting.THEME_NIGHT -> AppCompatDelegate
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
                mImportData.launch(arrayOf("application/json"))
                true
            }
        }
    }

    private fun proxySummary(index: Int): String {
        val extendInfo = when (index) {
            Setting.PROXY_DIRECT,
            Setting.PROXY_SYSTEM -> ""
            Setting.PROXY_HTTP,
            Setting.PROXY_SOCKS -> " ${shareData.spProxyIp}:${shareData.spProxyPort}"
            else -> throw  IllegalArgumentException()
        }
        return string(Setting.proxySummary(index)) + extendInfo
    }

}