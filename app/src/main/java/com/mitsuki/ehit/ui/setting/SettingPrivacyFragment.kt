package com.mitsuki.ehit.ui.setting

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extensions.text
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingPrivacyFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

    @RequiresApi(Build.VERSION_CODES.R)
    private val biometricEnrollContract =
        registerForActivityResult(object : ActivityResultContract<String?, Intent?>() {
            override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
                return intent
            }

            override fun createIntent(context: Context, input: String?): Intent {
                return Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_WEAK or DEVICE_CREDENTIAL
                    )
                }
            }
        }) {}

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_privacy, rootKey)


        findPreference<SwitchPreference>(ShareData.SP_SECURITY)?.apply {
            setOnPreferenceClickListener {
                if (!shareData.spSecurity) {
                    showSnackBar(R.string.text_lock_disable)
                } else {
                    val manager = BiometricManager.from(requireContext())
                    when (manager.canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            showSnackBar(R.string.text_lock_enable)
                        }
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                            isChecked = false
                            showSnackBar(R.string.text_biometric_no_hardware)
                        }
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                            isChecked = false
                            showSnackBar(R.string.text_biometric_hw_unavailable)
                        }
                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                            isChecked = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                biometricEnrollContract.launch(null)
                            } else {
                                showSnackBar(R.string.text_biometric_none_enrolled)
                            }
                        }
                        else -> {
                        }
                    }
                }
                true
            }
        }

        findPreference<SwitchPreference>(ShareData.SP_DATA_ANALYTICS)?.apply {
            setOnPreferenceClickListener {
                shareData.spDataAnalytics = isChecked
                true
            }
        }
    }

    private fun showSnackBar(text: Int) {
        Snackbar.make(
            requireView(),
            text(text),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}