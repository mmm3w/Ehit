package com.mitsuki.ehit.crutch.db

import androidx.room.TypeConverter
import com.mitsuki.ehit.model.page.GalleryPageSource

class GalleryListTypeConverter {
    @TypeConverter
    fun fromType(type: GalleryPageSource.Type): String {
        return type.name
    }

    @TypeConverter
    fun toType(type: String): GalleryPageSource.Type {
        return GalleryPageSource.Type.valueOf(type)
    }
}