package com.mitsuki.ehit.ui.download.dialog

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.zip.ZipPacker
import com.mitsuki.ehit.databinding.DialogFixedProgressBinding
import com.mitsuki.ehit.ui.common.dialog.FixedDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import kotlin.math.roundToInt

class ExportProgressDialog(
    private val uri: Uri,
    private val folder: File
) : FixedDialogFragment<DialogFixedProgressBinding>(
    R.layout.dialog_fixed_progress,
    DialogFixedProgressBinding::bind
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startExport()
    }

    private fun startExport() {
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.Main) {
                binding?.fixedProgressHint?.setText(text(R.string.hint_waiting))
            }
            withContext(Dispatchers.IO) {
                try {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    val outputStream = AppHolder.contentResolver.openOutputStream(uri)
                        ?: throw IllegalArgumentException()
                    val files = folder.listFiles() ?: throw IllegalArgumentException()

                    ZipPacker().packFiles(outputStream, files) { file, progress ->
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                binding?.fixedProgressHint?.text = file.name
                                binding?.fixedProgress?.progress = (progress * 100).roundToInt()
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        dismiss()
                        AppHolder.toast(string(R.string.hint_save_success))
                    }
                } catch (inner: Exception) {
                    inner.printStackTrace()
                    withContext(Dispatchers.Main) {
                        dismiss()
                        AppHolder.toast(string(R.string.hint_save_failed))
                    }
                }
            }
        }
    }

}