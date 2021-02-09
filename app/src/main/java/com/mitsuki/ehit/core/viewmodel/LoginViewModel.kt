package com.mitsuki.ehit.core.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.ehit.being.network.RequestResult
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository
import com.mitsuki.ehit.core.ui.adapter.LoginAccountAdapter
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    fun login(account: String, password: String) {
        viewModelScope.launch {
            when (val result = repository.login(account, password)) {
                is RequestResult.SuccessResult -> {
                    //登录成功
                }
                is RequestResult.FailResult -> {
                    //登录失败
                }
            }
        }
    }

    fun login(id: String, hash: String, igneous: String) {

    }
}