package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.text

class TextDialogFragment : BaseDialogFragment() {

    private var message: CharSequence = ""
    private var isSelectable = false

    override fun onCreateSubView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return TextView(context).apply {
            id = R.id.dialog_base_text
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.dialog_base_text).apply {
            text = message
            setTextIsSelectable(isSelectable)
        }

    }

    fun message(text: CharSequence? = null, res: Int = -1) {
        if (text.isNullOrEmpty() && res == -1) return
        message = if (text.isNullOrEmpty()) {
            text(res)
        } else {
            text
        }
    }

    fun selectable() {
        isSelectable = true
    }


}