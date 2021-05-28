package com.mitsuki.ehit.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.ListFloatHeader
import com.mitsuki.ehit.crutch.ListScrollTrigger
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentGalleryListBinding
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.ui.search.SearchActivity
import com.mitsuki.ehit.ui.adapter.*
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list) {

    private val mViewModel: GalleryListViewModel
            by createViewModelLazy(GalleryListViewModel::class, { viewModelStore })

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

    private val binding by viewBinding(FragmentGalleryListBinding::bind)

    private val searchActivityLaunch: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //结果处理
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            it.data?.getParcelableExtra<SearchKey>(DataKey.GALLERY_SEARCH_KEY)?.apply {
                mViewModel.galleryListPage(1)
                mViewModel.galleryListCondition(this)
                mAdapter.refresh()
            }
        }


    @Suppress("ControlFlowWithEmptyBody")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)

        mAdapter.clickEvent.observe(this, this::onDetailNavigation)
        lifecycleScope.launchWhenCreated {
            mAdapter.loadStateFlow.collectLatest {
                if (mInitAdapter.isOver) {
                    binding?.galleryListRefresh?.isRefreshing = it.refresh is LoadState.Loading
                } else {
                    mInitAdapter.loadState = it.refresh
                }
                binding?.galleryListRefresh?.isEnabled = it.prepend.endOfPaginationReached
            }
        }

        mViewModel.galleryList.observe(this, { mAdapter.submitData(lifecycle, it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        mViewModel.searchBarText.observe(viewLifecycleOwner, {
            binding?.topBar?.topSearchText?.text = it
        })

        mViewModel.searchBarHint.observe(viewLifecycleOwner, {
            binding?.topBar?.topSearchText?.hint = it
        })

        binding?.galleryList?.apply {
            setPadding(0, paddingTop + requireActivity().statusBarHeight(), 0, 0)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mConcatAdapter
            addOnScrollListener(ListScrollTrigger(this) {
                if (it) binding?.galleryMotionLayout?.transitionToStart()
                else binding?.galleryMotionLayout?.transitionToEnd()
            })

            binding?.topBar?.topSearchLayout?.apply {
                addOnScrollListener(ListFloatHeader(this))
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
                        putExtra(DataKey.GALLERY_SEARCH_KEY, mViewModel.searchKey)
                    }, options)
                }
            }
        }

        binding?.galleryGoTop?.setOnClickListener {
            binding?.galleryList?.smoothScrollToPosition(0)
        }

        binding?.galleryPageJump?.setOnClickListener { showPageJumpDialog() }

        binding?.galleryListRefresh?.apply {
            setProgressViewOffset(false, dp2px(8f).toInt(), dp2px(120f).toInt())
            setOnRefreshListener { mAdapter.refresh() }
        }
    }

    private fun onDetailNavigation(galleryClick: GalleryAdapter.GalleryClick) {
        with(galleryClick) {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(
                    R.id.action_gallery_list_fragment_to_gallery_detail_fragment,
                    bundleOf(
                        DataKey.GALLERY_INFO to data,
                        DataKey.IMAGE_CACHE_KEY to cacheKey
                    ),
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