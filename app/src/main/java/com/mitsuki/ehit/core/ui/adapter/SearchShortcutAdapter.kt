package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R

class SearchShortcutAdapter : RecyclerView.Adapter<SearchShortcutAdapter.ViewHolder>() {

    var isEnable: Boolean = true
        set(value) {
            if (value != field) {
                if (value && !field) {
                    notifyItemRangeInserted(0, 5)
                } else if (!value && field) {
                    notifyItemRangeRemoved(0, 5)
                }
                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (isEnable) 5 else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        ) {

        private val mSearchIcon = view<ImageView>(R.id.search_item_icon)
        private val mSearchText = view<TextView>(R.id.search_item_text)


        fun bind(index: Int) {
            mSearchIcon?.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
            mSearchText?.text = "这是第{$index}条快捷搜索"
        }
    }
}