package com.mitsuki.ehit.ui.detail.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.mitsuki.armory.adapter.notify.NotifyItem
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.armory.base.extend.statusBarHeight
import com.mitsuki.armory.base.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentGalleryDetailBinding
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.ui.detail.activity.GalleryActivity
import com.mitsuki.ehit.ui.detail.activity.GalleryCommentActivity
import com.mitsuki.ehit.ui.detail.activity.GalleryMoreInfoActivity
import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.detail.adapter.*
import com.mitsuki.ehit.viewmodel.GalleryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryDetailFragment : BaseFragment(R.layout.fragment_gallery_detail) {

    private val mViewModel: GalleryDetailViewModel by viewModels()

    private val mPreviewAdapter: GalleryDetailPreviewAdapter
            by lazy { GalleryDetailPreviewAdapter(mViewModel.gid, mViewModel.token) }

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
        GalleryDetailHeader(mViewModel.headerInfo)
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

                binding?.topBar?.topTitleFavorite?.apply {
                    isVisible = mInitialLoadState.isOver
                    if (loadStates.refresh is LoadState.NotLoading) {
                        isSelected = mViewModel.isFavorited
                    }
                }

                if (mInitialLoadState.isOver && loadStates.refresh !is LoadState.Loading) {
                    finishRefreshAnimate()
                }
            }
        }

        mViewModel.galleryDetail.observe(this, {
            mPreviewAdapter.submitData(lifecycle, it)
        })


        mHeader.receiver<String>("header").observe(this, this::onHeaderEvent)
        mOperating.receiver<String>("operating").observe(this, this::onOperatingEvent)
        mTag.receiver<Pair<String, String>>("tag").observe(this, this::onTagNavigation)
        mComment.receiver<String>("comment").observe(this) {
            startActivity(Intent(requireActivity(), GalleryCommentActivity::class.java).apply {
                putExtra(DataKey.GALLERY_ID, mViewModel.baseInfo.gid)
                putExtra(DataKey.GALLERY_TOKEN, mViewModel.baseInfo.token)
            })
        }

        mPreviewAdapter.receiver<ImageSource>("detail").observe(this, this::onPreviewClick)
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
        binding?.topBar?.topTitleRefresh?.apply {
            setImageDrawable(CircularProgressDrawable(requireContext()).apply {
                setStyle(CircularProgressDrawable.DEFAULT)
                strokeWidth = dp2px(1.9f)
                centerRadius = dp2px(6.4f)
                setColorSchemeColors(0xff272926.toInt())
                setStartEndTrim(0.1f, 0.9f)
                arrowEnabled = true
            })
            setOnClickListener {
                startRefreshAnimate()
                mViewModel.clearCache()
                mPreviewAdapter.refresh()
            }
        }
        binding?.topBar?.topTitleFavorite?.apply {
            setOnClickListener {
                if (it.isSelected) {
                    //取消收藏
                    mViewModel.submitFavorites(-1)
                } else {
                    showFavoriteDialog()
                }
            }
            setOnLongClickListener {
                showFavoriteDialog()
                true
            }
        }

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

        mViewModel.receiver<NotifyItem>("rate")
            .observe(viewLifecycleOwner) { it.dispatch(mOperating) }
        mViewModel.receiver<String>("toast").observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
        mViewModel.receiver<String>("fav").observe(viewLifecycleOwner) {
            binding?.topBar?.topTitleFavorite?.isSelected = mViewModel.isFavorited
        }
    }

    private fun onHeaderEvent(event: String) {
        when (event) {
            GalleryDetailHeader.UPLOADER -> onUploaderNavigation()
            GalleryDetailHeader.CATEGORY -> {
                //TODO 跳转分类搜索
            }
        }
    }

    private fun onOperatingEvent(event: String) {
        when (event) {
            GalleryDetailOperatingBlock.READ -> goPreview(0)
            GalleryDetailOperatingBlock.DOWNLOAD -> {
                //TODO 下载
            }
            GalleryDetailOperatingBlock.SCORE -> showRatingDialog()
            GalleryDetailOperatingBlock.SIMILARITYSEARCH -> onNameNavigation()
            GalleryDetailOperatingBlock.MOREINFO -> {
                startActivity(Intent(requireActivity(), GalleryMoreInfoActivity::class.java).apply {
                    putExtra(DataKey.GALLERY_ID, mViewModel.baseInfo.gid)
                    putExtra(DataKey.GALLERY_TOKEN, mViewModel.baseInfo.token)
                })
            }
        }
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

    private fun startRefreshAnimate() {
        (binding?.topBar?.topTitleRefresh?.drawable as? CircularProgressDrawable)?.apply {
            arrowEnabled = false
            start()
        }
    }

    private fun finishRefreshAnimate() {
        (binding?.topBar?.topTitleRefresh?.drawable as? CircularProgressDrawable)?.apply {
            stop()
            setStartEndTrim(0.1f, 0.9f)
            arrowEnabled = true
        }
    }

    private fun onTagNavigation(tag: Pair<String, String>) {
        Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(
                R.id.action_gallery_detail_fragment_to_gallery_list_fragment,
                bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.Tag("${tag.first}:${tag.second}")),
                null,
                null
            )
    }

    private fun onNameNavigation() {
        Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(
                R.id.action_gallery_detail_fragment_to_gallery_list_fragment,
                bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.Normal(mViewModel.galleryName)),
                null,
                null
            )
    }

    private fun onUploaderNavigation() {
        Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(
                R.id.action_gallery_detail_fragment_to_gallery_list_fragment,
                bundleOf(DataKey.GALLERY_PAGE_SOURCE to GalleryPageSource.Uploader(mViewModel.uploader)),
                null,
                null
            )
    }

    private fun showRatingDialog() {
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
            lifecycleOwner(viewLifecycleOwner)
        }
    }

    private fun showFavoriteDialog() {
        MaterialDialog(requireContext()).show {
            title(res = R.string.title_add_favorites)
            listItemsSingleChoice(
                items = GalleryFavorites.favorites,
                initialSelection = GalleryFavorites.findIndex(mViewModel.favoriteName)
            ) { _, index, _ -> mViewModel.submitFavorites(index) }
            lifecycleOwner(viewLifecycleOwner)
        }
    }

}