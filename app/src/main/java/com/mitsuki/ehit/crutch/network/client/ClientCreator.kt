package com.mitsuki.ehit.crutch.network.client

import okhttp3.OkHttpClient

interface ClientCreator {
    fun create(): OkHttpClient
}