package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
                    notifyItemRangeInserted(0, Category.CATEGORY_SET.size - 1)
                } else if (!value && field) {
                    notifyItemRangeRemoved(0, Category.CATEGORY_SET.size - 1)
                }
                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (isEnable) Category.CATEGORY_SET.size - 1 else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view<CategoryView>(R.id.search_category_content)?.apply {
            val str = Category.CATEGORY_SET[position]
            setCategoryColor(Category.getColor(str))
            text = str
        }
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search_category, parent, false)
    )
}