package com.mitsuki.ehit.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R

class SettingAboutFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_about, rootKey)

        findPreference<Preference>("setting_source_code")?.apply {
            setOnPreferenceClickListener {

                requireActivity().startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.summary.toString())
                    )
                )
                true
            }
        }
    }
}