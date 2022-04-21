package com.mitsuki.ehit.ui.download.dialog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogGalleryMenuListBinding
import com.mitsuki.ehit.ui.common.dialog.BottomDialogFragment
import com.mitsuki.ehit.ui.detail.adapter.GalleryPreviewMenuAdapter

class OptionMenu: BottomDialogFragment(R.layout.dialog_gallery_menu_list) {

    private val binding by viewBinding(DialogGalleryMenuListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireDialog().setCanceledOnTouchOutside(true)
        isCancelable = true

        binding?.galleryMenu?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = GalleryPreviewMenuAdapter()
        }
    }
}