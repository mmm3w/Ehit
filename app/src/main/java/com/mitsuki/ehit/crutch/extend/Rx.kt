package com.mitsuki.ehit.crutch.extend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <T : Any> Observable<T>.observe(owner: LifecycleOwner, onNext: Consumer<T>) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    owner.lifecycle.addObserver(RxLife(subscribe(onNext), owner))
}

fun <T : Any> Observable<T>.observeWithCoro(owner: LifecycleOwner, action: suspend (T) -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    owner.lifecycle.addObserver(RxLife(subscribe {
        owner.lifecycleScope.launch(Dispatchers.IO) { action(it) }
    }, owner))
}


inline fun <reified T> Observable<T>.hideWithMainThread(): Observable<T> =
    hide().observeOn(AndroidSchedulers.mainThread())
