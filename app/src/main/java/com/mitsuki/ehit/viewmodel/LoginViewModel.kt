package com.mitsuki.ehit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

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
        ShareData.saveCookie(id, hash, igneous)
        (HttpRookie.client.cookieJar as? CookieJarImpl)?.refresh()
        nextEvent.postValue(0)
    }

}