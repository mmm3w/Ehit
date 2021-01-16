package com.mitsuki.ehit.core.crutch

import java.lang.RuntimeException

class PageIn {
    //0为起始第一页
    var targetPage: Int = 0
        set(value) {
            if (value < 1) throw  RuntimeException("Value error")
            field = value - 1
        }
}