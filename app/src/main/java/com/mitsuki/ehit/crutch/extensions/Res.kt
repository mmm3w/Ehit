package com.mitsuki.ehit.crutch.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mitsuki.ehit.crutch.AppHolder

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Fragment.getInteger(@IntegerRes id: Int) = resources.getInteger(id)


fun Context.string(@StringRes id: Int): String = getString(id)

fun Context.text(@StringRes id: Int): CharSequence = getText(id)

fun Context.color(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Fragment.color(@ColorRes id: Int): Int = requireActivity().color(id)


fun string(@StringRes id: Int): String = AppHolder.getString(id)

fun text(@StringRes id: Int): CharSequence = AppHolder.getText(id)