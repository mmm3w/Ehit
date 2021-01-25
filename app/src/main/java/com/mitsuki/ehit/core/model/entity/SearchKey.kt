package com.mitsuki.ehit.core.model.entity

data class SearchKey(val text: String) {
    val showContent: String
        get() {
            return text
        }
}