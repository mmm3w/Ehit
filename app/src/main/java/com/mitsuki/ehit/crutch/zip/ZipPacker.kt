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
import java.lang.Exception

/**
 * 想外部存储写入
 */
class ZipPacker(
    private val lifecycleOwner: LifecycleOwner,
    private val registry: ActivityResultRegistry,
    private val folder: File,
    private val files: Array<String>? = null,
    private val input: () -> String
) : DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private lateinit var packerLauncher: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        packerLauncher =
            registry.register("packZip", owner, ActivityResultContracts.CreateDocument()) {
                owner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        AppHolder.contentResolver.openOutputStream(it)?.use {
                            Zip.packFile(it, folder, files)
                        }
                        withContext(Dispatchers.Main) { AppHolder.toast("保存成功") }
                    } catch (inner: Exception) {
                        inner.printStackTrace()
                        withContext(Dispatchers.Main) { AppHolder.toast("保存失败") }
                    }
                }
            }
    }

    fun pack() {
        packerLauncher.launch(input())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}