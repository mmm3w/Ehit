package com.mitsuki.ehit.crutch.extensions

@Suppress("UNCHECKED_CAST")
fun <T> Any.safeAs(action: T.() -> Unit) {
    (this as? T)?.apply(action)
}

fun String?.saveToInt(default: Int = 0): Int {
    if (this == null) return default
    return toIntOrNull() ?: default
}

fun String?.saveToDouble(default: Double = 0.0): Double {
    if (this == null) return default
    return toDoubleOrNull() ?: default
}

fun String?.saveToLong(default: Long = 0L): Long {
    if (this == null) return default
    return toLongOrNull() ?: default
}

fun String?.saveToFloat(default: Float = 0F): Float {
    if (this == null) return default
    return toFloatOrNull() ?: default
}