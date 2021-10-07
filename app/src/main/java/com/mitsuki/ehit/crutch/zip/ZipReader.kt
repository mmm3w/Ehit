package com.mitsuki.ehit.crutch.zip

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mitsuki.ehit.crutch.AppHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLConnection

class ZipReader(
    private val lifecycleOwner: LifecycleOwner,
    private val registry: ActivityResultRegistry,
    private val targetFolder: File,
) : DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private lateinit var readerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        readerLauncher =
            registry.register("readZip", owner, ActivityResultContracts.OpenDocument()) {

                if (it == null) {
                    AppHolder.toast("未选择文件")
                    return@register
                }

                owner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        AppHolder.contentResolver.openInputStream(it)?.use { inputStream ->
                            Zip.unpack(inputStream, targetFolder)
                        }
                        withContext(Dispatchers.Main) { AppHolder.toast("解压成功") }
                    } catch (inner: Exception) {
                        inner.printStackTrace()
                        withContext(Dispatchers.Main) { AppHolder.toast("解压失败") }
                    }
                }
            }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    fun read() {
        readerLauncher.launch(
            arrayOf(URLConnection.getFileNameMap().getContentTypeFor("sample.zip"))
        )
    }
}