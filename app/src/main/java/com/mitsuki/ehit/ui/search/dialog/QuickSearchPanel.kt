package com.mitsuki.ehit.ui.search.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.isClick
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.observeWithCoro
import com.mitsuki.ehit.databinding.DialogQuickSearchBinding
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.ui.common.dialog.BindingBottomDialogFragment
import com.mitsuki.ehit.ui.main.QuickSearchNameInputDialog
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

    private val mAdapter by lazy { QuickSearchAdapter(mViewModel.data) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        requireDialog().setCanceledOnTouchOutside(true)

        binding?.quickSearchAdd?.setOnClickListener { showQuickNameInputDialog() }

        val touchCallBack = QuickSearchItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(touchCallBack)

        mAdapter.receiver<QuickSearch>("delete")
            .isClick()
            .observe(viewLifecycleOwner) { mViewModel.remove(it) }

        mAdapter.receiver<QuickSearch>("click")
            .isClick()
            .observe(viewLifecycleOwner) {
                onQuickSearch(it.type, it.key)
                dismiss()
            }
        mAdapter.receiver<QuickSearchAdapter.ViewHolder>("sort").observe(viewLifecycleOwner) {
            itemTouchHelper.startDrag(it)
        }

        touchCallBack.receiver<Pair<Int, Int>>("move").observe(viewLifecycleOwner){
            mViewModel.move(it.first, it.second)
        }
        touchCallBack.receiver<Pair<Int, Int>>("event").observe(viewLifecycleOwner){
            mViewModel.resort()
        }

        binding?.quickSearchTarget?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            itemTouchHelper.attachToRecyclerView(this)
            adapter
        }
    }

    private fun showQuickNameInputDialog() {
//        mParentViewModel.currentDataMeta.apply {
//            val type: GalleryDataMeta.Type
//            val k: String
//            when (this) {
//                is GalleryDataMeta.Normal -> {
//                    type = GalleryDataMeta.Type.NORMAL
//                    k = key?.key ?: ""
//                }
//                is GalleryDataMeta.Uploader -> {
//                    type = GalleryDataMeta.Type.UPLOADER
//                    k = name
//                }
//                is GalleryDataMeta.Tag -> {
//                    type = GalleryDataMeta.Type.TAG
//                    k = tag
//                }
//                is GalleryDataMeta.Subscription -> {
//                    type = GalleryDataMeta.Type.SUBSCRIPTION
//                    k = key?.key ?: ""
//                }
//                is GalleryDataMeta.Popular -> {
//                    type = GalleryDataMeta.Type.WHATS_HOT
//                    k = ""
//                }
//            }
//
//            QuickSearchNameInputDialog(hint) { mViewModel.add(it, k, type) }
//                .show(childFragmentManager, "quick_search_name")
//        }
    }

}