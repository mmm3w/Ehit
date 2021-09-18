package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.crutch.ShareData
import okhttp3.Cookie


@Entity(
    tableName = DBValue.TABLE_USER_COOKIE,
)
class Cookie(
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "domain") val domain: String,
    @ColumnInfo(name = "expires") val expires: Long,
    @PrimaryKey
    @ColumnInfo(name = "full_cookie") val flag: String
) {
    fun buildCookie() :Cookie{
        return Cookie.Builder()
            .name(key)
            .value(value)
            .expiresAt(expires)
            .domain(domain)
            .build()
    }
}