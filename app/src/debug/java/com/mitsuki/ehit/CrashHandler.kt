package com.mitsuki.ehit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Process
import android.util.Log
import com.mitsuki.ehit.crutch.extensions.ensureDir
import com.mitsuki.ehit.crutch.uils.crashTime
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

object CrashHandler : Thread.UncaughtExceptionHandler {
    private val mDefaultExceptionHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    private lateinit var mLogCacheFile: File

    fun init(context: Context) {
        /*try create*/
        Log.i("CrashHandler", "Create")
        Thread.setDefaultUncaughtExceptionHandler(this)
        mLogCacheFile = File(context.cacheDir, "crash_log")
        mLogCacheFile.ensureDir()
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            try {
                val writer: Writer = StringWriter()
                val printWriter = PrintWriter(writer)
                e.printStackTrace(printWriter)
                var cause = e.cause
                while (cause != null) {
                    cause.printStackTrace(printWriter)
                    cause = cause.cause
                }
                printWriter.close()
                //日志数据
                val result = writer.toString()
                File(mLogCacheFile, "${System.currentTimeMillis().crashTime()}.log")
                    .writeText(result, charset = Charsets.UTF_8)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }

            try {
                Thread.sleep(500)
            } catch (e1: InterruptedException) {
            }
            mDefaultExceptionHandler?.uncaughtException(t, e) ?: killProcessAndExit()
        } catch (exception: java.lang.Exception) {
            //ignored
        }
    }

    private fun killProcessAndExit() {
        try {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        } catch (e: Exception) {
            //ignored
        }
    }
}