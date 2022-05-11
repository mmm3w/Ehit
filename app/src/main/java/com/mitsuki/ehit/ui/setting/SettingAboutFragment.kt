package com.mitsuki.ehit.ui.setting

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R

class SettingAboutFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_about, rootKey)
        findPreference<Preference>("setting_check_for_updates")?.setOnPreferenceClickListener {
            //TODO 检查更新
            true
        }
    }
}