package com.mitsuki.ehit.ui.download

import com.mitsuki.ehit.model.entity.DownloadAtom
import java.util.concurrent.FutureTask

class DownloadComparatorTask(runnable: Runnable) : FutureTask<Any>(runnable, null),
    Comparator<DownloadAtom> {

    override fun compare(p0: DownloadAtom?, p1: DownloadAtom?): Int {
        TODO("Not yet implemented")
    }
}