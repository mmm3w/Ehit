package com.mitsuki.ehit.ui.main

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.showToast
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.GeneralEditTextBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment

class QuickSearchNameInputDialog(
    private val initContent: String,
    private val onConfirm: (String) -> Unit
) :
    BindingDialogFragment<GeneralEditTextBinding>(
        R.layout.general_edit_text,
        GeneralEditTextBinding::bind
    ) {

    init {
        title(text = string(R.string.title_add_quick_search))
        positiveButton(text(R.string.text_confirm)) {
            val content = binding?.editTextUi?.text?.toString()?.trim()
            if (content.isNullOrEmpty()) {
                requireContext().showToast(string(R.string.error_content_empty))
            } else {
                onConfirm(content)
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.editTextUi?.apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT
            setText(initContent) }
    }
}