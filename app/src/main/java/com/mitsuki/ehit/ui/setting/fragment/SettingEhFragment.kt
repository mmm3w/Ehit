package com.mitsuki.ehit.ui.setting.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.di.AsCookieManager
import com.mitsuki.ehit.crutch.extensions.copying2Clipboard
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.network.Site
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.toJson
import com.mitsuki.ehit.model.activityresult.GallerySearchActivityResultContract
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.ui.common.dialog.TextDialogFragment
import com.mitsuki.ehit.ui.common.dialog.show
import com.mitsuki.ehit.ui.login.activity.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingEhFragment : PreferenceFragmentCompat() {


    @AsCookieManager
    @Inject
    lateinit var mCookieManager: CookieManager

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    private val loginLaunch: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                updateLoginStates()
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_eh, rootKey)
        findPreference<Preference>("setting_logout")?.apply {
            setTitle(if (mCookieManager.isLogin) R.string.text_sign_out else R.string.text_login)
            setOnPreferenceClickListener {
                if (mCookieManager.isLogin) {
                    showLogoutDialog()
                } else {
                    loginLaunch.launch(Intent(requireContext(), LoginActivity::class.java))
                }
                true
            }
        }

        findPreference<Preference>("setting_cookie")?.apply {
            isVisible = mCookieManager.isLogin
            setOnPreferenceClickListener { showCookieDialog() }
        }
//
        findPreference<ListPreference>(ShareData.SP_DOMAIN)?.apply {
            entries = arrayOf(Site.EH, Site.EX)
            entryValues = Array(2) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                memoryData.domain = newValue.toString().toInt()
                true
            }
        }

        findPreference<Preference>("setting_site")?.setOnPreferenceClickListener { openSiteSetting() }
    }

    private fun updateLoginStates() {
        findPreference<Preference>("setting_logout")?.setTitle(if (mCookieManager.isLogin) R.string.text_sign_out else R.string.text_login)
        findPreference<Preference>("setting_cookie")?.isVisible = mCookieManager.isLogin
    }


    private fun showLogoutDialog() {
        TextDialogFragment().show(childFragmentManager, "logout") {
            title(res = R.string.text_sign_out)
            message(res = R.string.text_sign_out_desc)
            positiveBtn(res = R.string.text_confirm) {
                mCookieManager.clearCookie()
                updateLoginStates()
                dismiss()
            }
        }
    }

    private fun showCookieDialog(): Boolean {
        val cookie = mCookieManager.cookieSummary()
        TextDialogFragment().show(childFragmentManager, "logout") {
            title(res = R.string.text_setting_cookie)
            message(text = cookie.map { "${it.key}:\n${it.value}\n" }.joinToString("\n"))
            selectable()
            positiveBtn(res = R.string.text_copy) { cookie.toJson().copying2Clipboard() }
        }
        return true
    }

    private fun openSiteSetting(): Boolean {
        return true
    }

}