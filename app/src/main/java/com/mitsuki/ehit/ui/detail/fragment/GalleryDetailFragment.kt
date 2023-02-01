package com.mitsuki.ehit.ui.detail.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.armory.base.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.*
import com.mitsuki.ehit.databinding.FragmentGalleryDetailBinding
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.service.download.DownloadEvent
import com.mitsuki.ehit.ui.detail.activity.GalleryActivity
import com.mitsuki.ehit.ui.comment.activity.GalleryCommentActivity
import com.mitsuki.ehit.ui.detail.activity.GalleryMoreInfoActivity
import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.detail.adapter.*
import com.mitsuki.ehit.ui.detail.dialog.DownloadRangeDialog
import com.mitsuki.ehit.ui.detail.dialog.FavoriteDialog
import com.mitsuki.ehit.ui.common.widget.CircularProgressDrawable
import com.mitsuki.ehit.ui.common.widget.HeartDrawable
import com.mitsuki.ehit.ui.detail.dialog.RatingDialog
import com.mitsuki.ehit.viewmodel.GalleryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class GalleryDetailFragment : BindingFragment<FragmentGalleryDetailBinding>(
    R.layout.fragment_gallery_detail,
    FragmentGalleryDetailBinding::bind
) {

    private val mViewModel: GalleryDetailViewModel
            by createViewModelLazy(GalleryDetailViewModel::class, { viewModelStore })

    private val mStatesAdapter by lazy { GalleryDetailLoadStatesAdapter { mViewModel.loadInfo() } }
    private val mHeader: GalleryDetailHeader by lazy { GalleryDetailHeader() }

    private val mOperating: GalleryDetailOperatingBlock by lazy { GalleryDetailOperatingBlock() }
    private val mTag: GalleryDetailTagAdapter by lazy { GalleryDetailTagAdapter(mViewModel.myTags) }
    private val mComment: GalleryDetailCommentAdapter by lazy {
        GalleryDetailCommentAdapter(
            mViewModel.myComments
        )
    }
    private val mCommentHint: GalleryDetailCommentHintAdapter by lazy { GalleryDetailCommentHintAdapter() }

    private val mInfoAdapter by lazy {
        ConcatAdapter(mHeader, mStatesAdapter, mOperating, mTag, mComment, mCommentHint)
    }

    private val mMainAdapter: GalleryDetailPreviewAdapter
            by lazy { GalleryDetailPreviewAdapter(mViewModel.gid, mViewModel.token) }
    private val mLoadHeader by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mLoadFooter by lazy { DefaultLoadStateAdapter(mMainAdapter) }

    private val mPreviewAdapter by lazy { ConcatAdapter(mLoadHeader, mMainAdapter, mLoadFooter) }

    private var mDataPickJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true).apply {
            startContainerColor = color(R.color.background_color_general)
            endContainerColor = color(R.color.background_color_general)
        }

        if (savedInstanceState == null) {
            mViewModel.initData(arguments)
        }


        mViewModel.infoStates.observe(this) { infoStates ->
            mHeader.data = infoStates.header
            mStatesAdapter.loadState = infoStates.loadState
            mOperating.data = infoStates.part
            mCommentHint.commentState = infoStates.commentState

            binding?.topBar?.topTitleRefresh?.isVisible = infoStates.favorite != null
            binding?.topBar?.topTitleFavorite?.apply {
                isVisible = infoStates.favorite != null
                infoStates.favorite?.apply {
                    (drawable as? HeartDrawable)?.setChecked(this)
                }
                isSelected = infoStates.favorite == true
            }
        }

        mViewModel.receiver<Int>("datapick").observe(this) {
            mDataPickJob?.cancel()
            mDataPickJob = lifecycleScope.launchWhenCreated {
                mViewModel.detailImage.collect { mMainAdapter.submitData(it) }
            }
        }

        if (savedInstanceState == null) {
            mViewModel.loadInfo()
        }
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
//
        binding?.topBar?.topTitleBack?.setOnClickListener { requireActivity().onBackPressed() }
        binding?.topBar?.topTitleRefresh?.apply {
            setImageDrawable(CircularProgressDrawable().apply {
                strokeWidth = dp2px(1.9f)
                centerRadius = dp2px(6.4f)
                color = color(R.color.icon_tint_general)
                setStartEndTrim(0.1f, 0.9f)
                setArrowDimensions(dp2px(10f), dp2px(5f))
            })
            setOnClickListener {
                mViewModel.clearCache()
                mViewModel.loadInfo()
            }
        }
        binding?.topBar?.topTitleFavorite?.apply {
            setImageDrawable(HeartDrawable().apply {
                heartHintColor = color(R.color.icon_tint_general)
                heartHintStroke = dp2px(1.9f)
            })
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
        binding?.topBar?.topTitleMenu?.apply {
            setOnClickListener {
                showPopupMenu(context, R.menu.menu_gallery_detail) {

                }
            }
        }

        binding?.topControl?.topTitleGoTop?.setOnClickListener {
            binding?.galleryDetail?.goToPreviewTop()
        }

        binding?.topControl?.topTitleJump?.setOnClickListener {
            //TODO  show page dialog
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
                adapter = mInfoAdapter
            }

            previewList {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = mPreviewAdapter
            }

            bindBarMove {
                binding?.topBar?.topTitleLayout?.translationY =
                    it.coerceIn(-(dp2px(56f) + requireActivity().statusBarHeight()), 0f)
            }
            bindControlEvent {
                binding?.topControl?.topTitleLayout?.animate {
                    translationY(
                        if (it) dp2px(-56f) else requireActivity().statusBarHeight().toFloat()
                    )
                }
            }
            restoreTranslationY(mViewModel.viewTranslationY)
            bindState {
                mViewModel.viewTranslationY = it
            }
        }

        mHeader.receiver<String>("header")
            .isClick()
            .observe(viewLifecycleOwner, this::onHeaderEvent)

        mViewModel.receiver<String>("rate")
            .isClick()
            .observe(viewLifecycleOwner) { mOperating.notifyItemChanged(0) }

        mOperating.receiver<String>("operating")
            .isClick()
            .observe(viewLifecycleOwner, this::onOperatingEvent)

        mTag.receiver<Pair<String, String>>("tag")
            .isClick()
            .observe(viewLifecycleOwner, this::onTagNavigation)

        mComment.receiver<String>("comment")
            .isClick()
            .observe(viewLifecycleOwner) { goComment() }

        mCommentHint.receiver<String>("comment")
            .isClick()
            .observe(viewLifecycleOwner) { goComment() }

        mMainAdapter.receiver<ImageSource>("detail")
            .isClick()
            .observe(viewLifecycleOwner, this::onPreviewClick)

        mViewModel.receiver<String>("toast").observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }

        mViewModel.receiver<Boolean>("loading").observe(viewLifecycleOwner) {
            if (it) startRefreshAnimate() else finishRefreshAnimate()
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
            GalleryDetailOperatingBlock.DOWNLOAD -> showDownloadDialog()
            GalleryDetailOperatingBlock.SCORE -> showRatingDialog()
            GalleryDetailOperatingBlock.SIMILARITYSEARCH -> onNameNavigation()
            GalleryDetailOperatingBlock.MOREINFO -> {
                startActivity(Intent(requireActivity(), GalleryMoreInfoActivity::class.java).apply {
                    putExtra(DataKey.GALLERY_ID, mViewModel.gid)
                    putExtra(DataKey.GALLERY_TOKEN, mViewModel.token)
                })
            }
        }
    }

    private fun onPreviewClick(item: ImageSource) {
        goPreview(item.index)
    }

    private fun goPreview(index: Int) {
        startActivity(Intent(requireActivity(), GalleryActivity::class.java).apply {
            putExtra(DataKey.GALLERY_INDEX, index + 1)
            putExtra(DataKey.GALLERY_PAGE, mViewModel.page)
            putExtra(DataKey.GALLERY_TOKEN, mViewModel.token)
            putExtra(DataKey.GALLERY_ID, mViewModel.gid)
        })
    }

    private fun goComment() {
        startActivity(Intent(requireActivity(), GalleryCommentActivity::class.java).apply {
            putExtra(DataKey.GALLERY_ID, mViewModel.gid)
            putExtra(DataKey.GALLERY_TOKEN, mViewModel.token)
            putExtra(DataKey.GALLERY_API_KEY, mViewModel.apiKey)
            putExtra(DataKey.GALLERY_API_UID, mViewModel.apiUID)
            putExtra(DataKey.GALLERY_NAME, mViewModel.title)
        })
    }

    private fun startRefreshAnimate() {
        (binding?.topBar?.topTitleRefresh?.drawable as? CircularProgressDrawable)?.start()
    }

    private fun finishRefreshAnimate() {
        (binding?.topBar?.topTitleRefresh?.drawable as? CircularProgressDrawable)?.stop()
    }

    private fun onTagNavigation(tag: Pair<String, String>) {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_gallery_detail_fragment_to_gallery_list_fragment,
                bundleOf(DataKey.GALLERY_TYPE_TAG to "${tag.first}:${tag.second}"),
                null,
                null
            )
    }

    private fun onNameNavigation() {
        Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(
                R.id.action_gallery_detail_fragment_to_gallery_list_fragment,
                bundleOf(
                    DataKey.GALLERY_SEARCH_KEY to mViewModel.galleryName,
                    DataKey.GALLERY_SEARCH_KEY_BY_QUERY to false
                ),
                null,
                null
            )
    }

    private fun onUploaderNavigation() {
        Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(
                R.id.action_gallery_detail_fragment_to_gallery_list_fragment,
                bundleOf(DataKey.GALLERY_TYPE_UPLOADER to mViewModel.uploader),
                null,
                null
            )
    }

    private fun showDownloadDialog() {
        DownloadRangeDialog(mViewModel.page) { s, e ->
            DownloadEvent.startDownload(requireContext(), mViewModel.obtainDownloadMessage(s, e))
        }.show(childFragmentManager, "download")
    }

    private fun showRatingDialog() {
        RatingDialog(mViewModel.rating) {
            mViewModel.submitRating(it)
        }.show(childFragmentManager, "rating")
    }

    private fun showFavoriteDialog() {
        FavoriteDialog(mViewModel.favoriteName) {
            mViewModel.submitFavorites(it)
        }.show(childFragmentManager, "favorite")
    }

}