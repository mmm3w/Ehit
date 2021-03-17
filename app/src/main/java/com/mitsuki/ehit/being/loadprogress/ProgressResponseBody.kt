package com.mitsuki.ehit.being.loadprogress

import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

class ProgressResponseBody(
    tag: String,
    private val mResponseBody: ResponseBody,
    private val onProgress: PublishSubject<Progress>
) : ResponseBody() {

    private val mProgress by lazy { Progress(tag, 0, 0) }

    override fun contentLength(): Long = mResponseBody.contentLength()

    override fun contentType(): MediaType? = mResponseBody.contentType()

    override fun source(): BufferedSource {
        return mResponseBody.source().run { source(this).buffer() }
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {

            private var mTotalBytesRead: Long = 0

            override fun read(sink: Buffer, byteCount: Long): Long {
                return super.read(sink, byteCount).apply {
                    mTotalBytesRead += if (this != -1L) this else 0
                    onProgress.onNext(mProgress.apply {
                        contentLength = contentLength()
                        currentBytes = mTotalBytesRead
                    })
                }
            }
        }
    }
}