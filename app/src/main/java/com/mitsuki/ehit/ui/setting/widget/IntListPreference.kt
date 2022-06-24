package com.mitsuki.ehit.ui.setting.widget

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference

class IntListPreference : ListPreference {
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun persistString(value: String): Boolean {
        val intValue = value.toInt()
        return persistInt(intValue)
    }

    override fun getPersistedString(defaultReturnValue: String?): String {
        val intValue: Int = if (defaultReturnValue != null) {
            val intDefaultReturnValue = defaultReturnValue.toInt()
            getPersistedInt(intDefaultReturnValue)
        } else {
            // We haven't been given a default return value, but we need to specify one when retrieving the value
            if (getPersistedInt(0) == getPersistedInt(1)) {
                // The default value is being ignored, so we're good to go
                getPersistedInt(0)
            } else {
                throw IllegalArgumentException("Cannot get an int without a default return value")
            }
        }
        return intValue.toString()
    }


}