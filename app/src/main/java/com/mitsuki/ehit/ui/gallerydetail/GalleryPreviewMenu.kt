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
import com.mitsuki.ehit.ui.gallerydetail.adapter.GalleryPreviewMenuAdapter

class GalleryPreviewMenu : DialogFragment(R.layout.dialog_gallery_preview_menu) {

    private val binding by viewBinding(DialogGalleryPreviewMenuBinding::bind)

    private val controller by windowController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        controller.window(statusBarHide = true)
        isCancelable = true
        requireDialog().setCanceledOnTouchOutside(true)
        requireWindow().apply {
            setBackgroundDrawable(drawable(R.drawable.bg_dialog_gallery_preview))
            attributes = WindowManager.LayoutParams().apply {
                copyFrom(attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.BOTTOM
            }
        }

        binding?.galleryMenuOption?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = GalleryPreviewMenuAdapter()
        }
    }
}