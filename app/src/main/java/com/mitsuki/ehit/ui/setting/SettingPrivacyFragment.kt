package com.mitsuki.ehit.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R

class SettingPrivacyFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_privacy, rootKey)

    }
}