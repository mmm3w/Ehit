package com.mitsuki.ehit.crutch.network

import okhttp3.Dns
import java.net.InetAddress

class MyDns :Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return listOf(InetAddress.getByAddress("https://e-hentai.org", parseV4("104.20.26.25")))
    }


    private fun parseV4(s: String): ByteArray? {
        var numDigits: Int
        var currentOctet: Int
        val values = ByteArray(4)
        var currentValue: Int
        val length = s.length
        currentOctet = 0
        currentValue = 0
        numDigits = 0
        for (i in 0 until length) {
            val c = s[i]
            if (c >= '0' && c <= '9') {
                /* Can't have more than 3 digits per octet. */
                if (numDigits == 3) return null
                /* Octets shouldn't start with 0, unless they are 0. */if (numDigits > 0 && currentValue == 0) return null
                numDigits++
                currentValue *= 10
                currentValue += c - '0'
                /* 255 is the maximum value for an octet. */if (currentValue > 255) return null
            } else if (c == '.') {
                /* Can't have more than 3 dots. */
                if (currentOctet == 3) return null
                /* Two consecutive dots are bad. */if (numDigits == 0) return null
                values[currentOctet++] = currentValue.toByte()
                currentValue = 0
                numDigits = 0
            } else return null
        }
        /* Must have 4 octets. */if (currentOctet != 3) return null
        /* The fourth octet can't be empty. */if (numDigits == 0) return null
        values[currentOctet] = currentValue.toByte()
        return values
    }
}