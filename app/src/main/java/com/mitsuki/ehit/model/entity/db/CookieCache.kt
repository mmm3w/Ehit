package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

@Entity(
    tableName = DBValue.TABLE_USER_COOKIE,
)
class CookieCache(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val _id: Long = 0,
    @ColumnInfo(name = "domain") val domain: String,
    @ColumnInfo(name = "expires") val expires: Long,
    @ColumnInfo(name = "content") val content: String,
) {
    fun buildCookie(url: HttpUrl): Cookie? {
        return Cookie.Companion.parse(url, content)
    }
}