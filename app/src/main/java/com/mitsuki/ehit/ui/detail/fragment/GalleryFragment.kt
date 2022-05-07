package com.mitsuki.ehit.ui.detail.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.dispose
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import com.mitsuki.armory.imagegesture.ImageGesture
import com.mitsuki.armory.imagegesture.StartType
import com.mitsuki.armory.loadprogress.Progress
import com.mitsuki.armory.loadprogress.ProgressProvider
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.FragmentGalleryBinding
import com.mitsuki.ehit.ui.common.dialog.BottomMenuDialogFragment
import com.mitsuki.ehit.ui.detail.activity.GalleryActivity
import com.mitsuki.ehit.ui.detail.widget.GalleryImageGesture
import com.mitsuki.ehit.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : BaseFragment(R.layout.fragment_gallery) {

    private val mViewModel: GalleryViewModel by viewModels()

    private var mImageGesture: ImageGesture? = null

    private val binding by viewBinding(FragmentGalleryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mImageGesture =
            binding?.galleryImage?.run {
                GalleryImageGesture(this).apply {
                    startType = StartType.TOP
                    onLongPress = this@GalleryFragment::showGalleryMenu
                    onAreaTap = this@GalleryFragment::onAreaTap
                }
            }
        binding?.galleryIndex?.text = (mViewModel.index + 1).toString()
        binding?.galleryErrorRetry?.setOnClickListener { mViewModel.retry() }

        mViewModel.loadUrl.observe(viewLifecycleOwner, Observer(this::onLoadImage))
        mViewModel.state.observe(viewLifecycleOwner, Observer(this::onLoadState))
        ProgressProvider.event(mViewModel.tag)
            .observe(viewLifecycleOwner, this@GalleryFragment::onLoadProgress)

        mViewModel.obtainData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mImageGesture = null
    }

    private fun onLoadImage(url: String) {
        binding?.galleryImage?.apply {
            dispose()
            load(url) {
                memoryCacheKey(mViewModel.largeCacheTag)
                size(Size.ORIGINAL)
                listener(
                    onStart = { onImageLoadStart() },
                    onSuccess = { _: ImageRequest, _: SuccessResult -> onImageLoadFinish() },
                    onError = { _: ImageRequest, error: ErrorResult -> onImageLoadError(error.throwable) },
                    onCancel = { onImageLoadFinish() }
                )
            }
        }
    }

    private fun onLoadState(state: GalleryViewModel.LoadState) {
        binding?.galleryLoading?.isVisible = state.loading
        binding?.galleryErrorMessage?.text = state.error ?: ""
        binding?.galleryErrorRetry?.isVisible = state.error != null
    }

    private fun onImageLoadStart() {
        binding?.galleryProgress?.isVisible = true
        binding?.galleryErrorMessage?.text = ""
    }

    private fun onImageLoadError(throwable: Throwable) {
        binding?.galleryErrorMessage?.text = throwable.message
        binding?.galleryErrorRetry?.isVisible = true
        onImageLoadFinish()
    }

    private fun onImageLoadFinish() {
        binding?.galleryProgress?.isVisible = false
    }

    private fun onLoadProgress(progress: Progress?) {
        mViewModel.changeLoadingState(false)
        binding?.galleryProgress?.isVisible = true
        progress?.apply {
            binding?.galleryProgress?.progress =
                (currentBytes.toDouble() / contentLength.toDouble() * 100).toInt()
        }
    }

    private fun showGalleryMenu() {
        BottomMenuDialogFragment(
            intArrayOf(
                R.string.text_refresh,
                R.string.text_share,
                R.string.text_save_to_system_album,
                R.string.text_save_to
            )
        ) {
            when (it) {
                0 -> mViewModel.retry()
                1 -> {

                }
                2 -> {

                }
                3 -> {

                }
            }
            true
        }.show(childFragmentManager, "menu")

    }

    private fun onAreaTap(index: Int) {
        //TODO 需要根据阅读方向做出调整
        when (index) {
            0, 3 -> (requireActivity() as GalleryActivity).nextPage()
            1, 2 -> (requireActivity() as GalleryActivity).previousPage()
            4, 5 -> (requireActivity() as GalleryActivity).showReadConfig()
        }
    }
}