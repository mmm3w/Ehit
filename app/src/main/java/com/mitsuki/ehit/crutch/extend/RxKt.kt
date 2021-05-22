package com.mitsuki.ehit.crutch.extend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

fun <T : Any> Observable<T>.observe(owner: LifecycleOwner, onNext: Consumer<T>) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    owner.lifecycle.addObserver(RxLife(subscribe(onNext), owner))
}

inline fun <reified T> Observable<T>.hideWithMainThread(): Observable<T> =
    hide().observeOn(AndroidSchedulers.mainThread())
