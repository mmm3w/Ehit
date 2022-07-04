package com.mitsuki.ehit.model.entity

sealed class GalleryMode {

    class Normal(val id: Long, val token: String) : GalleryMode() {

    }

    class Local(val id: Long, val token: String) : GalleryMode() {

    }

    class Zip : GalleryMode()

}