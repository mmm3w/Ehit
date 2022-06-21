package com.mitsuki.ehit.ui.common.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R

class LoadingDialogFragment : DialogFragment(R.layout.dialog_loading) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog?.window?.setLayout(
            dp2px(80f).toInt(),
            dp2px(80f).toInt()
        )
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener { _, _, _ -> true }
    }
}