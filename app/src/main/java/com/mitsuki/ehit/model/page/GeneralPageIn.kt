package com.mitsuki.ehit.model.page

open class GeneralPageIn {
    companion object {
        const val START = 0
    }

    var targetPage: Int = START
        set(value) {
            if (value < 1) throw  IllegalStateException("Page error")
            field = value - 1
        }
}