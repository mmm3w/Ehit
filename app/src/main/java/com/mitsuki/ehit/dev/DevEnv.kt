package com.mitsuki.ehit.dev

object DevEnv {
    var NSFW = true

    fun nsfwSwitch():Boolean{
        NSFW = !NSFW
        return NSFW
    }
}