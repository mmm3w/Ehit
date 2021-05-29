package com.mitsuki.ehit.ui.favourite

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.crutch.ListFloatHeader
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentFavouriteBinding

import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.ui.main.GalleryAdapter
import com.mitsuki.ehit.ui.temp.adapter.GalleryListLoadStateAdapter
import com.mitsuki.ehit.viewmodel.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding?.favouriteCate?.setOnClickListener { showFavouriteSelectPanel() }

        binding?.favouriteRefresh?.apply {
            setProgressViewOffset(false, dp2px(8f).toInt(), dp2px(120f).toInt())
            setOnRefreshListener { mAdapter.refresh() }
        }
    }

    private fun showFavouriteSelectPanel() {

    }


}