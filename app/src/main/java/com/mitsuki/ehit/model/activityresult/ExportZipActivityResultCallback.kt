package com.mitsuki.ehit.model.activityresult

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.zip.Zip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

/**
 * 想外部存储写入
 */
class ExportZipActivityResultCallback(
    private val lifecycleOwner: LifecycleOwner,
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private lateinit var packerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        packerLauncher =
            registry.register("ExportGalleryZip", owner, ExportZipActivityResultContract()) {
                val uri = it.second
                val folder = it.first
                if (uri == null) {
                    return@register
                }
                owner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        AppHolder.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            Zip.pack(outputStream, folder)
                        }
                        withContext(Dispatchers.Main) { AppHolder.toast("保存成功") }
                    } catch (inner: Exception) {
                        inner.printStackTrace()
                        withContext(Dispatchers.Main) { AppHolder.toast("保存失败") }
                    }
                }
            }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
    }


    fun pack(title: String, gid: Long, token: String) {
        packerLauncher.launch(arrayOf(title, gid.toString(), token))
    }
}