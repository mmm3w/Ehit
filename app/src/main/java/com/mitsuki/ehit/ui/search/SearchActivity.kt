package com.mitsuki.ehit.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.mitsuki.armory.base.extend.statusBarHeight
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.network.site.ApiContainer
import com.mitsuki.ehit.crutch.network.site.EhSite
import com.mitsuki.ehit.databinding.ActivitySearchBinding
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.ui.search.adapter.*
import com.mitsuki.ehit.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private val mViewModel: SearchViewModel by viewModels()

    private val mModeSwitch by lazy { SearchModeSwitch() }
    private val mHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val mCategoryAdapter by lazy { SearchCategoryAdapter() }
    private val mAdvancedAdapter by lazy { SearchAdvancedAdapter() }
//    private val mAdvancedAdapter by lazy { if (ApiContainer.site is EhSite) SearchAdvancedAdapter() else SearchAdvancedExAdapter() }

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


    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        ViewCompat.setTransitionName(
            findViewById(android.R.id.content),
            string(R.string.transition_name_gallery_list_toolbar)
        )
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            startContainerColor = color(R.color.background_color_general)
            startContainerColor = color(R.color.background_color_general)
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
        }

        super.onCreate(savedInstanceState)
        findViewById<View>(android.R.id.content).setPadding(0, statusBarHeight(), 0, 0)

        mViewModel.initData(intent)

        mHistoryAdapter.receiver<SearchWordEvent>("his").observe(this, this::onItemEvent)
        mAdvancedAdapter.receiver<Int>("rating").observe(this) { onRatingEvent() }

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
        binding.searchBack.setOnClickListener { onBackPressed() }
        binding.searchClear.setOnClickListener { binding.searchInput.setText("") }
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

        mModeSwitch.receiver<Boolean>("switch").observe(this, ::onSwitch)

        mAdvancedSwitch.receiver<Boolean>("switch")
            .observe(this) { mAdvancedAdapter.isVisible = it }

        lifecycleScope.launchWhenCreated {
            mViewModel.searchHistory().collect { mHistoryAdapter.submitData(it) }
        }
    }

    override fun onUiMode(isNightMode: Boolean) {
        controller.window(
            navigationBarLight = !isNightMode,
            statusBarLight = !isNightMode,
            navigationBarColor = color(R.color.navigation_bar_color),
            statusBarColor = color(R.color.status_bar_color),
            barFit = false
        )
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
        lifecycleScope.launch { mViewModel.saveSearch(text.trim()) }
        finishWithResult(text.trim())
    }

    private fun onRatingEvent() {
        //TODO 补上逻辑
//        MaterialDialog(this).show {
//            listItems(items = GalleryRating.strList(this@SearchActivity)) { _, index, _ ->
//                mAdvancedAdapter.applyRating(GalleryRating.DATA[index])
//            }
//            lifecycleOwner(this@SearchActivity)
//        }
    }

    private fun onSwitch(isAdvancedMode: Boolean) {
        mHistoryAdapter.isEnable = !isAdvancedMode
        mCategoryAdapter.isEnable = isAdvancedMode
        mAdvancedSwitch.isEnable = isAdvancedMode
        mAdvancedAdapter.isEnable = isAdvancedMode
    }

    private fun onSearchUpdate(keyGallery: GalleryDataKey) {
        binding.searchInput.apply {
            if (keyGallery.key.isNotEmpty()) {
                setText(keyGallery.key)
                setSelection(keyGallery.key.length)
            }
        }
        mAdvancedSwitch.isChecked = keyGallery.isAdvancedEnable
        mCategoryAdapter.submitData(keyGallery.category)
        mAdvancedAdapter.submitData(keyGallery)
    }

    private fun obtainSearchKey(text: String): GalleryDataKey {
        return GalleryDataKey(text, mCategoryAdapter.categoryCode()).apply {
            isAdvancedEnable = mAdvancedSwitch.isChecked
            if (isAdvancedEnable)
                mAdvancedAdapter.getOptions(this)
        }
    }

    private fun finishWithResult(text: String) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(DataKey.GALLERY_SEARCH_KEY, obtainSearchKey(text))
        })
        finish()
    }

}

sealed class SearchWordEvent(val data: SearchHistory) {
    class Select(data: SearchHistory) : SearchWordEvent(data)
    class Delete(data: SearchHistory) : SearchWordEvent(data)
}