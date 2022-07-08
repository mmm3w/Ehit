package com.mitsuki.ehit.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
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
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.utils.InitialGate
import com.mitsuki.ehit.crutch.utils.PagingEmptyValve
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.*
import com.mitsuki.ehit.ui.common.widget.ListFloatHeader
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.databinding.FragmentGalleryListBinding
import com.mitsuki.ehit.model.activityresult.GallerySearchActivityResultContract
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import com.mitsuki.ehit.ui.main.adapter.GalleryClick
import com.mitsuki.ehit.ui.main.adapter.GalleryListAdapter
import com.mitsuki.ehit.ui.search.dialog.QuickSearchPanel
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    private val mQuickSearch by lazy {
        QuickSearchPanel { type, key ->
            mViewModel.galleryListCondition(type, key)
            mViewModel.refreshEnable.postValue(false)
            mEmptyValve.enable()
            resetSearchBar()
            mMainAdapter.refresh()
        }
    }

    @Inject
    lateinit var shareData: ShareData

    private val searchActivityLaunch: ActivityResultLauncher<GalleryDataKey> =
        registerForActivityResult(GallerySearchActivityResultContract()) {
            it?.apply {
                mViewModel.updateSearchKey(this)
                //禁用下拉刷新效果
                mViewModel.refreshEnable.postValue(false)
                //新的搜索去置空列表
                mEmptyValve.enable()
                resetSearchBar()
                mMainAdapter.refresh()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)

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
                        mStateAdapter.listState = ListStatesAdapter.ListState.Refresh
                    }
                    is LoadState.NotLoading -> {
                        mGate.trigger()
                        binding?.galleryListRefresh?.isRefreshing = false
                        mViewModel.refreshEnable.postValue(it.prepend.endOfPaginationReached && mGate.ignore())

                        mStateAdapter.listState =
                            if (mMainAdapter.itemCount == 0)
                                ListStatesAdapter.ListState.Message(string(R.string.text_content_empty))
                            else
                                ListStatesAdapter.ListState.None
                        mStateAdapter.isRefreshEnable = true
                    }
                    is LoadState.Error -> {
                        mGate.prep(false)
                        binding?.galleryListRefresh?.isRefreshing = false
                        //TODO 加载异常的时候it.prepend.endOfPaginationReached有点问题，后续再进行问题追踪
//                        mViewModel.refreshEnable.postValue(it.prepend.endOfPaginationReached && mGate.ignore())
                        mViewModel.refreshEnable.postValue(mGate.ignore())
                        mStateAdapter.apply {
                            //既然刷新状态让你显示，那么错误状态也别显示了
                            listState =
                                if (isRefreshEnable) ListStatesAdapter.ListState.Error((it.refresh as LoadState.Error).error)
                                else ListStatesAdapter.ListState.None
                            if (!isRefreshEnable) {
                                requireContext().showToast((it.refresh as LoadState.Error).error.message)
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

        when {
            shareData.spInitial -> {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_gallery_list_fragment_to_nav_first_time)
                return
            }
            shareData.spSecurity && AppHolder.isLocked -> {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_gallery_list_fragment_to_nav_security)
                return
            }
        }
        (requireActivity() as? MainActivity)?.setDrawerEnable(true)
        binding?.apply {
            galleryList.apply {
                setPadding(0, paddingTop + requireActivity().statusBarHeight(), 0, 0)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mAdapter

                addOnScrollListener(ListFloatHeader(topBar.topSearchLayout) {
                    mViewModel.searchBarTranslationY = it
                })
            }

            topBar.topSearchLayout.apply {
                translationY = mViewModel.searchBarTranslationY
                layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                    setMargins(
                        leftMargin,
                        topMargin + requireActivity().statusBarHeight(),
                        rightMargin,
                        bottomMargin
                    )
                }

                setOnClickListener {
                    mViewModel.currentSearchKey?.also { key ->
                        val name = string(R.string.transition_name_gallery_list_toolbar)
                        val options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                requireActivity(), it, name
                            )
                        searchActivityLaunch.launch(key, options)
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

        mMainAdapter.isPageShow = shareData.spShowPage
        mMainAdapter.receiver<GalleryClick>("click")
            .isClick()
            .observe(viewLifecycleOwner, ::onDetailNavigation)

        mViewModel.searchBarText.observe(viewLifecycleOwner) {
            binding?.topBar?.topSearchText?.text = it
        }

        mViewModel.refreshEnable.observe(viewLifecycleOwner) {
            binding?.galleryListRefresh?.isEnabled = it
        }

        lifecycleScope.launchWhenStarted {
            mViewModel.galleryList.collect {
                mEmptyValve.submitData(lifecycle, mMainAdapter, it)
            }
        }
    }


    private fun onDetailNavigation(galleryClick: GalleryClick) {
        /* 同时点击两个item会导致同时进入该方法，后一个进入会导致Navigation.findNavController抛出异常 */
        /* 接收处需要防抖处理*/
        with(galleryClick) {
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.action_gallery_list_fragment_to_gallery_detail_fragment,
                    bundleOf(DataKey.GALLERY_INFO to data),
                    null,
                    FragmentNavigatorExtras(target to data.itemTransitionName)
                )
        }
    }

    private fun showPageJumpDialog() {
        if (mViewModel.maxPage <= 1) return
        PageDialog(mViewModel.maxPage) {
            mViewModel.galleryListPage(it)
            mViewModel.refreshEnable.postValue(false)
            mEmptyValve.enable()
            resetSearchBar()
            mMainAdapter.refresh()
        }.show(childFragmentManager, "page")
    }

    private fun showQuickSearchPanel() {
        mQuickSearch.show(childFragmentManager, "QuickSearchPanel")
    }

    private fun resetSearchBar() {
        binding?.topBar?.topSearchLayout?.animate { translationY(0f) }
        mViewModel.searchBarTranslationY = 0f
    }
}