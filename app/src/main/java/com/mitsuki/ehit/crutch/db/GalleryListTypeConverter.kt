package com.mitsuki.ehit.crutch.db

import androidx.room.TypeConverter
import com.mitsuki.ehit.model.page.GalleryListPageIn

class GalleryListTypeConverter {
    @TypeConverter
    fun fromType(type: GalleryListPageIn.Type): String {
        return type.name
    }

    @TypeConverter
    fun toType(type: String): GalleryListPageIn.Type {
        return GalleryListPageIn.Type.valueOf(type)
    }
}