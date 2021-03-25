package com.mitsuki.ehit.being.extend

import android.os.Parcel
import android.os.Parcelable

inline fun <reified T : Parcelable> parcelableCreatorOf(): Parcelable.Creator<T> = object : Parcelable.Creator<T> {
    override fun newArray(size: Int): Array<T?> = arrayOfNulls(size)
    override fun createFromParcel(source: Parcel?): T =
        T::class.java.getDeclaredConstructor(Parcel::class.java).newInstance(source)
}