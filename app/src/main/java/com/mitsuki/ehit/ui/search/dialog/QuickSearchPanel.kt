package com.mitsuki.ehit.ui.search.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.Log
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.DialogQuickSearchBinding
import com.mitsuki.ehit.ui.common.dialog.BottomDialogFragment
import com.mitsuki.ehit.ui.search.QuickSearchItemTouchHelperCallback
import com.mitsuki.ehit.ui.search.adapter.QuickSearchAdapter
import com.mitsuki.ehit.viewmodel.QuickSearchViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuickSearchPanel : BottomDialogFragment(R.layout.dialog_quick_search) {

    private val mViewModel: QuickSearchViewModel by viewModels()

    private val binding by viewBinding(DialogQuickSearchBinding::bind)

    private val mAdapter by lazy { QuickSearchAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData(arguments)
        lifecycleScope.launchWhenCreated {
            mAdapter.submitData(mViewModel.quickSearch())
        }

        mAdapter.clickItem.observe(this, {
            when (it) {
                is QuickSearchAdapter.Event.Click -> {

                    dismiss()
                }
                is QuickSearchAdapter.Event.Delete ->
                    mViewModel.delSearch(it.data.key, it.data.type)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        requireDialog().setCanceledOnTouchOutside(true)

        binding?.quickSearchAdd?.setOnClickListener {
            mViewModel.saveSearch(mViewModel.key)
        }

        val touchCallBack = QuickSearchItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(touchCallBack)

        touchCallBack.swapEvent.observe(viewLifecycleOwner, {
            mAdapter.onItemMove(it.first, it.second)
        })

        touchCallBack.dataSwap.observe(viewLifecycleOwner, {
            mViewModel.swapQuickItem(mAdapter.newSortData)
        })

        mAdapter.sortDragTrigger.observe(viewLifecycleOwner) {
            itemTouchHelper.startDrag(it)
        }

        binding?.quickSearchTarget?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }
}