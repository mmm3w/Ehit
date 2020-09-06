package com.mitsuki.ehit.core.crutch

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.core.model.entity.TagSet

class TagDiffer(private val mOldArray: Array<TagSet>, private val mNewArray: Array<TagSet>) :
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TagSet.DIFF_CALLBACK.areItemsTheSame(
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
        return TagSet.DIFF_CALLBACK.areContentsTheSame(
            mOldArray[oldItemPosition],
            mNewArray[newItemPosition]
        )
    }
}