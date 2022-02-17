package com.mitsuki.ehit

import android.util.Log
import com.mitsuki.ehit.model.download.DownloadQueue
import org.junit.Test

class DownloadQueueTest {


    @Test
    fun test() {
        val queue = DownloadQueue<String>(3)
        val c = System.currentTimeMillis()

        queue.put(
            arrayListOf(
                "1" to arrayListOf("1-1", "1-2", "1-3", "1-4", "1-5"),
                "2" to arrayListOf("2-1", "2-2", "2-3", "2-4", "2-5"),
                "3" to arrayListOf("3-1", "3-2", "3-3", "3-4"),
                "4" to arrayListOf("4-1", "4-2", "4-3", "4-4", "4-5"),
            )
        )

        while (true) {
            queue.take().apply {
                val s = System.currentTimeMillis() - c
                Thread {
                    Thread.sleep(1000)
                    println("$this ---> $s~${(System.currentTimeMillis() - c)}")
                    queue.idle()
                }.start()
            }
        }
    }
}