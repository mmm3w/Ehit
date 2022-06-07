package com.mitsuki.ehit.ui.search.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.observeWithCoro
import com.mitsuki.ehit.databinding.DialogQuickSearchBinding
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.ui.common.dialog.BindingBottomDialogFragment
import com.mitsuki.ehit.ui.search.QuickSearchItemTouchHelperCallback
import com.mitsuki.ehit.ui.search.adapter.QuickSearchAdapter
import com.mitsuki.ehit.viewmodel.GalleryListViewModel
import com.mitsuki.ehit.viewmodel.QuickSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class QuickSearchPanel(val onQuickSearch: ((GalleryDataMeta.Type, String) -> Unit)) :
    BindingBottomDialogFragment<DialogQuickSearchBinding>(
        R.layout.dialog_quick_search,
        DialogQuickSearchBinding::bind
    ) {

    private val mViewModel: QuickSearchViewModel by viewModels({ requireActivity() })

    private val mParentViewModel: GalleryListViewModel
            by viewModels(ownerProducer = { requireParentFragment() })

    private val mAdapter by lazy { QuickSearchAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mAdapter.submitData(mViewModel.quickSearch())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        requireDialog().setCanceledOnTouchOutside(true)

        mAdapter.receiver<QuickSearch>("delete").observe(viewLifecycleOwner) {
            runBlocking {
                mAdapter.removeItem(it)
                mViewModel.delSearch(it.key, it.type)
            }
        }

        mAdapter.receiver<QuickSearch>("click").observe(viewLifecycleOwner) {
            onQuickSearch(it.type, it.key)
            dismiss()
        }


        binding?.quickSearchAdd?.setOnClickListener {
//            mParentViewModel.pageSource.apply {
//                lifecycleScope.launch {
//                    if (!mViewModel.isQuickSave(cacheKey, type)) {
//                        mViewModel.saveSearch(cacheKey, cacheKey, type)
//                        mAdapter.addItem(cacheKey, cacheKey, type)
//                    }
//                }
//            }
        }

        val touchCallBack = QuickSearchItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(touchCallBack)

        //涉及数据交换，无法直接使用封装好的队列更新数据类
        touchCallBack.swapEvent.observeWithCoro(viewLifecycleOwner) {
            mAdapter.onItemMove(it.first, it.second)
        }
        touchCallBack.dataSwap.observeWithCoro(viewLifecycleOwner) {
            mViewModel.swapQuickItem(mAdapter.newSortData)
        }
        //内部View的事件带出来
        mAdapter.receiver<QuickSearchAdapter.ViewHolder>("sort").observe(viewLifecycleOwner) {
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