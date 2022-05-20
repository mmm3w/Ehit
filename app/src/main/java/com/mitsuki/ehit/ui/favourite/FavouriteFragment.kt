package com.mitsuki.ehit.ui.favourite

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
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
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.ui.common.widget.ListFloatHeader
import com.mitsuki.ehit.crutch.uils.InitialGate
import com.mitsuki.ehit.crutch.uils.PagingEmptyValve
import com.mitsuki.ehit.databinding.FragmentFavouriteBinding

import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.main.GalleryListAdapter
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import com.mitsuki.ehit.viewmodel.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteFragment : BindingFragment<FragmentFavouriteBinding>(
    R.layout.fragment_favourite,
    FragmentFavouriteBinding::bind
) {

    private val mViewModel: FavouriteViewModel by viewModels()

    //大体结构上和list相同
    private val mGate = InitialGate()
    private val mEmptyValve = PagingEmptyValve()

    private val mMainAdapter by lazy { GalleryListAdapter() }
    private val mHeader by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mFooter by lazy { DefaultLoadStateAdapter(mMainAdapter) }
    private val mStateAdapter by lazy { ListStatesAdapter { mMainAdapter.retry() } }

    private val mAdapter by lazy { ConcatAdapter(mHeader, mStateAdapter, mMainAdapter, mFooter) }

    private val favouriteSelectPanel by lazy {
        FavouriteSelectPanel().apply {
            onFavouriteSelect = {
                mViewModel.setFavouriteGroup(it)
                mViewModel.refreshEnable.postValue(false)
                mEmptyValve.enable()
                mMainAdapter.refresh()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        binding?.favouriteRefresh?.apply {
                            if (isEnabled) isRefreshing = true
                        }
                        mStateAdapter.listState = ListStatesAdapter.ListState.Refresh
                    }
                    is LoadState.NotLoading -> {
                        mGate.trigger()
                        binding?.favouriteRefresh?.isRefreshing = false
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
                        binding?.favouriteRefresh?.isRefreshing = false
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
            mViewModel.favouriteList.collect { mEmptyValve.submitData(lifecycle, mMainAdapter, it) }
        }

        mViewModel.count.observe(this) { favouriteSelectPanel.postCountData(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        binding?.apply {
            favouriteTarget.apply {
                setPadding(0, paddingTop + requireActivity().statusBarHeight(), 0, 0)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mAdapter
                topBar.topSearchLayout.apply { addOnScrollListener(ListFloatHeader(this) {}) }
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
            }

            mViewModel.searchBarHint.observe(viewLifecycleOwner) {
                topBar.topSearchText.hint = it
            }

            mViewModel.refreshEnable.observe(viewLifecycleOwner) {
                favouriteRefresh.isEnabled = it
            }

            favouriteCate.setOnClickListener { showFavouriteSelectPanel() }

            favouriteRefresh.apply {
                setProgressViewOffset(false, dp2px(0f).toInt(), dp2px(140f).toInt())
                setOnRefreshListener {
                    mStateAdapter.isRefreshEnable = false
                    mMainAdapter.refresh()
                }
            }
        }
    }


    private fun onDetailNavigation(galleryClick: GalleryListAdapter.GalleryClick) {
        with(galleryClick) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(
                    R.id.action_favourite_fragment_to_gallery_detail_fragment,
                    bundleOf(DataKey.GALLERY_INFO to data),
                    null,
                    FragmentNavigatorExtras(galleryClick.target to data.itemTransitionName)
                )
        }
    }

    private fun showFavouriteSelectPanel() {
        favouriteSelectPanel.show(childFragmentManager, "FavouriteSelectPanel")
    }

}