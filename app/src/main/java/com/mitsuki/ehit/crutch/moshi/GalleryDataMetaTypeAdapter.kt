package com.mitsuki.ehit.crutch.moshi

import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class GalleryDataMetaTypeAdapter {
    @FromJson
    fun fromJson(value: String): GalleryDataMeta.Type {
        return try {
            GalleryDataMeta.Type.valueOf(value)
        } catch (e: Exception) {
            GalleryDataMeta.Type.NORMAL
        }
    }

    @ToJson
    fun toJson(type: GalleryDataMeta.Type): String {
        return type.name
    }
}