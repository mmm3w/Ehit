package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.DialogDownloadRangeBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment

class DownloadRangeDialog(
    private val range: Int,
    private val onConfirm: (Int, Int) -> Unit
) : BindingDialogFragment<DialogDownloadRangeBinding>(
    R.layout.dialog_download_range,
    DialogDownloadRangeBinding::bind
) {

    init {
        title(text = text(R.string.text_download))

        positiveButton(text(R.string.text_confirm)) {
            binding?.apply {
                val start = dialogDownloadRangeStart.text.toString().trim().toIntOrNull() ?: 1
                val end = dialogDownloadRangeEnd.text.toString().trim().toIntOrNull() ?: range
                onConfirm(start, end)
            }
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.dialogDownloadRangeStart?.setText("1")
        binding?.dialogDownloadRangeEnd?.setText(range.toString())

    }
}