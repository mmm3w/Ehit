package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.transition.platform.MaterialElevationScale
import com.google.android.material.transition.platform.MaterialFade
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.mitsuki.armory.extend.toast
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.ui.adapter.DefaultLoadStateAdapter
import com.mitsuki.ehit.core.ui.adapter.GalleryAdapter
import com.mitsuki.ehit.core.ui.widget.CategoryView
import com.mitsuki.ehit.core.viewmodel.GalleryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list) {

    private val mViewModel: GalleryListViewModel
            by createViewModelLazy(GalleryListViewModel::class, { viewModelStore })

    private val mAdapter: GalleryAdapter by lazy { GalleryAdapter() }

    private val mConcatAdapter by lazy {
        mAdapter.withLoadStateHeaderAndFooter(
            header = DefaultLoadStateAdapter(mAdapter),
            footer = DefaultLoadStateAdapter(mAdapter)
        )
    }

    @Suppress("ControlFlowWithEmptyBody")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply { duration = 300 }
        reenterTransition  = MaterialElevationScale(true).apply { duration = 300 }

        mAdapter.currentItem.observe(this, Observer(this::toDetail))

        lifecycleScope.launchWhenCreated {
            mAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.Error -> toast("刷新失败")
                }

                when (it.append) {
                    is LoadState.Error -> toast("底部加载失败")
                }

                when (it.prepend) {
                    is LoadState.Error -> toast("顶部加载失败")
                }

                gallery_list?.isRefreshing = it.refresh is LoadState.Loading
                gallery_list?.endOfPrepend =
                    if (it.refresh is LoadState.Error) true
                    else it.prepend.endOfPaginationReached
            }
        }

        mViewModel.galleryList.observe(this, Observer { mAdapter.submitData(lifecycle, it) })

        securityCheck()
        installCheck()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }

        gallery_list?.apply {
            //配置recycleView
            recyclerView {
                layoutManager = LinearLayoutManager(activity)
                adapter = mConcatAdapter
            }

            setListener(
                refreshListener = { mAdapter.refresh() },
                extendControl = {
                    if (it) gallery_motion_layout?.transitionToStart()
                    else gallery_motion_layout?.transitionToEnd()
                }
            )
        }

        gallery_go_top?.setOnClickListener {
            gallery_list?.recyclerView()?.smoothScrollToPosition(0)
        }

        gallery_page_jump?.setOnClickListener {
            showPageJumpDialog()
        }
    }

    @Suppress("ConstantConditionIf")
    private fun securityCheck() {
        //包含应用锁定，导航向锁定界面
        if (false) Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(R.id.action_gallery_list_fragment_to_security_fragment)
    }

    @Suppress("ConstantConditionIf")
    private fun installCheck() {
        //首次安装，导航向引导
        if (false) Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
            .navigate(R.id.action_gallery_list_fragment_to_disclaimer_fragment)
    }

    private fun toDetail(galleryClick: GalleryAdapter.GalleryClick) {
        with(galleryClick) {
            val extras = FragmentNavigatorExtras(
                target.findViewById<ImageView>(R.id.gallery_thumb) to data.thumbTransitionName
            )

            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                .navigate(
                    R.id.action_gallery_list_fragment_to_gallery_detail_fragment,
                    bundleOf(DataKey.GALLERY_INFO to data),
                    null,
                    extras
                )
        }
    }

    private fun showPageJumpDialog() {
        MaterialDialog(requireContext()).show {
            input(inputType = InputType.TYPE_CLASS_NUMBER) { _, text ->
                //配置页码，刷新数据
                mViewModel.galleryListPage(text.toString().toIntOrNull() ?: 0)
                mAdapter.refresh()
            }
            title(R.string.title_page_go_to)
            positiveButton(R.string.text_confirm)
            lifecycleOwner(this@GalleryListFragment)
        }
    }
}