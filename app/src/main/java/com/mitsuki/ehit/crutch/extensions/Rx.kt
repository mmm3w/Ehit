package com.mitsuki.ehit.crutch.extensions

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

fun <T : Any> Observable<T>.observe(owner: LifecycleOwner, onNext: Consumer<T>) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    owner.lifecycle.addObserver(RxLife(subscribe(onNext)))
}

fun <T : Any> Observable<T>.observeWithCoro(owner: LifecycleOwner, action: suspend (T) -> Unit) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    owner.lifecycle.addObserver(RxLife(subscribe {
        owner.lifecycleScope.launch(Dispatchers.IO) { action(it) }
    }))
}

fun <T> Observable<T>.hideWithMainThread(): Observable<T> =
    hide().observeOn(AndroidSchedulers.mainThread())


class RxLife(private val disposable: Disposable) : DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        if (!disposable.isDisposed) disposable.dispose()
        owner.lifecycle.removeObserver(this)
    }
}

inline fun <reified T : Any> Observable<T>.isClick(): Observable<T> =
    throttleFirst(444, TimeUnit.MILLISECONDS)
