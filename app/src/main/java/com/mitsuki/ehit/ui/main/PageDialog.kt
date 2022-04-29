package com.mitsuki.ehit.ui.main

import android.os.Bundle
import android.view.View
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.DialogPageJumpBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment

class PageDialog(
    private val range: Int,
    private val onConfirm: (Int) -> Unit
) :
    BindingDialogFragment<DialogPageJumpBinding>(
        R.layout.dialog_page_jump,
        DialogPageJumpBinding::bind
    ) {

    init {
        title(text = string(R.string.title_jump_page).format(range))
        positiveBtn(text(R.string.text_confirm)) {
            val target = binding.dialogPageInput.text?.toString()?.trim()?.toIntOrNull()
                ?.coerceIn(1, range) ?: -1
            if (target == -1) {
                //提示输入的内容有问题
            } else {
                onConfirm(target)
            }
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogPageInput.apply { setText("1") }
    }
}