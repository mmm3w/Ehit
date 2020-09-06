package com.mitsuki.ehit.core.model.entity

import androidx.recyclerview.widget.DiffUtil
import org.jsoup.nodes.Element

@Suppress("ArrayInDataClass")
data class TagSet(val setName: String, val tags: Array<String>) {
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
}
