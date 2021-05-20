package com.mitsuki.ehit.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.OriginalSize
import com.mitsuki.armory.imagegesture.ImageGesture
import com.mitsuki.armory.imagegesture.StartType
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.crutch.extend.getInteger
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.ui.widget.OriginalTransformation
import com.mitsuki.ehit.viewmodel.GalleryViewModel
import com.mitsuki.loadprogress.Progress
import com.mitsuki.loadprogress.ProgressProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery.*

@AndroidEntryPoint
class GalleryFragment : BaseFragment(R.layout.fragment_gallery) {

    private val mViewModel: GalleryViewModel
            by createViewModelLazy(GalleryViewModel::class, { viewModelStore })

    private lateinit var mImageGesture: ImageGesture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mImageGesture = ImageGesture(gallery_image).apply { startType = StartType.TOP }
        gallery_index?.text = (mViewModel.index + 1).toString()

        mViewModel.data.observe(viewLifecycleOwner, Observer(this::onLoadImage))
        mViewModel.state.observe(viewLifecycleOwner, Observer(this::onViewState))
        ProgressProvider.event(mViewModel.tag)
            .observe(this, this@GalleryFragment::onLoadProgress)

        mViewModel.obtainData()
    }

    private fun onViewState(state: GalleryViewModel.ViewState) {
        gallery_loading?.isVisible = state.loading
        gallery_error_message?.text = state.error ?: ""
    }

    private fun onLoadImage(url: String) {
        gallery_image.load(url) {
            crossfade(getInteger(R.integer.image_load_cross_fade))
            size(OriginalSize)
            transformations(OriginalTransformation())
            listener(
                onError = { _: ImageRequest, throwable: Throwable -> onLoadError(throwable) },
                onSuccess = { _: ImageRequest, _: ImageResult.Metadata -> onLoadSuccess() }
            )
        }
    }

    private fun onLoadError(throwable: Throwable) {
        gallery_progress?.isVisible = false
        gallery_error_message?.text = throwable.message
    }

    private fun onLoadSuccess() {
        gallery_progress?.isVisible = false
    }

    private fun onLoadProgress(progress: Progress?) {
        mViewModel.changeLoadingState(false)
        gallery_progress?.isVisible = true
        progress?.apply {
            gallery_progress.progress =
                (currentBytes.toDouble() / contentLength.toDouble() * 100).toInt()
        }
    }
}