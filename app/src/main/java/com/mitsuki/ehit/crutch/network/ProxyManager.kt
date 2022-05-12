package com.mitsuki.ehit.crutch.network

import com.mitsuki.ehit.crutch.save.MemoryData
import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import javax.inject.Inject

class ProxyManager @Inject constructor(
    private val memoryData: MemoryData
) : ProxySelector() {

    private val ds by lazy { getDefault() }

    override fun select(uri: URI?): MutableList<Proxy> {
        return memoryData.proxy?.run { arrayListOf(this) } ?: ds.select(uri)
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
    }
}