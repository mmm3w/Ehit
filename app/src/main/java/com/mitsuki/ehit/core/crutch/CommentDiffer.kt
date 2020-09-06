package com.mitsuki.ehit.core.crutch

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.core.model.entity.Comment
import com.mitsuki.ehit.core.model.entity.TagSet

class CommentDiffer(private val mOldArray: Array<Comment>, private val mNewArray: Array<Comment>) :
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return Comment.DIFF_CALLBACK.areItemsTheSame(
            mOldArray[oldItemPosition],
            mNewArray[newItemPosition]
        )
    }

    override fun getOldListSize(): Int {
        return mOldArray.size
    }

    override fun getNewListSize(): Int {
        return mNewArray.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return Comment.DIFF_CALLBACK.areContentsTheSame(
            mOldArray[oldItemPosition],
            mNewArray[newItemPosition]
        )
    }
}