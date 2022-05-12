package com.mitsuki.ehit.crutch.network

import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI

class ProxyManager : ProxySelector() {
    override fun select(uri: URI?): MutableList<Proxy> {
        TODO("Not yet implemented")

    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
    }
}