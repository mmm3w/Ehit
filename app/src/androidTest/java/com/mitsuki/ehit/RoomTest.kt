package com.mitsuki.ehit

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomTest {


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {

    }


}