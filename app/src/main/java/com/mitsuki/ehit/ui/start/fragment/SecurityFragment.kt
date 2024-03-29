package com.mitsuki.ehit.ui.start.fragment

import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.FragmentSecurityBinding
import com.mitsuki.ehit.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityFragment : BindingFragment<FragmentSecurityBinding>(
    R.layout.fragment_security,
    FragmentSecurityBinding::bind
) {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? MainActivity)?.setDrawerEnable(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> showSnackBar(string(R.string.text_authentication_cancel))
                        else -> showSnackBar(
                            string(R.string.text_authentication_error).format(errString, errorCode)
                        )
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    AppHolder.unlock()
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_global_security_back)
                }

                override fun onAuthenticationFailed() {
                    showSnackBar(string(R.string.text_authentication_failed))
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(text(R.string.text_ehit_biometric_login))
            .setSubtitle(text(R.string.text_ehit_biometric_login_sub))
            .setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
            .setConfirmationRequired(false)
            .build()

        binding?.securityAuthenticationBtn?.setOnClickListener { authenticate() }

        authenticate()
    }

    private fun authenticate() {
        val code = BiometricManager.from(requireContext())
            .canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
        when (code) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> showSnackBar(string(R.string.text_biometric_no_hardware))
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> showSnackBar(string(R.string.text_biometric_hw_unavailable))
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> showSnackBar(string(R.string.text_biometric_none_enrolled))
            else -> showSnackBar(
                string(R.string.text_authentication_error).format(
                    string(R.string.text_biometric_error),
                    code
                )
            )
        }
    }

    private fun showSnackBar(text: CharSequence) {
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}