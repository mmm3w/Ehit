package com.mitsuki.ehit.mvvm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.ehit.R

object OperatingAdapter : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_operating, parent, false)
        )
    }

    override fun getItemCount(): Int = 7

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text(position)
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val view = view<TextView>(R.id.operating)

    fun text(position: Int) {
        when (position) {
            0 -> view?.text = "heart"
            1 -> view?.text = "rate"
            2 -> view?.text = "share"
            3 -> view?.text = "torrent"
            4 -> view?.text = "archive"
            5 -> view?.text = "similar"
            6 -> view?.text = "search_cover"
        }
    }
}