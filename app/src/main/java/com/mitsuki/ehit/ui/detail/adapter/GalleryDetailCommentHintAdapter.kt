package com.mitsuki.ehit.ui.detail.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.ItemGalleryDetailCommentBinding
import com.mitsuki.ehit.model.entity.CommentState

class GalleryDetailCommentHintAdapter :
    SingleItemBindingAdapter<ItemGalleryDetailCommentBinding>(
        R.layout.item_gallery_detail_comment,
        ItemGalleryDetailCommentBinding::bind,
        false
    ), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    var commentState: CommentState? = null
        set(value) {
            if (value != field) {
                when {
                    isEnable && value == null -> isEnable = false
                    !isEnable && value != null -> isEnable = true
                    isEnable && value != null -> notifyItemChanged(0)
                }
                field = value
            }
        }

    override val onViewHolderCreate: ViewHolder<ItemGalleryDetailCommentBinding>.() -> Unit = {
        itemView.setOnClickListener { post("comment", "More Comment") }
    }

    override val onViewHolderBind: ViewHolder<ItemGalleryDetailCommentBinding>.() -> Unit = {
        binding.galleryDetailMore.text = when (commentState) {
            CommentState.NoComments -> text(R.string.text_no_comments)
            CommentState.AllLoaded -> text(R.string.text_no_more_comments)
            CommentState.MoreComments -> text(R.string.text_more_comments)
            else -> ""
        }
    }
}