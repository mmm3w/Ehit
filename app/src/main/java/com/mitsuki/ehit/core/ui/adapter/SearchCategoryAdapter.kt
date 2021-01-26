package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.ehparser.Category
import com.mitsuki.ehit.core.ui.widget.CategoryView

class SearchCategoryAdapter : RecyclerView.Adapter<SearchCategoryAdapter.ViewHolder>() {

    var isEnable: Boolean = false
        set(value) {
            if (value != field) {
                if (value && !field) {
                    notifyItemRangeInserted(0, Category.DATA.size - 1)
                } else if (!value && field) {
                    notifyItemRangeRemoved(0, Category.DATA.size - 1)
                }
                field = value
            }
        }

    private val checkState = BooleanArray(Category.DATA.size) { true }

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        checkState[holder.bindingAdapterPosition] = !checkState[holder.bindingAdapterPosition]
        notifyItemChanged(holder.bindingAdapterPosition)
    }

    private val mItemLongClick = { view: View ->
        val currentIndex = (view.tag as ViewHolder).bindingAdapterPosition
        for (index in checkState.indices) {
            if (index != currentIndex) {
                checkState[index] = !checkState[currentIndex]
            }
        }
        notifyDataSetChanged()
        true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
            itemView.setOnLongClickListener(mItemLongClick)
        }
    }

    override fun getItemCount(): Int {
        return if (isEnable) Category.DATA.size - 1 else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view<CategoryView>(R.id.search_category_content)?.apply {
            setCategoryColor(Category.color(position))
            text = Category.text(position)
        }

        holder.view<View>(R.id.search_category_mask)?.isVisible = !checkState[position]
    }

    fun categoryCode(): Int {
        var code = 0
        for ((index, value) in checkState.withIndex()) {
            if (value) code = code or Category.DATA[index].code
        }
        return code
    }

    fun submitData(code: Int) {
        for (index in checkState.indices) {
            checkState[index] = (code and Category.DATA[index].code) == Category.DATA[index].code
        }

        if (isEnable) {
            notifyDataSetChanged()
        }
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search_category, parent, false)
    )
}