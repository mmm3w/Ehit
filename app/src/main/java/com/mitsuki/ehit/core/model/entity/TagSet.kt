package com.mitsuki.ehit.core.model.entity

import org.jsoup.nodes.Element

@Suppress("ArrayInDataClass")
data class TagSet(val setName: String, val tags: Array<String>) {
    companion object {

        fun parse(element: Element): TagSet {
            val name = element.child(0).text().replace(":", "")

            val tags = with(element.child(1).children()) {
                Array(size) { number -> this[number].text().replace("|", "").trim() }
            }
            return TagSet(name, tags)
        }

    }
}
