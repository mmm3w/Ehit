package com.mitsuki.ehit.crutch

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Key(vararg val key: String)