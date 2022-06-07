package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.View
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.DialogFixedProgressBinding

open class FixedProgressDialogFragment :
    BindingDialogFragment<DialogFixedProgressBinding>(
        R.layout.dialog_fixed_progress,
        DialogFixedProgressBinding::bind
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener { _, _, _ -> true }
    }
}