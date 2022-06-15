package com.mitsuki.ehit.ui.setting.fragment

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingRootFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_root, rootKey)
    }
}