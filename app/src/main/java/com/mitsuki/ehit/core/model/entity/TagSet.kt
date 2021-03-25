package com.mitsuki.ehit.core.model.entity

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize
import org.jsoup.nodes.Element

@Parcelize
data class TagSet(val setName: String, val tags: Array<String>) : Parcelable {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TagSet>() {
            override fun areItemsTheSame(
                oldConcert: TagSet,
                newConcert: TagSet
            ): Boolean =
                oldConcert.setName == newConcert.setName

            override fun areContentsTheSame(
                oldConcert: TagSet,
                newConcert: TagSet
            ): Boolean {
                return oldConcert.tags.size == newConcert.tags.size
            }
        }

        fun parse(element: Element): TagSet {
            val name = element.child(0).text().replace(":", "")

            val tags = with(element.child(1).children()) {
                Array(size) { number -> this[number].text().replace("|", "").trim() }
            }
            return TagSet(name, tags)
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagSet

        if (setName != other.setName) return false
        if (!tags.contentEquals(other.tags)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = setName.hashCode()
        result = 31 * result + tags.contentHashCode()
        return result
    }
}
