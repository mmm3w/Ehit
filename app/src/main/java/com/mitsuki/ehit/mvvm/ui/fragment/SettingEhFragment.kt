package com.mitsuki.ehit.mvvm.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R

class SettingEhFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_root, rootKey)
    }
}