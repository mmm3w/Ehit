package com.mitsuki.ehit.crutch.db

import androidx.room.TypeConverter
import com.mitsuki.ehit.model.entity.GalleryDataType

class GalleryListTypeConverter {
    @TypeConverter
    fun fromType(type: GalleryDataType.Type): String {
        return type.name
    }

    @TypeConverter
    fun toType(type: String): GalleryDataType.Type {
        return GalleryDataType.Type.valueOf(type)
    }
}