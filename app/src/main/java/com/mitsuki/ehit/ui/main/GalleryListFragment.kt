package com.mitsuki.ehit.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.armory.base.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.uils.InitialGate
import com.mitsuki.ehit.crutch.uils.PagingEmptyValve
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.ui.common.widget.ListFloatHeader
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.databinding.FragmentGalleryListBinding
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.ui.search.SearchActivity
import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import com.mitsuki.ehit.ui.search.dialog.QuickSearchPanel
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryListFragment : BindingFragment<FragmentGalleryListBinding>(
    R.layout.fragment_gallery_list,
    FragmentGalleryListBinding::bind
) {

    private val mViewModel: GalleryListViewModel
            by createViewModelLazy(GalleryListViewModel::class, { viewModelStore })

    //控制下拉刷新的可用性
    private val mGate = InitialGate()
    private val mEmptyValve = PagingEmptyValve()

    private val mMainAdapter by lazy { GalleryListAdapter() }
    private val mHeader by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mFooter by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mStateAdapter by lazy { ListStatesAdapter { mMainAdapter.retry() } }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? MainActivity)?.enableDrawer()
        mViewModel.initData(arguments)

        mMainAdapter.receiver<GalleryListAdapter.GalleryClick>("click")
            .observe(this, ::onDetailNavigation)


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
                        requireBinding().galleryListRefresh.apply {
                            if (isEnabled) isRefreshing = true
                        }
                        mStateAdapter.listState = ListStatesAdapter.ListState.Refresh
                    }
                    is LoadState.NotLoading -> {
                        mGate.trigger()
                        requireBinding().galleryListRefresh.isRefreshing = false
                        mViewModel.refreshEnable.postValue(it.prepend.endOfPaginationReached && mGate.ignore())

                        mStateAdapter.listState =
                            if (mMainAdapter.itemCount == 0)
                            //TODO 提示文字替换
                                ListStatesAdapter.ListState.Message("empty")
                            else
                                ListStatesAdapter.ListState.None
                        mStateAdapter.isRefreshEnable = true
                    }
                    is LoadState.Error -> {
                        mGate.prep(false)
                        requireBinding().galleryListRefresh.isRefreshing = false
                        mViewModel.refreshEnable.postValue(it.prepend.endOfPaginationReached && mGate.ignore())
                        mStateAdapter.apply {
                            //既然刷新状态让你显示，那么错误状态也别显示了
                            listState =
                                if (isRefreshEnable) ListStatesAdapter.ListState.Error((it.refresh as LoadState.Error).error)
                                else ListStatesAdapter.ListState.None
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

        lifecycleScope.launchWhenCreated {
            mViewModel.galleryList.collect { mEmptyValve.submitData(lifecycle, mMainAdapter, it) }
        }
    }


    override fun onViewCreated(
        innBinding: FragmentGalleryListBinding, view: View, savedInstanceState: Bundle?
    ) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        with(innBinding) {
            mViewModel.searchBarText.observe(viewLifecycleOwner) { topBar.topSearchText.text = it }

            mViewModel.searchBarHint.observe(viewLifecycleOwner) { topBar.topSearchText.hint = it }

            mViewModel.refreshEnable.observe(viewLifecycleOwner) {
                galleryListRefresh.isEnabled = it
            }

            galleryList.apply {
                setPadding(0, paddingTop + requireActivity().statusBarHeight(), 0, 0)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mAdapter

                topBar.topSearchLayout.apply {
                    translationY = mViewModel.searchBarTranslationY
                    addOnScrollListener(ListFloatHeader(this) {
                        mViewModel.searchBarTranslationY = it
                    })
                }
            }

            topBar.topSearchLayout.apply {
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

            galleryQuickSearchPanel.setOnClickListener { showQuickSearchPanel() }

            galleryPageJump.setOnClickListener { showPageJumpDialog() }

            galleryListRefresh.apply {
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
    }

    private fun onDetailNavigation(galleryClick: GalleryListAdapter.GalleryClick) {
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