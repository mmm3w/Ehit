package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.OriginalSize
import com.mitsuki.armory.imagegesture.ImageGesture
import com.mitsuki.armory.imagegesture.StartType
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.being.extend.getInteger
import com.mitsuki.ehit.being.imageloadprogress.Progress
import com.mitsuki.ehit.being.imageloadprogress.ProgressProvider
import com.mitsuki.ehit.being.load
import com.mitsuki.ehit.core.ui.widget.OriginalTransformation
import com.mitsuki.ehit.core.viewmodel.GalleryViewModel
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
        //TODO：图片加载进度跳存在bug，整个界面还需要优化
        ProgressProvider.event(mViewModel.tag())
            .observe(viewLifecycleOwner, Observer(this@GalleryFragment::onLoadProgress))

        mViewModel.obtainData()
    }

    private fun onViewState(state: GalleryViewModel.ViewState) {
        gallery_loading?.isVisible = state.loading
        gallery_error_message?.text = state.error ?: ""
    }

    private fun onLoadImage(url: String) {
        gallery_image.load(url = url) {
            crossfade(getInteger(R.integer.image_load_cross_fade))
            size(OriginalSize)
            transformations(OriginalTransformation())
            listener(
                onStart = { onLoadStart() },
                onError = { _: ImageRequest, throwable: Throwable -> onLoadError(throwable) },
                onSuccess = { _: ImageRequest, _: ImageResult.Metadata -> onLoadSuccess() }
            )
        }
    }

    private fun onLoadStart() {
        gallery_progress?.isVisible = true
    }

    private fun onLoadError(throwable: Throwable) {
        gallery_progress?.isVisible = false
        gallery_error_message?.text = throwable.message
    }

    private fun onLoadSuccess() {
        gallery_progress?.isVisible = false
    }

    private fun onLoadProgress(progress: Progress?) {
        progress?.apply { gallery_progress.progress = (progress() * 100).toInt() }
    }
}