package com.mitsuki.ehit.crutch.coil

import android.content.Context
import android.os.StatFs
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.mitsuki.armory.loadprogress.ProgressProvider
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.FakeHeader
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

object CoilInit {
    private const val RETRY_TIMES = 3
    private const val CACHE_DIRECTORY_NAME = "image_cache"

    private const val CACHE_LEVEL_MIN = 10L * 1024 * 1024 //10MB
    private const val CACHE_LEVEL_MAX = 1024L * 1024 * 1024 //1GB
    private const val CACHE_LEVEL_1 = 100L * 1024 * 1024 //100MB
    private const val CACHE_LEVEL_2 = 200L * 1024 * 1024 //200MB
    private const val CACHE_LEVEL_3 = 400L * 1024 * 1024 //400MB
    private const val CACHE_LEVEL_4 = 600L * 1024 * 1024 //600MB
    private const val CACHE_LEVEL_5 = 800L * 1024 * 1024 //800MB

    private const val DISK_CACHE_PERCENTAGE = 0.02


    @Suppress("EXPERIMENTAL_API_USAGE")
    fun init(context: Context, cookieJar: CookieJar) {
        Coil.setImageLoader(ImageLoader.Builder(context)
            .okHttpClient(buildCoilOkHttpClient(context, cookieJar))
            .availableMemoryPercentage(0.9)
            .crossfade(true)
            .error(R.drawable.ic_baseline_broken_image_24)
            .componentRegistry {
                add(RetryInterceptor(RETRY_TIMES))
                if (BuildConfig.SAVE_MODE) add(LoadBreakInterceptor())
            }
            .build())
    }

    @JvmStatic
    fun coilCache(context: Context): Cache {
        val cacheDirectory = File(context.cacheDir, CACHE_DIRECTORY_NAME).apply { mkdirs() }
        val cacheSize = autoDiskCache(cacheDirectory)
        return Cache(cacheDirectory, cacheSize)
    }

    private fun buildCoilOkHttpClient(context: Context, cookieJar: CookieJar): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(coilCache(context))
            .cookieJar(cookieJar)
            .addInterceptor(FakeHeader())
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })
            .addInterceptor(ProgressProvider.imageLoadInterceptor)
            .build()
    }

    //adapt to max
    private fun autoDiskCache(cacheDirectory: File): Long {
        return try {
            val cacheDir = StatFs(cacheDirectory.absolutePath)
            val size = DISK_CACHE_PERCENTAGE * cacheDir.blockCountLong * cacheDir.blockSizeLong
            when (size.toLong()) {
                in CACHE_LEVEL_MIN..CACHE_LEVEL_1 -> CACHE_LEVEL_MIN
                in CACHE_LEVEL_1..CACHE_LEVEL_2 -> CACHE_LEVEL_1
                in CACHE_LEVEL_2..CACHE_LEVEL_3 -> CACHE_LEVEL_2
                in CACHE_LEVEL_3..CACHE_LEVEL_4 -> CACHE_LEVEL_3
                in CACHE_LEVEL_4..CACHE_LEVEL_5 -> CACHE_LEVEL_4
                in CACHE_LEVEL_5..CACHE_LEVEL_MAX -> CACHE_LEVEL_5
                in CACHE_LEVEL_MAX..Long.MAX_VALUE -> CACHE_LEVEL_MAX
                else -> CACHE_LEVEL_MIN
            }
        } catch (_: Exception) {
            CACHE_LEVEL_MIN
        }
    }
}