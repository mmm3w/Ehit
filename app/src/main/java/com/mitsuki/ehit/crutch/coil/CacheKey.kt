package com.mitsuki.ehit.crutch.coil

object CacheKey {
    fun thumbKey(gid: Long, token: String): String {
        return "GalleryThumb:$gid-$token"
    }

    fun previewKey(gid: Long, token: String, index: Int): String {
        return "GalleryPreview:$gid-$token-$index"
    }
}