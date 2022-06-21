package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.DialogFixedProgressBinding

open class FixedDialogFragment<VB : ViewBinding>(
    @LayoutRes layout: Int, bind: (View) -> VB
) : BindingDialogFragment<VB>(layout, bind) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener { _, _, _ -> true }
    }
}