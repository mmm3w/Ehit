package com.mitsuki.ehit.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.statusBarHeight
import com.mitsuki.armory.extend.toast
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentGalleryDetailBinding
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.ui.activity.GalleryActivity
import com.mitsuki.ehit.ui.activity.GalleryCommentActivity
import com.mitsuki.ehit.ui.activity.GalleryMoreInfoActivity
import com.mitsuki.ehit.ui.adapter.*
import com.mitsuki.ehit.ui.adapter.gallerydetail.*
import com.mitsuki.ehit.viewmodel.GalleryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryDetailFragment : BaseFragment(R.layout.fragment_gallery_detail) {

    private val mViewModel: GalleryDetailViewModel
            by createViewModelLazy(GalleryDetailViewModel::class, { viewModelStore })

    private val mPreviewAdapter: GalleryDetailPreviewAdapter
            by lazy { GalleryDetailPreviewAdapter() }

    private val mConcatPreviewAdapter by lazy {
        mPreviewAdapter.withLoadStateHeaderAndFooter(
            header = DefaultLoadStateAdapter(mPreviewAdapter),
            footer = DefaultLoadStateAdapter(mPreviewAdapter)
        )
    }

    private val mInitialLoadState: GalleryDetailInitialLoadStateAdapter by lazy {
        GalleryDetailInitialLoadStateAdapter(mPreviewAdapter)
    }
    private val mHeader: GalleryDetailHeader by lazy {
        GalleryDetailHeader(mViewModel.infoWrap)
    }
    private val mOperating: GalleryDetailOperatingBlock by lazy {
        GalleryDetailOperatingBlock(mViewModel.infoWrap)
    }
    private val mTag: GalleryDetailTagAdapter by lazy {
        GalleryDetailTagAdapter(mViewModel.infoWrap)
    }
    private val mComment: GalleryDetailCommentAdapter by lazy {
        GalleryDetailCommentAdapter(mViewModel.infoWrap)
    }
    private val mConcatAdapter: ConcatAdapter by lazy {
        ConcatAdapter(mHeader, mInitialLoadState, mOperating, mTag, mComment)
    }

    private val binding by viewBinding(FragmentGalleryDetailBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true).apply {
            startContainerColor = Color.WHITE
            endContainerColor = Color.WHITE
        }

        mViewModel.initData(arguments)
        lifecycleScope.launchWhenCreated {
            mPreviewAdapter.loadStateFlow.collectLatest { loadStates ->
                mInitialLoadState.loadState = loadStates.refresh
                mOperating.loadState = loadStates.refresh
                mTag.loadState = loadStates.refresh
                mComment.loadState = loadStates.refresh
            }
        }

        mViewModel.galleryDetail.observe(this@GalleryDetailFragment, {
            mPreviewAdapter.submitData(lifecycle, it)
        })


        mHeader.event.observe(this, this::onHeaderEvent)
        mOperating.event.observe(this, this::onOperatingEvent)
        mTag.event.observe(this, this::onTagEvent)
        mComment.event.observe(this) {
            //TODO 更多评论
            requireActivity().apply {
                startActivity(Intent(this, GalleryCommentActivity::class.java))
            }
        }

        mPreviewAdapter.event.observe(this, this::onPreviewClick)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setTransitionName(view, mViewModel.itemTransitionName)
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }
        binding?.topBar?.topTitleLayout?.apply {
            layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                setMargins(
                    leftMargin,
                    topMargin + requireActivity().statusBarHeight(),
                    rightMargin,
                    bottomMargin
                )
            }
        }

        binding?.topBar?.topTitleBack?.setOnClickListener { requireActivity().onBackPressed() }

        binding?.galleryDetail?.apply {
            infoList {
                setPadding(
                    0,
                    requireActivity().statusBarHeight() + dp2px(56f).toInt(),
                    0,
                    0
                )
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mConcatAdapter
            }

            previewList {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = mConcatPreviewAdapter
            }

            bindbarMove {
                binding?.topBar?.topTitleLayout?.translationY =
                    it.coerceIn(-(dp2px(56f) + requireActivity().statusBarHeight()), 0f)
            }
        }

        mViewModel.event.observe(viewLifecycleOwner, this::onViewEvent)
    }

    private fun onViewEvent(event: GalleryDetailViewModel.Event) {
        event.rateNotifyItem?.dispatch(mOperating)

        event.message?.apply {
            Snackbar.make(requireView(), this, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun onHeaderEvent(event: GalleryDetailHeader.Event) {
        when (event) {
            is GalleryDetailHeader.Event.Uploader -> {
                //TODO 跳转上传者搜索
            }
            is GalleryDetailHeader.Event.Category -> {
                //TODO 跳转分类搜索
            }
        }
    }

    private fun onOperatingEvent(event: GalleryDetailOperatingBlock.Event) {
        when (event) {
            GalleryDetailOperatingBlock.Event.Read -> goPreview(0)
            GalleryDetailOperatingBlock.Event.Download -> {
                //TODO 下载
            }
            GalleryDetailOperatingBlock.Event.Score -> {
                MaterialDialog(requireContext()).show {
                    title(res = R.string.text_rate)
                    customView(viewRes = R.layout.dialog_rating)
                    getCustomView().findViewById<RatingView>(R.id.rating_target)?.rating =
                        mViewModel.infoWrap.partInfo.rating
                    positiveButton(res = R.string.text_confirm) {
                        it.getCustomView()
                            .findViewById<RatingView>(R.id.rating_target)?.rating?.apply {
                                mViewModel.submitRating(this)
                            }
                    }
                    negativeButton(res = R.string.text_cancel)
                    lifecycleOwner(this@GalleryDetailFragment)
                }
            }
            GalleryDetailOperatingBlock.Event.SimilaritySearch -> {
                //TODO 相似搜索
            }
            GalleryDetailOperatingBlock.Event.MoreInfo -> {
                Intent(requireActivity(), GalleryMoreInfoActivity::class.java).apply {
                    putExtra(DataKey.GALLERY_ID, mViewModel.baseInfo.gid)
                    putExtra(DataKey.GALLERY_TOKEN, mViewModel.baseInfo.token)
                    startActivity(this)
                }
            }
        }
    }

    private fun onTagEvent(tag: String) {
        //TODO tag点击
        toast(tag)
    }

    private fun onPreviewClick(item: ImageSource) {
        goPreview(item.index)
    }

    private fun goPreview(index: Int) {
        startActivity(Intent(requireActivity(), GalleryActivity::class.java).apply {
            putExtra(DataKey.GALLERY_INDEX, index)
            putExtra(DataKey.GALLERY_PAGE, mViewModel.infoWrap.partInfo.page)
            putExtra(DataKey.GALLERY_TOKEN, mViewModel.baseInfo.token)
            putExtra(DataKey.GALLERY_ID, mViewModel.baseInfo.gid)
        })
    }


}