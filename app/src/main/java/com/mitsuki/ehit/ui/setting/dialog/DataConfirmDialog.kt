package com.mitsuki.ehit.ui.setting.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.GeneralCheckBoxBinding
import com.mitsuki.ehit.databinding.GeneralRecyclerViewBinding
import com.mitsuki.ehit.ui.common.adapter.BindingViewHolder
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment

class DataConfirmDialog(
    private val dl: IntArray = intArrayOf(DATA_COOKIE, DATA_QUICK_SEARCH),
    private val action: (IntArray) -> Unit,
) :
    BindingDialogFragment<GeneralRecyclerViewBinding>(
        R.layout.general_recycler_view,
        GeneralRecyclerViewBinding::bind
    ) {
    companion object {
        const val DATA_COOKIE = 0
        const val DATA_QUICK_SEARCH = 1
    }

    init {
        title(res = R.string.text_export_data)
        positiveButton(res = R.string.text_confirm) {
            (binding?.listUi?.adapter as? MyAdapter)?.obtainSelected()?.apply {
                action(this)
                it.dismiss()
            }
        }
        negativeButton(res = R.string.text_cancel) { it.dismiss() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.listUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MyAdapter(dl.map { it to false }.toMutableList())
        }
    }

    private class MyAdapter(private val mData: MutableList<Pair<Int, Boolean>>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent).apply {
                (itemView as CheckBox).apply {
                    setPadding(0, dp2px(12f).toInt(), 0, dp2px(12f).toInt())
                    setOnCheckedChangeListener { _, isChecked ->
                        val mLast = mData[bindingAdapterPosition]
                        if (mLast.second != isChecked) {
                            mData.removeAt(bindingAdapterPosition)
                            mData.add(bindingAdapterPosition, mLast.first to isChecked)
                            notifyItemChanged(bindingAdapterPosition)
                        }
                    }
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(mData[position]) {
                when (first) {
                    DATA_COOKIE ->
                        (holder.itemView as CheckBox).apply {
                            text = text(R.string.text_setting_cookie)
                            isChecked = second
                        }
                    DATA_QUICK_SEARCH -> (holder.itemView as CheckBox).apply {
                        text = text(R.string.text_quick_search)
                        isChecked = second
                    }
                    else -> {}
                }
            }
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        class ViewHolder(parent: ViewGroup) : BindingViewHolder<GeneralCheckBoxBinding>(
            parent,
            R.layout.general_check_box,
            GeneralCheckBoxBinding::bind
        )

        fun obtainSelected(): IntArray {
            return mData.filter { it.second }.map { it.first }.toIntArray()
        }

    }

}