package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import com.mitsuki.ehit.model.ehparser.Matcher
import com.mitsuki.ehit.crutch.extensions.htmlEscape
import com.mitsuki.ehit.model.entity.db.GalleryPreviewCache
import java.util.regex.Pattern

data class GalleryPreview(val imageUrl: String, val reloadKey: String, val downloadUrl: String) {

    constructor(cache: GalleryPreviewCache)
            : this(cache.imageUrl, cache.reloadKey, cache.downloadUrl)

    companion object {
        fun parse(content: String?): GalleryPreview {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")
            val imageUrl: String = Matcher.PREVIEW_IMG_URL.dataParse(content, "not found image url")
            val reloadKey: String =
                Matcher.PREVIEW_RELOAD_KEY.dataParse(content, "not found reload key")
//            val downloadUrl: String =
//                Matcher.PREVIEW_DOWNLOAD_URL.dataParse(content, "not found download url")
            return GalleryPreview(imageUrl, reloadKey, "")
        }

        private fun Pattern.dataParse(content: String, msg: String = ""): String {
            return matcher(content).run {
                if (find()) {
                    group(1)?.htmlEscape() ?: throw ParseThrowable(msg)
                } else {
                    throw ParseThrowable(msg)
                }
            }
        }
    }
}