package com.mitsuki.ehit.ui.detail.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.ui.detail.fragment.GalleryFragment

class GalleryFragmentAdapter(
    activity: FragmentActivity,
    private val mId: Long,
    private val mToken: String,
    private val mSize: Int
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return mSize
    }

    override fun createFragment(position: Int): Fragment {
        return GalleryFragment().apply {
            arguments =
                bundleOf(
                    DataKey.GALLERY_ID to mId,
                    DataKey.GALLERY_TOKEN to mToken,
                    DataKey.GALLERY_INDEX to position
                )
        }
    }
}