package com.mitsuki.ehit.ui.main

import android.os.Bundle
import android.view.View
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.databinding.DialogExJumpBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment

class ExPageDialog(
    private val onConfirm: (Int) -> Unit
) :
    BindingDialogFragment<DialogExJumpBinding>(
        R.layout.dialog_ex_jump,
        DialogExJumpBinding::bind
    ) {

    init {
        title(text = string(R.string.title_ex_jump_page))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //输入框
        //1d 3d 1w 2w 1m 6m 1y 2y 几个按钮
        //弹出时间选择器按钮
        //底部按钮， 往前推还是往后推
    }
}