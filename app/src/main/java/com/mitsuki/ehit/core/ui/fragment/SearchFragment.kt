package com.mitsuki.ehit.core.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scaleMatrix
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFade
import com.mitsuki.armory.extend.paddingStatusBarHeight
import com.mitsuki.armory.extend.themeColor
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.core.ui.adapter.SearchCategoryAdapter
import com.mitsuki.ehit.core.ui.adapter.SearchExpandMore
import com.mitsuki.ehit.core.ui.adapter.SearchHistoryAdapter
import com.mitsuki.ehit.core.ui.adapter.SearchSwitch
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(R.layout.fragment_search) {


    private val mSwitch by lazy { SearchSwitch() }
    private val mHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val mCategoryAdapter by lazy { SearchCategoryAdapter() }
    private val mExtendMore by lazy { SearchExpandMore() }
    private val mAdapter by lazy {
        ConcatAdapter(mSwitch, mHistoryAdapter, mCategoryAdapter, mExtendMore)
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
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        search_back?.setOnClickListener { requireActivity().onBackPressed() }
    }
}