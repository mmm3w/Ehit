package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogGalleryPreviewMenuBinding
import com.mitsuki.ehit.ui.common.dialog.BottomDialogFragment
import com.mitsuki.ehit.ui.detail.adapter.GalleryPreviewMenuAdapter

class GalleryPreviewMenu : BottomDialogFragment(R.layout.dialog_gallery_preview_menu) {

    private val binding by viewBinding(DialogGalleryPreviewMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireDialog().setCanceledOnTouchOutside(true)
        isCancelable = true

        binding?.galleryMenuOption?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = GalleryPreviewMenuAdapter()
        }
    }
}