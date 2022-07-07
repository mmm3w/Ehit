package com.mitsuki.ehit.ui.setting.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mitsuki.ehit.base.BindingActivity
import com.mitsuki.ehit.crutch.di.AsCookieManager
import com.mitsuki.ehit.crutch.network.Site
import com.mitsuki.ehit.databinding.ActivityWebBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingWebActivity : BindingActivity<ActivityWebBinding>(ActivityWebBinding::inflate) {

    @AsCookieManager
    @Inject
    lateinit var mCookieManager: com.mitsuki.ehit.crutch.network.CookieManager

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.topBar.topBarBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.mainWeb.apply {
            settings.javaScriptEnabled = true



            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, t: String?) {
                    if (title.isNullOrEmpty()) {
                        t?.apply { binding.topBar.topBarText.text = this }
                    }
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    if (title.isNullOrEmpty()) {
                        view?.title?.apply { binding.topBar.topBarText.text = this }
                    }
                }
            }
            val cookie =
                mCookieManager.loadCookie(Site.currentDomain)
                    .joinToString(";") { "${it.name}=${it.value}" }
            loadUrl(Site.ehSetting, hashMapOf("Cookie" to cookie))
        }
    }

    override fun onBackPressed() {
        if (binding.mainWeb.canGoBack()) {
            binding.mainWeb.goBack()
        } else {
            super.onBackPressed()
        }
    }
}