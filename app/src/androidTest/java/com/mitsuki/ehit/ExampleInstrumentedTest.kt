package com.mitsuki.ehit

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mitsuki.ehviewer", appContext.packageName)
    }


    @Test
    fun myTest() {
        val uri1 = Uri.parse("https://e-hentai.org/?f_search=stockings")
        println("-------------")
        println(uri1.host)
        println(uri1.path)
        println(uri1.query)

        val uri2 = Uri.parse("https://e-hentai.org/g/2115573/fab7305444/")
        println("-------------")
        println(uri2.host)
        println(uri2.path)
        println(uri2.query)

    }
}
