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
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.ui.common.widget.ListFloatHeader
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.FragmentFavouriteBinding

import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.main.GalleryAdapter
import com.mitsuki.ehit.ui.main.GalleryListLoadStateAdapter
import com.mitsuki.ehit.viewmodel.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavouriteFragment : BaseFragment(R.layout.fragment_favourite) {

    private val mViewModel: FavouriteViewModel by viewModels()
    private val binding by viewBinding(FragmentFavouriteBinding::bind)

    private val mAdapter by lazy { GalleryAdapter() }
    private val mInitAdapter by lazy { GalleryListLoadStateAdapter(mAdapter) }

    private val mConcatAdapter by lazy {
        val header = DefaultLoadStateAdapter(mAdapter)
        val footer = DefaultLoadStateAdapter(mAdapter)

        mAdapter.addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        ConcatAdapter(header, mInitAdapter, mAdapter, footer)
    }

    private val favouriteSelectPanel by lazy {
        FavouriteSelectPanel().apply {
            onFavouriteSelect = {
                mViewModel.setFavouriteGroup(it)
                mAdapter.refresh()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            mAdapter.loadStateFlow.collectLatest {
                if (mInitAdapter.isOver) {
                    binding?.favouriteRefresh?.isRefreshing = it.refresh is LoadState.Loading
                } else {
                    mInitAdapter.loadState = it.refresh
                }
                binding?.favouriteRefresh?.isEnabled = it.prepend.endOfPaginationReached
            }
        }
        mAdapter.receiver<GalleryAdapter.GalleryClick>("click").observe(this, ::onDetailNavigation)
        mViewModel.count.observe(this, { favouriteSelectPanel.postCountData(it) })
        mViewModel.favouriteList.observe(this, { mAdapter.submitData(lifecycle, it) })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        binding?.favouriteTarget?.apply {
            setPadding(0, paddingTop + requireActivity().statusBarHeight(), 0, 0)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mConcatAdapter
            binding?.topBar?.topSearchLayout?.apply { addOnScrollListener(ListFloatHeader(this)) }
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
        }

        mViewModel.searchBarHint.observe(viewLifecycleOwner, {
            binding?.topBar?.topSearchText?.hint = it
        })

        binding?.favouriteCate?.setOnClickListener { showFavouriteSelectPanel() }

        binding?.favouriteRefresh?.apply {
            setProgressViewOffset(false, dp2px(8f).toInt(), dp2px(120f).toInt())
            setOnRefreshListener { mAdapter.refresh() }
        }
    }

    private fun onDetailNavigation(galleryClick: GalleryAdapter.GalleryClick) {
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