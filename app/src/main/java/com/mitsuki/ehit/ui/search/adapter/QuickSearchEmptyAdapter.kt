package com.mitsuki.ehit.ui.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.empty.EmptyAdapter
import com.mitsuki.armory.adapter.empty.EmptyState
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.createItemView

class QuickSearchEmptyAdapter : EmptyAdapter<QuickSearchEmptyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, emptyState: EmptyState): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, emptyState: EmptyState) {
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.dialog_quick_search_empty))
}