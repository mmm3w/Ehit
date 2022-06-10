package com.mitsuki.ehit.ui.detail.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import coil.clear
import coil.dispose
import coil.drawable.CrossfadeDrawable
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
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.utils.ImageSaver
import com.mitsuki.ehit.databinding.FragmentGalleryBinding
import com.mitsuki.ehit.ui.common.dialog.BottomMenuDialogFragment
import com.mitsuki.ehit.ui.detail.activity.GalleryActivity
import com.mitsuki.ehit.ui.detail.widget.GalleryImageGesture
import com.mitsuki.ehit.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : BindingFragment<FragmentGalleryBinding>(
    R.layout.fragment_gallery,
    FragmentGalleryBinding::bind
) {
    private val mViewModel: GalleryViewModel by viewModels()

    private var mImageGesture: ImageGesture? = null

    private var isLoadSuccess = false

    @Inject
    lateinit var memoryData: MemoryData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mImageGesture =
            binding?.galleryImage?.run {
                GalleryImageGesture(this).apply {
                    startType = when (memoryData.imageZoom) {
                        0 -> StartType.NONE
                        1 -> StartType.TOP
                        2 -> if (memoryData.readOrientation == 0) StartType.RIGHT else StartType.LEFT
                        else -> throw  IllegalArgumentException()
                    }
                    onLongPress = this@GalleryFragment::showGalleryMenu
                    onAreaTap = this@GalleryFragment::onAreaTap
                }
            }
        binding?.galleryIndex?.text = (mViewModel.index + 1).toString()
        binding?.galleryErrorRetry?.setOnClickListener { mViewModel.obtainData() }

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
            if (url.isNotEmpty()) {
                load(url) {
                    memoryCacheKey(mViewModel.largeCacheTag)
                    size(Size.ORIGINAL)
                    listener(
                        onStart = {
                            isLoadSuccess = false
                            onImageLoadStart()
                        },
                        onSuccess = { _: ImageRequest, sr: SuccessResult ->
                            isLoadSuccess = true
                            onImageLoadFinish()
                        },
                        onError = { _: ImageRequest, error: ErrorResult -> onImageLoadError(error.throwable) },
                        onCancel = { onImageLoadFinish() }
                    )
                }
            } else {
                setImageDrawable(null)
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
        binding?.galleryProgress?.layoutDirection = View.LAYOUT_DIRECTION_LTR
        progress?.apply {
            binding?.galleryProgress?.progress =
                (currentBytes.toDouble() / contentLength.toDouble() * 100).toInt()
        }
    }

    private fun showGalleryMenu() {
        if (!isLoadSuccess) return

        BottomMenuDialogFragment(
            intArrayOf(
                R.string.text_refresh,
                R.string.text_share,
                R.string.text_save_to_system_album,
                R.string.text_save_to
            )
        ) {
            when (it) {
                0 -> mViewModel.obtainData()

                1 -> {

                }
                2 -> ((binding?.galleryImage?.drawable as CrossfadeDrawable).end as? BitmapDrawable)?.bitmap?.apply {
                    (requireActivity() as GalleryActivity).saveImageByDefault(
                        mViewModel.index,
                        this
                    )
                }
                3 -> ((binding?.galleryImage?.drawable as CrossfadeDrawable).end as? BitmapDrawable)?.bitmap?.apply {
                    (requireActivity() as GalleryActivity).saveImageByCustom(mViewModel.index, this)
                }
            }
            true
        }.show(childFragmentManager, "menu")

    }

    private fun onAreaTap(index: Int) {
        when (index) {
            0 -> (requireActivity() as GalleryActivity).dyPreviousPage()
            1 -> (requireActivity() as GalleryActivity).previousPage()
            2 -> (requireActivity() as GalleryActivity).dyNextPage()
            3 -> (requireActivity() as GalleryActivity).nextPage()
            4 -> (requireActivity() as GalleryActivity).showReadConfig()
            5 -> (requireActivity() as GalleryActivity).triggerSeekBar()
        }
    }
}