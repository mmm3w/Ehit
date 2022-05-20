package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

/**
 *
 */
open class BindingDialogFragment<VB : ViewBinding>(
    @LayoutRes val layout: Int,
    private val bind: (View) -> VB
) : BaseDialogFragment() {
    var binding: VB? = null
        private set

    //构建binding
    @CallSuper
    override fun onCreateSubView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, null, false).apply { binding = bind(this) }
    }

    @CallSuper
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}