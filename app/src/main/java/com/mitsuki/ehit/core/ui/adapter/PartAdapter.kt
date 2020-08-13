package com.mitsuki.ehit.core.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.DetailPart

class PartAdapter(val itemEventObservable: MutableLiveData<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var part = DetailPart(0f, 0, 0)

    val divider = object : RecyclerView.ItemDecoration() {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xffDADCE0.toInt()
            style = Paint.Style.FILL
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.getChildAdapterPosition(view) != itemCount - 1)
                outRect.set(0, 0, dp2px(1f), 0)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            for (i in 0 until parent.childCount) {
                parent.getChildAt(i)?.apply {
                    val center = (bottom + top) / 2
                    c.drawRect(
                        Rect(
                            left,
                            center - dp2px(16f),
                            left + dp2px(1f),
                            center + dp2px(16f)
                        ), paint
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_operating, parent, false)
        ) {}
    }

    override fun getItemCount(): Int = 4

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(TextView(context).apply {
                        text = String.format("%.1f", part.rating)
                        textSize = 18f
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setPadding(0, 0, dp2px(4f), 0)
                    })
                    addView(ImageView(context).apply { setImageResource(R.drawable.ic_baseline_star_20) })
                }
                holder.view<TextView>(R.id.operatingText)?.text = "${part.ratingCount}条评价"
            }
            1 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(TextView(context).apply {
                        text = part.page.toString()
                        textSize = 18f
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    })
                }
                holder.view<TextView>(R.id.operatingText)?.text = "页"
            }
            2 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(ImageView(context).apply { setImageResource(R.drawable.ic_baseline_find_in_page_24) })
                }
                holder.view<TextView>(R.id.operatingText)?.text = "相似画廊"
            }
            3 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(ImageView(context).apply { setImageResource(R.drawable.ic_baseline_description_24) })
                }
                holder.view<TextView>(R.id.operatingText)?.text = "更多详情"
            }
        }
    }

}

