package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

/**
 *
 */
open class BindingDialogFragment<VB : ViewBinding>(
    @LayoutRes val layout: Int,
    private val bind: (View) -> VB
) : BaseDialogFragment() {
    lateinit var binding: VB
        private set

    //构建binding
    override fun onCreateSubView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, null, false).apply { binding = bind(this) }
    }
}