package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.view.View
import com.mitsuki.armory.imagegesture.ImageGesture
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : BaseFragment(R.layout.fragment_gallery) {

    private lateinit var mImageGesture: ImageGesture

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mImageGesture = ImageGesture(gallery_image)
    }
}