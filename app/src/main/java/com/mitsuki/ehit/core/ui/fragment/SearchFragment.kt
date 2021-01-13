package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.core.ui.adapter.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(R.layout.fragment_search) {

    private val mSwitch by lazy { SearchSwitch() }
    private val mHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val mShortcutAdapter by lazy { SearchShortcutAdapter() }
    private val mCategoryAdapter by lazy { SearchCategoryAdapter() }
    private val mAdvancedAdapter by lazy { SearchAdvancedAdapter() }
    private val mExtendMore by lazy { SearchExpandMore() }

    private val mAdapter by lazy {
        ConcatAdapter(
            mCategoryAdapter,
            mAdvancedAdapter,
            mExtendMore,
            mSwitch,
            mHistoryAdapter,
            mShortcutAdapter
        )
    }

    private val mSpanSize = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (mAdapter.getItemViewType(position) == 4 && mCategoryAdapter.isEnable) {
                1
            } else {
                2
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.main_nav_fragment
            duration = resources.getInteger(R.integer.fragment_transition_motion_duration).toLong()
            //TODO:颜色值替换
            setAllContainerColors(0xffffffff.toInt())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        search_list?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = mSpanSize
            }
            adapter = mAdapter

        }

        search_back?.setOnClickListener { requireActivity().onBackPressed() }

        mSwitch.switchEvent.observe(viewLifecycleOwner, Observer(this::onSwitch))
        mExtendMore.expendEvent.observe(
            viewLifecycleOwner,
            Observer { mAdvancedAdapter.isVisible = it })
    }


    private fun onSwitch(isAdvancedMode: Boolean) {
        mHistoryAdapter.isEnable = !isAdvancedMode
        mShortcutAdapter.isEnable = !isAdvancedMode
        mCategoryAdapter.isEnable = isAdvancedMode
        mExtendMore.isEnable = isAdvancedMode
        mAdvancedAdapter.isEnable = isAdvancedMode
    }
}