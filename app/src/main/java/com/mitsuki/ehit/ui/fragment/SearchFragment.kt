package com.mitsuki.ehit.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.mitsuki.armory.extend.hideSoftInput
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.model.ehparser.GalleryRating
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.ui.adapter.*
import com.mitsuki.ehit.viewmodel.MainViewModel
import com.mitsuki.ehit.viewmodel.SearchViewModel
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

    private val mModeSwitch by lazy { SearchModeSwitch() }
    private val mHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val mShortcutAdapter by lazy { SearchShortcutAdapter() }
    private val mCategoryAdapter by lazy { SearchCategoryAdapter() }
    private val mAdvancedAdapter by lazy { SearchAdvancedAdapter() }
    private val mAdvancedSwitch by lazy { SearchAdvancedOptionsSwitch() }

    private val mAdapter by lazy {
        ConcatAdapter(
            mCategoryAdapter,
            mAdvancedSwitch,
            mAdvancedAdapter,
            mModeSwitch,
            mHistoryAdapter,
            mShortcutAdapter
        )
    }

    private val mSpanSize = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            var newPosition = position

            if (newPosition < mCategoryAdapter.itemCount) return 1
            newPosition -= mCategoryAdapter.itemCount

            return 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
        mHistoryAdapter.itemClickEvent.observe(this, Observer(this::onSearchEvent))
        mShortcutAdapter.itemClickEvent.observe(this, Observer(this::onSearchEvent))
        mAdvancedAdapter.ratingEvent.observe(this, Observer(this::onRatingEvent))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }

        mViewModel.tempKey?.apply { onSearchUpdate(this) }

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

        mModeSwitch.switchEvent.observe(viewLifecycleOwner, Observer(this::onSwitch))

        mAdvancedSwitch.switchEvent.observe(
            viewLifecycleOwner, Observer { mAdvancedAdapter.isVisible = it })

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

    private fun onRatingEvent(nil: String) {
        MaterialDialog(requireContext()).show {
            listItems(items = GalleryRating.strList(requireContext())) { _, index, _ ->
                mAdvancedAdapter.applyRating(GalleryRating.DATA[index])
            }
            lifecycleOwner(this@SearchFragment)
        }
    }

    private fun onSwitch(isAdvancedMode: Boolean) {
        mHistoryAdapter.isEnable = !isAdvancedMode
        mShortcutAdapter.isEnable = !isAdvancedMode
        mCategoryAdapter.isEnable = isAdvancedMode
        mAdvancedSwitch.isEnable = isAdvancedMode
        mAdvancedAdapter.isEnable = isAdvancedMode
    }

    private fun onSearchUpdate(key: SearchKey) {
        search_input?.apply {
            if (key.key.isNotEmpty()) {
                setText(key.key)
                setSelection(key.key.length)
            }
        }
        mAdvancedSwitch.isChecked = key.isAdvancedEnable
        mCategoryAdapter.submitData(key.category)
        mAdvancedAdapter.submitData(key)
    }

    private fun obtainSearchKey(text: String): SearchKey {
        return SearchKey(text, mCategoryAdapter.categoryCode()).apply {
            isAdvancedEnable = mAdvancedSwitch.isChecked
            if (isAdvancedEnable)
                mAdvancedAdapter.getOptions(this)
        }
    }

    private fun back() {
        hideSoftInput()
        requireActivity().onBackPressed()
    }
}