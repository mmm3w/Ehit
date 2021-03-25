package com.mitsuki.ehit.core.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
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
import com.mitsuki.armory.extend.toast
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.being.extend.observe
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.ui.activity.GalleryActivity
import com.mitsuki.ehit.core.ui.activity.GalleryMoreInfoActivity
import com.mitsuki.ehit.core.ui.adapter.*
import com.mitsuki.ehit.core.ui.adapter.gallerydetail.*
import com.mitsuki.ehit.core.viewmodel.GalleryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_detail.*
import kotlinx.android.synthetic.main.top_bar_detail_ver.*
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
        GalleryDetailHeader(mViewModel.detailWrap)
    }
    private val mOperating: GalleryDetailOperatingBlock by lazy {
        GalleryDetailOperatingBlock(mViewModel.detailWrap)
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

        mViewModel.galleryDetail.observe(this@GalleryDetailFragment, {
            mPreviewAdapter.submitData(lifecycle, it)
        })


        mHeader.event.observe(this, this::onHeaderEvent)
        mOperating.event.observe(this, this::onOperatingEvent)
        mTag.event.observe(this, this::onTagEvent)
        mComment.event.observe(this) {
            //TODO 更多评论
        }

        mPreviewAdapter.event.observe(this, this::onPreviewClick)
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

        mViewModel.event.observe(viewLifecycleOwner, this::onViewEvent)
    }

    //TODO 右上角扩展菜单

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
            GalleryDetailOperatingBlock.Event.Read -> {
                //TODO 阅读
            }
            GalleryDetailOperatingBlock.Event.Download -> {
                //TODO 下载
            }
            GalleryDetailOperatingBlock.Event.Score -> {
                MaterialDialog(requireContext()).show {
                    title(res = R.string.text_rate)
                    customView(viewRes = R.layout.dialog_rating)
                    getCustomView().findViewById<RatingView>(R.id.rating_target)?.rating =
                        mViewModel.detailWrap.partInfo.rating
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
                    putExtra(DataKey.GALLERY_DETAIL, mViewModel.detailWrap.sourceDetail)
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
        startActivity(Intent(requireActivity(), GalleryActivity::class.java).apply {
            putExtra(DataKey.GALLERY_INDEX, item.index)
            putExtra(DataKey.GALLERY_PAGE, mViewModel.detailWrap.partInfo.page)
            putExtra(DataKey.GALLERY_TOKEN, mViewModel.baseInfo.token)
            putExtra(DataKey.GALLERY_ID, mViewModel.baseInfo.gid)
        })
    }

}