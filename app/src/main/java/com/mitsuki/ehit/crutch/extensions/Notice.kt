package com.mitsuki.ehit.crutch.extensions

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Context.showToast(text: CharSequence? = null, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun View.showSnackBar(
    text: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT,
    context: Context? = null
) {
    if (context == null) {
        Snackbar.make(this, text, duration).show()
    } else {
        Snackbar.make(context, this, text, duration).show()
    }
}

fun Fragment.showSnackBar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
    view?.showSnackBar(text, duration, context)
}


fun View.showPopupMenu(context: Context, @MenuRes menu: Int, click: (Int) -> Unit) {
    val popupMenu = PopupMenu(context, this)
    popupMenu.menuInflater.inflate(menu, popupMenu.menu)
    popupMenu.setOnMenuItemClickListener {
        it?.itemId?.apply(click)
        true
    }
    popupMenu.show()
}
