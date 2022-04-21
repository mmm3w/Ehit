package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogDownloadRangeBinding
import com.mitsuki.ehit.ui.common.dialog.BaseDialogFragment

class DownloadRangeDialog(
    private val range: Int,
    private val onConfirm: (Int, Int) -> Unit
) :
    BaseDialogFragment(R.layout.dialog_download_range) {

    private val binding by viewBinding(DialogDownloadRangeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.dialogDownloadRange?.apply {
            thumbColor = 0xff808080
            selectedColor = 0xff808080
            progressWidth = dp2px(4f)
            thumbSize = dp2px(8f)
        }

        binding?.dialogDownloadRange?.apply {
            setMinValue(1)
            setMaxValue(range)
            setStartValue(1)
            setEndValue(range)
            thumbSize = dp2px(14f)
            callback = { s, e -> setTextHint(s, e) }
            setTextHint(start, end)
        }

        binding?.dialogDownloadConfirm?.setOnClickListener {
            binding?.dialogDownloadRange?.apply {
                onConfirm(start, end)
            }
            dismiss()
        }
    }

    private fun setTextHint(start: Int, end: Int) {
        binding?.dialogDownloadRangeHint?.text = string(R.string.text_page_range).format(start, end)
    }

}