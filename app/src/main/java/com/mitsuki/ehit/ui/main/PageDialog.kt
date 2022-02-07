package com.mitsuki.ehit.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.widget.addTextChangedListener
import com.mitsuki.armory.base.extend.hideSoftKeyboard
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogPageJumpBinding
import com.mitsuki.ehit.ui.common.dialog.BaseDialogFragment

class PageDialog(
    private val range: Int,
    private val onConfirm: (Int) -> Unit
) :
    BaseDialogFragment(R.layout.dialog_page_jump) {
    private val binding by viewBinding(DialogPageJumpBinding::bind)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.dialogPageRange?.text = "1..$range"
        binding?.dialogPageConfirm?.setOnClickListener {
            val target = binding?.dialogPageInput?.text?.toString()?.trim()?.toIntOrNull()
                ?.coerceIn(1, range) ?: -1
            if (target == -1) {
                //提示输入的内容有问题
            } else {
                onConfirm(target)
            }
            dismiss()
        }
        binding?.dialogPageSeek?.apply {
            max = range - 1
            setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) binding?.dialogPageInput?.setText("${progress + 1}")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    binding?.dialogPageInput?.apply {
                        hideSoftKeyboard()
                        clearFocus()
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }

        binding?.dialogPageInput?.apply {
            setText("1")
            addTextChangedListener(afterTextChanged = {
                binding?.dialogPageSeek?.progress =
                    (it?.toString()?.trim()?.toIntOrNull()?.coerceIn(0, range - 1) ?: 1) - 1
            })
        }


    }
}