package com.mitsuki.ehit.ui.search

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.armory.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivitySearchBinding
import com.mitsuki.ehit.model.ehparser.GalleryRating
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.ui.temp.adapter.*
import com.mitsuki.ehit.ui.search.adapter.*
import com.mitsuki.ehit.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private val mViewModel: SearchViewModel by viewModels()

    private val mModeSwitch by lazy { SearchModeSwitch() }
    private val mHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val mCategoryAdapter by lazy { SearchCategoryAdapter() }
    private val mAdvancedAdapter by lazy { SearchAdvancedAdapter() }
    private val mAdvancedSwitch by lazy { SearchAdvancedOptionsSwitch() }

    private val mAdapter by lazy {
        ConcatAdapter(
            mCategoryAdapter,
            mAdvancedSwitch,
            mAdvancedAdapter,
            mModeSwitch,
            mHistoryAdapter
        )
    }

    private val binding by viewBinding(ActivitySearchBinding::inflate)

    private val mSpanSize = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            var newPosition = position

            if (newPosition < mCategoryAdapter.itemCount) return 1
            newPosition -= mCategoryAdapter.itemCount

            return 2
        }
    }

    private val controller by windowController()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        ViewCompat.setTransitionName(
            findViewById(android.R.id.content),
            string(R.string.transition_name_gallery_list_toolbar)
        )
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            startContainerColor = Color.WHITE
            startContainerColor = Color.WHITE
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
        }

        super.onCreate(savedInstanceState)
        controller.window(navigationBarLight = true, statusBarLight = true, barFit = false)
        findViewById<View>(android.R.id.content).setPadding(0, statusBarHeight(), 0, 0)

        mViewModel.initData(intent)

        mHistoryAdapter.clickItem.observe(this, this::onItemEvent)
        mAdvancedAdapter.ratingEvent.observe(this, Observer(this::onRatingEvent))

        mViewModel.searchKey.apply { onSearchUpdate(this) }

        binding.searchList.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2).apply {
                spanSizeLookup = mSpanSize
            }
            adapter = mAdapter
        }

        binding.searchStart.setOnClickListener {
            binding.searchInput.text?.toString()?.apply { onSearchEvent(this) }
        }
        binding.searchBack.setOnClickListener { finish() }
        binding.searchInput.setOnEditorActionListener { v, actionId, _ ->
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

        mModeSwitch.switchEvent.observe(this, Observer(this::onSwitch))

        mAdvancedSwitch.switchEvent.observe(this, { mAdvancedAdapter.isVisible = it })

        lifecycleScope.launchWhenCreated {
            mViewModel.searchHistory().collect { mHistoryAdapter.submitData(it) }
        }
    }

    private fun onItemEvent(event: SearchWordEvent) {
        lifecycleScope.launch {
            when (event) {
                is SearchWordEvent.Select -> onSearchEvent(event.data.text)
                is SearchWordEvent.Delete -> mViewModel.delSearch(event.data)
            }
        }
    }

    private fun onSearchEvent(text: String) {
        lifecycleScope.launch { mViewModel.saveSearch(text) }
        finishWithResult(text)
    }

    private fun onRatingEvent(nil: String) {
        MaterialDialog(this).show {
            listItems(items = GalleryRating.strList(this@SearchActivity)) { _, index, _ ->
                mAdvancedAdapter.applyRating(GalleryRating.DATA[index])
            }
            lifecycleOwner(this@SearchActivity)
        }
    }

    private fun onSwitch(isAdvancedMode: Boolean) {
        mHistoryAdapter.isEnable = !isAdvancedMode
        mCategoryAdapter.isEnable = isAdvancedMode
        mAdvancedSwitch.isEnable = isAdvancedMode
        mAdvancedAdapter.isEnable = isAdvancedMode
    }

    private fun onSearchUpdate(key: SearchKey) {
        binding.searchInput.apply {
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

    private fun finishWithResult(text: String) {
        setResult(Activity.RESULT_OK, Intent().apply {
            val searchKey = obtainSearchKey(text)
            putExtra(DataKey.GALLERY_PAGE_SOURCE, mViewModel.buildNewSource(searchKey))
        })
        finish()
    }

}

sealed class SearchWordEvent(val data: SearchHistory) {
    class Select(data: SearchHistory) : SearchWordEvent(data)
    class Delete(data: SearchHistory) : SearchWordEvent(data)
}