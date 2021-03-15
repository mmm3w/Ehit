package com.mitsuki.ehit.core.ui.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mitsuki.ehit.being.MemoryCache
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.ui.fragment.GalleryFragment

class GalleryFragmentAdapter(
    activity: FragmentActivity,
    isReverse: Boolean,
    private val mId: Long,
    private val mToken: String,
    private val mSize: Int
) : FragmentStateAdapter(activity) {

    var isReverse = isReverse
        set(value) {
            if (value != field){
                notifyDataSetChanged()
                field = value
            }
        }


    override fun getItemCount(): Int {
        return mSize
    }

    override fun createFragment(position: Int): Fragment {
        return GalleryFragment().apply {
            val realIndex = if (isReverse) mSize - position -1 else position
            arguments =
                bundleOf(
                    DataKey.GALLERY_ID to mId,
                    DataKey.GALLERY_TOKEN to mToken,
                    DataKey.IMAGE_TOKEN to MemoryCache.getImageToken(mId, realIndex),
                    DataKey.GALLERY_INDEX to realIndex
                )
        }
    }
}