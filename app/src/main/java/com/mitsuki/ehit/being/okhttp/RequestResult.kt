package com.mitsuki.ehit.being.okhttp

import java.net.ContentHandler

sealed class RequestResult<T> {
    class SuccessResult<T>(val data: T) : RequestResult<T>()

    class FailResult<T>(val throwable: Throwable) : RequestResult<T>()
}