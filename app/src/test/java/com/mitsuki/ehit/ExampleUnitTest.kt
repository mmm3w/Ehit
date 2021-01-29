package com.mitsuki.ehit

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*
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
        return withContext(Dispatchers.IO){
            delay(1000L)
            13
        }
    }

    suspend fun doSomethingUsefulTwoC(): Int {
        return withContext(Dispatchers.IO){
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
}
