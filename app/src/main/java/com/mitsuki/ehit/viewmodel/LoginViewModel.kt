package com.mitsuki.ehit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch
import okhttp3.Cookie

class LoginViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    val toastEvent: SingleLiveEvent<String> by lazy { SingleLiveEvent() }
    val nextEvent: SingleLiveEvent<Int> by lazy { SingleLiveEvent() }

    fun login(account: String, password: String) {
        if (account.isEmpty()) {
            toastEvent.postValue("")
            return
        }
        if (password.isEmpty()) {
            toastEvent.postValue("")
            return
        }

        viewModelScope.launch {
            when (val result = repository.login(account, password)) {
                is RequestResult.SuccessResult -> nextEvent.postValue(0)
                is RequestResult.FailResult -> toastEvent.postValue(result.throwable.message)
            }
        }
    }

    fun login(id: String, hash: String, igneous: String) {
        if (id.isEmpty()) {
            toastEvent.postValue("")
            return
        }
        if (hash.isEmpty()) {
            toastEvent.postValue("")
            return
        }
        if (igneous.isEmpty()) {
            toastEvent.postValue("")
            return
        }

        CookieManager.new(
            arrayListOf(
                buildCookie("ipb_member_id", id, Url.EH),
                buildCookie("ipb_pass_hash", hash, Url.EH),
                buildCookie("igneous", igneous, Url.EH),

                buildCookie("ipb_member_id", id, Url.EX),
                buildCookie("ipb_pass_hash", hash, Url.EX),
                buildCookie("igneous", igneous, Url.EX)
            )
        )

        nextEvent.postValue(0)
    }

    private fun buildCookie(name: String, value: String, domain: String): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .expiresAt(Long.MAX_VALUE)
            .domain(domain)
            .build()
    }
}