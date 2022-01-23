package com.mitsuki.ehit.crutch.event

import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class Emitter {

    private val mSubjectMap: MutableMap<String, PublishSubject<*>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    fun <T> emitter(tag: String): PublishSubject<T> {
        return (mSubjectMap[tag] as? PublishSubject<T>) ?: PublishSubject.create<T>()
            .apply { mSubjectMap[tag] = this }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> receiver(tag: String): Observable<T> {
        return ((mSubjectMap[tag] as? PublishSubject<T>) ?: PublishSubject.create<T>()
            .apply { mSubjectMap[tag] = this }).hideWithMainThread()
    }
}



fun <T> EventEmitter.emitter(tag: String): PublishSubject<T> {
    return eventEmitter.emitter(tag)
}

fun <T> EventEmitter.post(tag: String, data: T) {
    emitter<T>(tag).onNext(data)
}

fun <T> EventEmitter.receiver(tag: String): Observable<T> {
    return eventEmitter.receiver(tag)
}