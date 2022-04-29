package com.mitsuki.ehit.ui.setting

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.network.Site
import com.mitsuki.ehit.ui.common.dialog.TextDialogFragment
import com.mitsuki.ehit.ui.common.dialog.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingEhFragment : PreferenceFragmentCompat() {


    @Inject
    lateinit var mCookieManager: CookieManager

    @Inject
    lateinit var shareData: ShareData


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_eh, rootKey)
        val isLogin = mCookieManager.isLogin
        findPreference<Preference>("setting_logout")?.apply {
            setTitle(if (isLogin) R.string.text_sign_out else R.string.text_login)
            setOnPreferenceClickListener { showLogoutDialog() }
        }

        findPreference<Preference>("setting_cookie")?.apply {
            isVisible = isLogin
            setOnPreferenceClickListener { showCookieDialog() }
        }
//
        findPreference<ListPreference>(ShareData.SP_DOMAIN)?.apply {
            entries = arrayOf(Site.EH, Site.EX)
            entryValues = Array(2) { it.toString() }
            setOnPreferenceChangeListener { _, newValue ->
                shareData.domain = newValue.toString().toInt()
                true
            }
        }

        findPreference<Preference>("setting_site")?.setOnPreferenceClickListener { openSiteSetting() }
    }


    private fun showLogoutDialog(): Boolean {
        TextDialogFragment().show(childFragmentManager, "logout") {
            title(res = R.string.text_sign_out)
            message(res = R.string.text_sign_out_desc)
            positiveBtn(res = R.string.text_confirm) {
                mCookieManager.clearCookie()
                //提示重启
            }
        }
        return true
    }

    private fun showCookieDialog(): Boolean {
        TextDialogFragment().show(childFragmentManager, "logout") {
            title(res = R.string.text_sign_out)
            message(res = R.string.text_sign_out_desc)
            positiveBtn(res = R.string.text_copy) {
                /*复制json操作*/
            }
        }
//        MaterialDialog(requireContext()).show {
//            title(res = R.string.title_cookie)
//            message(text = allCookieInfo)
//            positiveButton(R.string.text_copy) { /*复制json操作*/ }
//            lifecycleOwner(this@SettingEhFragment)
//        }
        return true
    }

    private fun openSiteSetting(): Boolean {
        return true
    }

}