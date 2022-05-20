package com.mitsuki.ehit.ui.common.dialog

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogGalleryMenuListBinding
import com.mitsuki.ehit.ui.common.adapter.BottomMenuAdapter

open class BottomMenuDialogFragment(
    private val mOptions: IntArray,
    private val onOptionClick: (Int) -> Boolean
) : BindingBottomDialogFragment<DialogGalleryMenuListBinding>(
    R.layout.dialog_gallery_menu_list,
    DialogGalleryMenuListBinding::bind
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireDialog().setCanceledOnTouchOutside(true)
        isCancelable = true

        val mAdapter = BottomMenuAdapter(mOptions)
        mAdapter.receiver<Int>("option").observe(viewLifecycleOwner) {
            if (onOptionClick(it)) dismiss()
        }

        binding?.galleryMenu?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }
}