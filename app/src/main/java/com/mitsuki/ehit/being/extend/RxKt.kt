package com.mitsuki.ehit.being.extend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.Subject

fun <T:Any> Observable<T>.observe(owner: LifecycleOwner, onNext: Consumer<T>) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    owner.lifecycle.addObserver(RxLife(subscribe(onNext)))
}

inline fun <reified T> Observable<T>.hideWithMainThread(): Observable<T> =
    hide().observeOn(AndroidSchedulers.mainThread())
