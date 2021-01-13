package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.being.ShareData

class MainFragment : BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShareData.remove(ShareData.SP_FIRST_OPEN)
    }
}