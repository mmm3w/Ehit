package com.mitsuki.ehit.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment {
    constructor() : super()

    constructor(@LayoutRes layout: Int) : super(layout)
}