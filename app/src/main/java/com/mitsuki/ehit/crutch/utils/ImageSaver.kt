package com.mitsuki.ehit.crutch.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream

class ImageSaver {
    fun save(context: Context, bimap: Bitmap, name: String, path: String): Boolean {
        return write(context, name, path) { outputStream ->
            bimap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }

    private fun write(
        context: Context,
        name: String,
        path: String = "",
        streamHandler: (OutputStream) -> Boolean
    ): Boolean {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //进行锁定
                put(MediaStore.Audio.Media.IS_PENDING, 1)
                if (path.isNotEmpty()) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, path)
                }
            } else {
                if (path.isNotEmpty()) {
                    //TODO need to test
                    val pictures =
                        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), path)
                    @Suppress("DEPRECATION")
                    put(MediaStore.Images.Media.DATA, File(pictures, name).absolutePath)
                }
            }
        }

        return with(context.contentResolver) {
            insert(uri, content)?.let { uri ->
                val result = openOutputStream(uri)?.buffered()?.use {
                    streamHandler(it)
                } ?: false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    update(
                        uri,
                        ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) },
                        null,
                        null
                    )
                }
                if (!result) delete(uri, null, null)
                result
            } ?: false
        }
    }
}