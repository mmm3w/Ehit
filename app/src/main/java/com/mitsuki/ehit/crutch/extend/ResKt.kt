package com.mitsuki.ehit.crutch.extend

import android.content.Context
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Fragment.getInteger(@IntegerRes id: Int) = resources.getInteger(id)