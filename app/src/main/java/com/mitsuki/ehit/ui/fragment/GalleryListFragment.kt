package com.mitsuki.ehit.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.ui.adapter.*
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import com.mitsuki.ehit.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list) {

    private val mViewModel: GalleryListViewModel
            by createViewModelLazy(GalleryListViewModel::class, { viewModelStore })

    private val mMainViewModel: MainViewModel
            by createViewModelLazy(MainViewModel::class, { requireActivity().viewModelStore })

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


    @Suppress("ControlFlowWithEmptyBody")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAdapter.currentItem.observe(this, Observer(this::toDetail))

        lifecycleScope.launchWhenCreated {
            mAdapter.loadStateFlow.collectLatest {
                if (mInitAdapter.isOver) {
                    gallery_list_refresh?.isRefreshing = it.refresh is LoadState.Loading
                } else {
                    mInitAdapter.loadState = it.refresh
                }
                gallery_list_refresh?.isEnabled = it.prepend.endOfPaginationReached
            }
        }

        mViewModel.galleryList.observe(this, Observer { mAdapter.submitData(lifecycle, it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        (mMainViewModel.removeSearchKey(hashCode())?.apply {
            mViewModel.galleryListPage(1)
            mViewModel.galleryListCondition(this)
            mAdapter.refresh()
        } ?: mViewModel.searchKey)?.apply {
            gallery_list?.topBar {
                if (showContent.isNotEmpty())
                    findViewById<TextView>(R.id.top_search_text).text = showContent
            }
        }


        gallery_list?.apply {
            //配置recycleView
            recyclerView {
                layoutManager = LinearLayoutManager(activity)
                adapter = mConcatAdapter
            }

            topBar {
                setOnClickListener {
                    Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                        .navigate(
                            R.id.action_gallery_list_fragment_to_search_fragment,
                            bundleOf(
                                DataKey.GALLERY_FRAGMENT_CODE to this@GalleryListFragment.hashCode(),
                                DataKey.GALLERY_SEARCH_KEY to mViewModel.searchKey
                            ),
                            null,
                            null
                        )
                }
            }

            setListener(
                extendControl = {
                    if (it) gallery_motion_layout?.transitionToStart()
                    else gallery_motion_layout?.transitionToEnd()
                }
            )
        }

        gallery_go_top?.setOnClickListener {
            gallery_list?.recyclerView()?.smoothScrollToPosition(0)
        }

        gallery_page_jump?.setOnClickListener { showPageJumpDialog() }

        gallery_list_refresh?.apply {
            setProgressViewOffset(true, dp2px(36f), dp2px(120f))
            setOnRefreshListener { mAdapter.refresh() }
        }
    }

    override fun onDestroy() {
        mMainViewModel.removeSearchKey(hashCode())
        super.onDestroy()
    }

    private fun toDetail(galleryClick: GalleryAdapter.GalleryClick) {
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
        MaterialDialog(requireContext()).show {
            input(inputType = InputType.TYPE_CLASS_NUMBER) { _, text ->
                mViewModel.galleryListPage(text.toString().toIntOrNull() ?: 1)
                mAdapter.refresh()
            }

            title(R.string.title_page_go_to)
            positiveButton(R.string.text_confirm)
            lifecycleOwner(this@GalleryListFragment)
        }
    }

}