package com.mitsuki.ehit.ui.gallerydetail.widget

import android.view.MotionEvent
import android.widget.ImageView
import com.mitsuki.armory.imagegesture.ImageGesture

class GalleryImageGesture(imageView: ImageView) : ImageGesture(imageView) {

    var onLongPress: (() -> Unit)? = null

    override fun onLongPress(e: MotionEvent) {
        onLongPress?.invoke()
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

}