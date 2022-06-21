package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.DialogRatingBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment

class RatingDialog(val rating: Float, val onConfirm: (Float) -> Unit) :
    BindingDialogFragment<DialogRatingBinding>(R.layout.dialog_rating, DialogRatingBinding::bind) {

    init {
        title(text(R.string.text_rate))
        positiveButton(text(R.string.text_confirm)) {
            binding?.ratingTarget?.rating?.apply(onConfirm)
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ratingTarget?.rating = rating

    }



}