package com.mitsuki.ehit.service.download

import kotlinx.coroutines.sync.Mutex

//能不能做到自己嵌自己，实现块上的控制

class Hopper {

    //进料出料均依赖该口
    private val mFodderLock = Mutex()





}