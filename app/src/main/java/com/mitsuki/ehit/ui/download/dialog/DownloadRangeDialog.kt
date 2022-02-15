package com.mitsuki.ehit.ui.download.dialog

import android.os.Bundle
import android.view.View
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
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
            setMinValue(1)
            setMaxValue(range)
            setStartValue(1)
            setEndValue(range)
            thumbSize = dp2px(14f)
        }

        binding?.dialogDownloadConfirm?.setOnClickListener {
            binding?.dialogDownloadRange?.apply {
                onConfirm(start, end)
            }
            dismiss()
        }
    }

}