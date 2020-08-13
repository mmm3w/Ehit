package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R

class SettingRootFragment : PreferenceFragmentCompat(),PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_root, rootKey)
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?
    ): Boolean {
        TODO("Not yet implemented")
    }


}