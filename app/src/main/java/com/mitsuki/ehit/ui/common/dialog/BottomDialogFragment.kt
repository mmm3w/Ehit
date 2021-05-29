package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mitsuki.ehit.R

open class BottomDialogFragment(@LayoutRes val layout: Int) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun show(manager: FragmentManager, tag: String?) {
        if (isAdded) return
        super.show(manager, tag)
    }

    override fun dismiss() {
        if (!isAdded) return
        super.dismiss()
    }
}