package com.mitsuki.ehit.ui.history.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.isClick
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.databinding.FragmentHistoryBinding
import com.mitsuki.ehit.ui.history.adapter.HistoryAdapter
import com.mitsuki.ehit.ui.main.adapter.GalleryClick
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import com.mitsuki.ehit.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : BindingFragment<FragmentHistoryBinding>(
    R.layout.fragment_history, FragmentHistoryBinding::bind
) {

    private val mViewModel: HistoryViewModel
            by createViewModelLazy(HistoryViewModel::class, { viewModelStore })

    private val mAdapter by lazy { HistoryAdapter(mViewModel.data) }

    @Inject
    lateinit var shareData: ShareData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.load()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }


        mAdapter.isPageShow = shareData.spShowPage
        mAdapter.receiver<GalleryClick>("click")
            .isClick()
            .observe(viewLifecycleOwner, ::onDetailNavigation)

        binding?.topBar?.topBarBack?.setOnClickListener { requireActivity().onBackPressed() }
        binding?.topBar?.topBarText?.text = text(R.string.text_menu_history)
        binding?.topBar?.topBarLayout?.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                setMargins(
                    leftMargin,
                    topMargin + requireActivity().statusBarHeight(),
                    rightMargin,
                    bottomMargin
                )
            }
        }
        binding?.historyTarget?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }


    private fun onDetailNavigation(galleryClick: GalleryClick) {
        with(galleryClick) {
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.action_history_fragment_to_gallery_detail_fragment,
                    bundleOf(DataKey.GALLERY_INFO to data),
                    null,
                    FragmentNavigatorExtras(target to data.itemTransitionName)
                )
        }
    }

}