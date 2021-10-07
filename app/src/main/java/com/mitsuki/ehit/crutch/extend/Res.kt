package com.mitsuki.ehit.crutch.extend

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.mitsuki.ehit.crutch.AppHolder

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Fragment.getInteger(@IntegerRes id: Int) = resources.getInteger(id)


fun string(@StringRes id: Int): String = AppHolder.string(id)

fun drawable(@DrawableRes id: Int): Drawable? = AppHolder.drawable(id)