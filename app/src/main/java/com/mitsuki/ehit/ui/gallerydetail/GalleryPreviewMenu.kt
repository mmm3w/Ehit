package com.mitsuki.ehit.ui.gallerydetail

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.drawable
import com.mitsuki.ehit.crutch.extend.requireWindow
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.DialogGalleryPreviewMenuBinding
import com.mitsuki.ehit.ui.common.dialog.BottomDialogFragment
import com.mitsuki.ehit.ui.gallerydetail.adapter.GalleryPreviewMenuAdapter

class GalleryPreviewMenu : BottomDialogFragment(R.layout.dialog_gallery_preview_menu) {

    private val binding by viewBinding(DialogGalleryPreviewMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        requireDialog().setCanceledOnTouchOutside(true)

        binding?.galleryMenuOption?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = GalleryPreviewMenuAdapter()
        }
    }
}