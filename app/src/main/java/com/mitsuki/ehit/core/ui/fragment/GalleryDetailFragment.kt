package com.mitsuki.ehit.core.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.ui.activity.GalleryActivity
import com.mitsuki.ehit.core.ui.adapter.*
import com.mitsuki.ehit.core.viewmodel.GalleryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_detail.*
import kotlinx.android.synthetic.main.part_top_title_bar.*
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


    private val mHeader: GalleryDetailHeadAdapter by lazy {
        GalleryDetailHeadAdapter(mViewModel.detailWrap)
    }
    private val mInitialLoadState: GalleryDetailInitialLoadStateAdapter by lazy {
        GalleryDetailInitialLoadStateAdapter(mPreviewAdapter)
    }
    private val mOperating: GalleryDetailOperatingAdapter by lazy {
        GalleryDetailOperatingAdapter(mViewModel.detailWrap)
    }
    private val mTag: GalleryDetailTagAdapter by lazy {
        GalleryDetailTagAdapter(mViewModel.detailWrap)
    }
    private val mComment: GalleryDetailCommentAdapter by lazy {
        GalleryDetailCommentAdapter(mViewModel.detailWrap)
    }
    private val mConcatAdapter: ConcatAdapter by lazy {
        ConcatAdapter(mHeader, mInitialLoadState, mOperating, mTag, mComment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)

        lifecycleScope.launchWhenCreated {
            mPreviewAdapter.loadStateFlow.collectLatest { loadStates ->
                mInitialLoadState.loadState = loadStates.refresh
                mOperating.loadState = loadStates.refresh
                mTag.loadState = loadStates.refresh
                mComment.loadState = loadStates.refresh

                gallery_detail?.endOfPrepend = loadStates.prepend.endOfPaginationReached
            }
        }

        mViewModel.galleryDetail.observe(this@GalleryDetailFragment, androidx.lifecycle.Observer {
            mPreviewAdapter.submitData(lifecycle, it)
        })

        mPreviewAdapter.currentItem.observe(this, Observer(this::onPreviewClick))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        gallery_detail_card?.transitionName = mViewModel.itemTransitionName
        top_title_back?.setOnClickListener { requireActivity().onBackPressed() }

        gallery_detail?.apply {
            infoList {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mConcatAdapter
            }

            previewList {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = mConcatPreviewAdapter
            }

            setListener(
                pageJumpListener = { showPageJumpDialog() }
            )
        }
    }

    private fun showPageJumpDialog() {
        MaterialDialog(requireContext()).show {
            input(inputType = InputType.TYPE_CLASS_NUMBER) { _, text ->
                //配置页码，刷新数据
                mViewModel.galleryDetailPage(text.toString().toIntOrNull() ?: 0)
                mPreviewAdapter.refresh()
            }
            title(R.string.title_page_go_to)
            positiveButton(R.string.text_confirm)
            lifecycleOwner(this@GalleryDetailFragment)
        }
    }

    private fun onPreviewClick(item: ImageSource) {
        startActivity(Intent(requireActivity(), GalleryActivity::class.java).apply {
            putExtra(DataKey.GALLERY_INDEX, item.index)
            putExtra(DataKey.GALLERY_PAGE, mViewModel.detailWrap.partInfo.page)
            putExtra(DataKey.GALLERY_TOKEN, mViewModel.baseInfo.token)
            putExtra(DataKey.GALLERY_ID, mViewModel.baseInfo.gid)
        })
    }

}