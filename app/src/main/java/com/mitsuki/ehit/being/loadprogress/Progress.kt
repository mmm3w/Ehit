package com.mitsuki.ehit.being.loadprogress

data class Progress(
    val tag: String,
    var currentBytes: Long,
    var contentLength: Long
) {
    override fun toString(): String {
        return "Progress : $currentBytes/$contentLength ($tag)"
    }

    fun progress(): Long = currentBytes / contentLength
}