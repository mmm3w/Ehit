package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetailTopBarPlugin(context: Context, root: ViewGroup? = null) :
    RecyclerView.OnScrollListener() {


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//        (recyclerView.layoutManager as GridLayoutManager).spanSizeLookup.getSpanSize()
    }
}