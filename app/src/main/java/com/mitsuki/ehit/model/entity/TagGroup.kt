package com.mitsuki.ehit.model.entity

import org.jsoup.nodes.Element

data class TagGroup(val groupName: String, val tags: Array<String>) {

    companion object {
        fun parse(element: Element): TagGroup {
            val name = element.child(0).text().replace(":", "")

            val tags = with(element.child(1).children()) {
                Array(size) { number -> this[number].text().replace("|", "").trim() }
            }
            return TagGroup(name, tags)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return other is TagGroup &&
                groupName == other.groupName &&
                tags.contentEquals(other.tags)
    }

    override fun hashCode(): Int {
        var result = groupName.hashCode()
        result = 31 * result + tags.contentHashCode()
        return result
    }
}
