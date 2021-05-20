package com.mitsuki.ehit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private val eventSubject: PublishSubject<Event> = PublishSubject.create()
    val event get() = eventSubject.hideWithMainThread()


    fun login(account: String, password: String) {
        if (account.isEmpty()) {
            postEvent(message = "")
            return
        }
        if (password.isEmpty()) {
            postEvent(message = "")
            return
        }

        viewModelScope.launch {
            when (val result = repository.login(account, password)) {
                is RequestResult.SuccessResult -> postEvent(goAhead = 0)
                is RequestResult.FailResult -> postEvent(message = result.throwable.message)
            }
        }
    }

    fun login(id: String, hash: String, igneous: String) {
        if (id.isEmpty()) {
            postEvent(message = "")
            return
        }
        if (hash.isEmpty()) {
            postEvent(message = "")
            return
        }
        if (igneous.isEmpty()) {
            postEvent(message = "")
            return
        }
        ShareData.saveCookie(id, hash, igneous)
        (HttpRookie.client.cookieJar as? CookieJarImpl)?.refresh()
        postEvent(goAhead = 0)
    }


    data class Event(val message: String?, val goAhead: Int?)

    private fun postEvent(message: String? = null, goAhead: Int? = null) {
        eventSubject.onNext(Event(message, goAhead))
    }
}