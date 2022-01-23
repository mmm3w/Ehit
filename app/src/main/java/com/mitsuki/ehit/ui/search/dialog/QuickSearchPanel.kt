package com.mitsuki.ehit.ui.search.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.armory.base.extend.toast
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.crutch.extend.observeWithCoro
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.DialogQuickSearchBinding
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.ui.common.dialog.BottomDialogFragment
import com.mitsuki.ehit.ui.search.QuickSearchItemTouchHelperCallback
import com.mitsuki.ehit.ui.search.adapter.QuickSearchAdapter
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import com.mitsuki.ehit.viewmodel.QuickSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuickSearchPanel : BottomDialogFragment(R.layout.dialog_quick_search) {

    private val mViewModel: QuickSearchViewModel by viewModels({ requireActivity() })

    private val mParentViewModel: GalleryListViewModel
            by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(DialogQuickSearchBinding::bind)

    private val mAdapter by lazy { QuickSearchAdapter() }

    var onQuickSearch: ((GalleryPageSource) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mAdapter.submitData(mViewModel.quickSearch())
        }

        mAdapter.clickItem.observeWithCoro(this) {
            when (it) {
                is QuickSearchAdapter.Event.Click -> {
                    val data = when (it.data.type) {
                        GalleryPageSource.Type.NORMAL -> GalleryPageSource.Normal(it.data.key)
                        GalleryPageSource.Type.UPLOADER -> GalleryPageSource.Uploader(it.data.key)
                        GalleryPageSource.Type.TAG -> GalleryPageSource.Tag(it.data.key)
                        GalleryPageSource.Type.SUBSCRIPTION -> GalleryPageSource.Subscription(it.data.key)
                        GalleryPageSource.Type.WHATS_HOT -> GalleryPageSource.POPULAR
                        else -> null
                    }
                    data?.apply { onQuickSearch?.invoke(this) }
                    dismiss()
                }
                is QuickSearchAdapter.Event.Delete -> {
                    mAdapter.removeItem(it.data)
                    mViewModel.delSearch(it.data.key, it.data.type)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        requireDialog().setCanceledOnTouchOutside(true)

        binding?.quickSearchAdd?.setOnClickListener {
            mParentViewModel.pageSource.apply {
                lifecycleScope.launch {
                    if (mViewModel.isQuickSave(cacheKey, type)) {
                        toast("项目已经存在")
                    } else {
                        mViewModel.saveSearch(cacheKey, cacheKey, type)
                        mAdapter.addItem(cacheKey, cacheKey, type)
                    }
                }
            }
        }

        val touchCallBack = QuickSearchItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(touchCallBack)

        //涉及数据交换，无法直接使用封装好的队列更新数据类
        touchCallBack.swapEvent.observeWithCoro(viewLifecycleOwner, {
            mAdapter.onItemMove(it.first, it.second)
        })
        touchCallBack.dataSwap.observeWithCoro(viewLifecycleOwner, {
            mViewModel.swapQuickItem(mAdapter.newSortData)
        })
        //内部View的事件带出来
        mAdapter.sortDragTrigger.observe(viewLifecycleOwner) {
            itemTouchHelper.startDrag(it)
        }
        //配置适配器以及绑定相关手势
        binding?.quickSearchTarget?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            itemTouchHelper.attachToRecyclerView(this)
            adapter
        }
    }

}