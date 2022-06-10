package com.mitsuki.ehit.model.ehparser

object GalleryPageSize {
    @Volatile
    var size: Int = -1
        private set

    val illegalSize: Boolean
        get() = size <= 0

    fun parseSize(content: String) {
        when {
            content.contains("<div class=\"ths nosel\">Normal</div>") -> size = 40
            content.contains("<div class=\"ths nosel\">Large</div>") -> size = 20
        }
    }
}