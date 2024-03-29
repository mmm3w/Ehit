package com.mitsuki.ehit.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.db.CookieCache
import java.sql.Timestamp

@Dao
abstract class CookieDao {

    @Query("DELETE FROM ${DBValue.TABLE_USER_COOKIE}")
    abstract suspend fun clearCookie()

    @Query("SELECT * FROM ${DBValue.TABLE_USER_COOKIE} WHERE ${DBValue.TABLE_USER_COOKIE}.domain = :domain")
    abstract suspend fun queryCookie(domain: String): List<CookieCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCookies(cookie: List<CookieCache>)

    @Query("DELETE FROM ${DBValue.TABLE_USER_COOKIE} WHERE ${DBValue.TABLE_USER_COOKIE}.expires < :timestamp")
    abstract suspend fun clearExpiredCookie(timestamp: Long)
}