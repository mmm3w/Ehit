package com.mitsuki.ehit.crutch.db

import androidx.room.TypeConverter
import com.mitsuki.ehit.model.entity.GalleryDataMeta

class GalleryListTypeConverter {
    @TypeConverter
    fun fromType(meta: GalleryDataMeta.Type): String {
        return meta.name
    }

    @TypeConverter
    fun toType(type: String): GalleryDataMeta.Type {
        return GalleryDataMeta.Type.valueOf(type)
    }
}