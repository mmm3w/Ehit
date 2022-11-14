package com.mitsuki.ehit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.ehit.crutch.di.AsCookieManager
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.network.site.ApiContainer
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Cookie
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    @AsCookieManager val cookieManager: CookieManager
) :
    ViewModel(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    fun login(account: String, password: String) {
        if (account.isEmpty()) {
            post("toast", "")
            return
        }
        if (password.isEmpty()) {
            post("toast", "")
            return
        }

        viewModelScope.launch {
            when (val result = repository.login(account, password)) {
                is RequestResult.Success<String> -> post("next", 0)
                is RequestResult.Fail<*> -> post("toast", result.throwable.message)
            }
        }
    }

    fun login(id: String, hash: String, igneous: String) {
        if (id.isEmpty()) {
            post("toast", "")
            return
        }
        if (hash.isEmpty()) {
            post("toast", "")
            return
        }
        if (igneous.isEmpty()) {
            post("toast", "")
            return
        }

        cookieManager.buildNewCookie(id, hash, igneous)

        post("next", 0)
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