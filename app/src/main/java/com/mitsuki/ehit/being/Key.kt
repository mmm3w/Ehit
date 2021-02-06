package com.mitsuki.ehit.being

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Key(vararg val key: String)