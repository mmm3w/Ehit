package com.mitsuki.ehit.ui.adapter.gallerydetail

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import io.reactivex.rxjava3.subjects.PublishSubject

class GalleryDetailOperatingPart(
    private val subject: PublishSubject<GalleryDetailOperatingBlock.Event>,
    var data: GalleryDetailWrap.DetailPart? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                outRect.set(0, 0, dp2px(1f).toInt(), 0)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            for (i in 0 until parent.childCount) {
                parent.getChildAt(i)?.apply {
                    val center = (bottom + top) / 2
                    c.drawRect(
                        Rect(
                            left,
                            center - dp2px(16f).toInt(),
                            left + dp2px(1f).toInt(),
                            center + dp2px(16f).toInt()
                        ), paint
                    )
                }
            }
        }
    }

    private val mItemClick = { view: View ->
        val holder = view.tag as RecyclerView.ViewHolder
        when (holder.bindingAdapterPosition) {
            0 -> subject.onNext(GalleryDetailOperatingBlock.Event.Score)
            2 -> subject.onNext(GalleryDetailOperatingBlock.Event.SimilaritySearch)
            3 -> subject.onNext(GalleryDetailOperatingBlock.Event.MoreInfo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :
            RecyclerView.ViewHolder(parent.createItemView(R.layout.item_operating)) {}.apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(TextView(context).apply {
                        text = String.format(
                            "%.1f",
                            (data?.rating ?: 0f).run { if (this < 0) 0f else this })
                        textSize = 18f
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setPadding(0, 0, dp2px(4f).toInt(), 0)
                    })
                    addView(ImageView(context).apply { setImageResource(R.drawable.ic_baseline_star_20) })
                }
                holder.view<TextView>(R.id.operatingText)?.apply {
                    text = String.format(
                        context.getString(R.string.text_reviews_number),
                        data?.ratingCount ?: 0
                    )
                }
            }
            1 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(TextView(context).apply {
                        text = (data?.page ?: 0).toString()
                        textSize = 18f
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    })
                }
                holder.view<TextView>(R.id.operatingText)?.apply {
                    text = context.getText(R.string.text_pages)
                }
            }
            2 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(ImageView(context).apply { setImageResource(R.drawable.ic_baseline_find_in_page_24) })
                }
                holder.view<TextView>(R.id.operatingText)?.apply {
                    text = context.getText(R.string.text_similar)
                }
            }
            3 -> {
                holder.view<LinearLayout>(R.id.operatingExtend)?.apply {
                    addView(ImageView(context).apply { setImageResource(R.drawable.ic_baseline_description_24) })
                }
                holder.view<TextView>(R.id.operatingText)?.apply {
                    text = context.getText(R.string.text_more_information)
                }
            }
        }
    }

}

