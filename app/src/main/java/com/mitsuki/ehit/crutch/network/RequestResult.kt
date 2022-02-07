package com.mitsuki.ehit.crutch.network

sealed class RequestResult<T> {
    class Success<T>(val data: T) : RequestResult<T>()

    class Fail<T>(val throwable: Throwable) : RequestResult<T>()
}