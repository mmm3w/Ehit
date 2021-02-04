package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.mitsuki.armory.extend.hideSoftInput
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.being.db.RoomData
import com.mitsuki.ehit.core.model.entity.SearchKey
import com.mitsuki.ehit.core.ui.adapter.*
import com.mitsuki.ehit.core.viewmodel.GalleryListViewModel
import com.mitsuki.ehit.core.viewmodel.MainViewModel
import com.mitsuki.ehit.core.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search) {

    private val mViewModel: SearchViewModel
            by createViewModelLazy(SearchViewModel::class, { viewModelStore })

    private val mMainViewModel: MainViewModel
            by createViewModelLazy(MainViewModel::class, { requireActivity().viewModelStore })

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
            if (mCategoryAdapter.isEnable) {
                return if (position < mCategoryAdapter.itemCount) 1 else 2
            }
            return 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
        mHistoryAdapter.itemClickEvent.observe(this, Observer(this::onSearchEvent))
        mShortcutAdapter.itemClickEvent.observe(this, Observer(this::onSearchEvent))
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

        search_start?.setOnClickListener {
            search_input?.text?.toString()?.apply { onSearchEvent(this) }
        }
        search_back?.setOnClickListener { back() }
        search_input?.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    //这个时候应该要返回首页
                    v.clearFocus()
                    onSearchEvent(v.text.toString())
                    true
                }
                else -> false
            }
        }

        mSwitch.switchEvent.observe(viewLifecycleOwner, Observer(this::onSwitch))

        mExtendMore.expendEvent.observe(
            viewLifecycleOwner, { mAdvancedAdapter.isVisible = it })

        mViewModel.tempKey?.apply { onSearchUpdate(this) }

        lifecycle.coroutineScope.launch {
            mViewModel.searchHistory().collect { mHistoryAdapter.submitData(it) }
            mViewModel.quickSearch().collect { mShortcutAdapter.submitData(it) }
        }
    }


    private fun onSearchEvent(text: String) {
        lifecycle.coroutineScope.launch { mViewModel.saveSearch(text) }
        mMainViewModel.postSearchKey(mViewModel.code, obtainSearchKey(text))
        back()
    }

    private fun onSwitch(isAdvancedMode: Boolean) {
        mHistoryAdapter.isEnable = !isAdvancedMode
        mShortcutAdapter.isEnable = !isAdvancedMode
        mCategoryAdapter.isEnable = isAdvancedMode
        mExtendMore.isEnable = isAdvancedMode
        mAdvancedAdapter.isEnable = isAdvancedMode
    }

    private fun onSearchUpdate(key: SearchKey) {
        search_input?.apply {
            setText(key.showContent)
            setSelection(key.showContent.length)
        }

        mCategoryAdapter.submitData(key.category)
    }

    private fun obtainSearchKey(text: String): SearchKey {
        return SearchKey(text, mCategoryAdapter.categoryCode())
    }

    private fun back() {
        hideSoftInput()
        requireActivity().onBackPressed()
    }
}