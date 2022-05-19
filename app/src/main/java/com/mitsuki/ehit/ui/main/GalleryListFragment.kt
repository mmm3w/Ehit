package com.mitsuki.ehit.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.armory.base.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.PagingEmptyValve
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.ui.common.widget.ListFloatHeader
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.FragmentGalleryListBinding
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.ui.search.SearchActivity
import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.search.dialog.QuickSearchPanel
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list) {

    private val binding by viewBinding(FragmentGalleryListBinding::bind)

    private val mViewModel: GalleryListViewModel
            by createViewModelLazy(GalleryListViewModel::class, { viewModelStore })

    //控制下拉刷新的可用性
    private val mGate = InitialGate()
    private val mEmptyValve = PagingEmptyValve<Gallery>()

    private val mMainAdapter by lazy { GalleryAdapter() }
    private val mHeader by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mFooter by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mStateAdapter by lazy { GalleryListStateAdapter(mMainAdapter) }
    private val mAdapter by lazy { ConcatAdapter(mHeader, mStateAdapter, mMainAdapter, mFooter) }

    private val searchActivityLaunch: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            it.data?.getParcelableExtra<GalleryPageSource>(DataKey.GALLERY_PAGE_SOURCE)?.apply {
                mViewModel.galleryListCondition(this)
                //禁用下拉刷新效果
                mViewModel.refreshEnable.postValue(false)
                //新的搜索去置空列表
                mEmptyValve.enable()
                mMainAdapter.refresh()
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("ControlFlowWithEmptyBody")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? MainActivity)?.enableDrawer()
        mViewModel.initData(arguments)

        mMainAdapter.receiver<GalleryAdapter.GalleryClick>("click")
            .observe(this, ::onDetailNavigation)

        lifecycleScope.launchWhenCreated {
            mViewModel.galleryList.collect { mEmptyValve.submitData(mMainAdapter, it) }
        }

        lifecycleScope.launchWhenCreated {
            mMainAdapter.loadStateFlow.collect {
                //过滤掉置空的状态事件
                if (mEmptyValve.emptyStates(it.source)) {
                    return@collect
                }

                mHeader.loadState = it.prepend
                mFooter.loadState = it.append

                when (it.refresh) {
                    is LoadState.Loading -> {
                        mGate.prep(true)
                        binding?.galleryListRefresh?.apply {
                            if (isEnabled) isRefreshing = true
                        }
                        mStateAdapter.listState = GalleryListStateAdapter.ListState.Refresh
                    }
                    is LoadState.NotLoading -> {
                        mGate.trigger()
                        binding?.galleryListRefresh?.isRefreshing = false
                        mViewModel.refreshEnable.postValue(it.prepend.endOfPaginationReached && mGate.ignore())

                        mStateAdapter.listState =
                            if (mMainAdapter.itemCount == 0)
                            //TODO 提示文字替换
                                GalleryListStateAdapter.ListState.Message("empty")
                            else
                                GalleryListStateAdapter.ListState.None
                        mStateAdapter.isRefreshEnable = true
                        binding?.galleryList?.smoothScrollToPosition(0)
                    }
                    is LoadState.Error -> {
                        mGate.prep(false)
                        binding?.galleryListRefresh?.isRefreshing = false
                        mViewModel.refreshEnable.postValue(it.prepend.endOfPaginationReached && mGate.ignore())
                        mStateAdapter.apply {
                            //既然刷新状态让你显示，那么错误状态也别显示了
                            listState =
                                if (isRefreshEnable) GalleryListStateAdapter.ListState.Error((it.refresh as LoadState.Error).error)
                                else GalleryListStateAdapter.ListState.None
                            if (!isRefreshEnable) {
                                //TODO 通过toast或snackBar展示错误信息
                            }
                            //判断完上面的逻辑重置状态
                            isRefreshEnable = true
                        }
                    }
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        mViewModel.searchBarText.observe(viewLifecycleOwner) {
            binding?.topBar?.topSearchText?.text = it
        }

        mViewModel.searchBarHint.observe(viewLifecycleOwner) {
            binding?.topBar?.topSearchText?.hint = it
        }

        mViewModel.refreshEnable.observe(viewLifecycleOwner) {
            binding?.galleryListRefresh?.isEnabled = it
        }

        binding?.galleryList?.apply {
            setPadding(0, paddingTop + requireActivity().statusBarHeight(), 0, 0)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter

            binding?.topBar?.topSearchLayout?.apply {
                translationY = mViewModel.searchBarTranslationY
                addOnScrollListener(ListFloatHeader(this) {
                    mViewModel.searchBarTranslationY = it
                })
            }
        }

        binding?.topBar?.topSearchLayout?.apply {
            layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                setMargins(
                    leftMargin,
                    topMargin + requireActivity().statusBarHeight(),
                    rightMargin,
                    bottomMargin
                )
            }

            setOnClickListener {
                requireActivity().apply {
                    val name = string(R.string.transition_name_gallery_list_toolbar)
                    val options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, name)
                    searchActivityLaunch.launch(Intent(this, SearchActivity::class.java).apply {
                        putExtra(DataKey.GALLERY_PAGE_SOURCE, mViewModel.pageSource)
                    }, options)
                }
            }
        }

        binding?.galleryQuickSearchPanel?.setOnClickListener { showQuickSearchPanel() }

        binding?.galleryPageJump?.setOnClickListener { showPageJumpDialog() }

        binding?.galleryListRefresh?.apply {
            setProgressViewOffset(false, 0, dp2px(140f).toInt())

            setOnRefreshListener {
                //下拉刷新不用置空列表
                //并且禁用列表中的刷新效果
                mStateAdapter.isRefreshEnable = false
                mViewModel.galleryListPage(1)
                mMainAdapter.refresh()
            }
        }
    }

    private fun onDetailNavigation(galleryClick: GalleryAdapter.GalleryClick) {
        with(galleryClick) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(
                    R.id.action_gallery_list_fragment_to_gallery_detail_fragment,
                    bundleOf(DataKey.GALLERY_INFO to data),
                    null,
                    FragmentNavigatorExtras(galleryClick.target to data.itemTransitionName)
                )
        }
    }

    private fun showPageJumpDialog() {
        if (mViewModel.maxPage <= 1) return
        PageDialog(mViewModel.maxPage) {
            mViewModel.galleryListPage(it)
            mMainAdapter.refresh()
        }.show(childFragmentManager, "page")
    }

    private fun showQuickSearchPanel() {
        QuickSearchPanel {
            mViewModel.galleryListCondition(it)
            mMainAdapter.refresh()
        }.show(childFragmentManager, "QuickSearchPanel")
    }
}