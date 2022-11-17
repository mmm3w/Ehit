package com.mitsuki.ehit.model.page

import com.mitsuki.ehit.crutch.network.ehcore.ApiContainer
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.GalleryDataMeta

class GalleryListPageIn(
    var meta: GalleryDataMeta
) {

    val hintContent: String get() = meta.hint

    val targetUrl: String get() = meta.targetUrl
    var key: GalleryDataKey?
        set(value) {
            if (value != meta.key) {
                meta.key = value
            }
        }
        get() = meta.key


    val enableJump: Boolean get() = if (ApiContainer.isEx) true else maxPage > 1

    var maxPage: Int = 0
    var targetPage: Int = 0

    fun setJumpOrSeek(value: String?, jump: Boolean, next: Boolean) {
        jumpOrSeek = value
        isJump = jump
        isNext = next
    }

    var jumpOrSeek: String? = null
        private set
    var isJump: Boolean = false
        private set
    var isNext: Boolean = false
        private set

}