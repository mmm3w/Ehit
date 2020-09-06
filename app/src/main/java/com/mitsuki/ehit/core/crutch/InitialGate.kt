package com.mitsuki.ehit.core.crutch

class InitialGate {
    companion object {
        private const val PREP_MASK = 0x1
        private const val CORE_MASK = 0x2
    }

    private var mSwitch: Int = 0

    fun prep(open: Boolean) {
        mSwitch = if (open) {
            mSwitch or PREP_MASK
        } else {
            mSwitch and PREP_MASK.inv()
        }
    }

    fun trigger() {
        if (mSwitch and PREP_MASK == PREP_MASK) {
            mSwitch = mSwitch or CORE_MASK
        }
    }

    fun ignore(): Boolean {
        return mSwitch and CORE_MASK == CORE_MASK
    }

    fun reset() {
        mSwitch = 0
    }
}