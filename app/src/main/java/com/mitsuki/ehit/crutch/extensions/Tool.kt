package com.mitsuki.ehit.crutch.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder

inline fun <reified T : Parcelable> parcelableCreatorOf(): Parcelable.Creator<T> =
    object : Parcelable.Creator<T> {
        override fun newArray(size: Int): Array<T?> = arrayOfNulls(size)
        override fun createFromParcel(source: Parcel?): T =
            T::class.java.getDeclaredConstructor(Parcel::class.java).newInstance(source)
    }


fun String.copying2Clipboard() {
    AppHolder.clipboardManager.setPrimaryClip(
        ClipData.newPlainText(string(R.string.hint_gallery_info), this)
    )
}


fun Context.setPrimaryClip(text: String) {
    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.setPrimaryClip(
        ClipData.newPlainText("Label", text)
    )
}