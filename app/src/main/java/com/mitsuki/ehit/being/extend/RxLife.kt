package com.mitsuki.ehit.being.extend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.disposables.Disposable

class RxLife(private val disposable: Disposable) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestroy() {
        if (!disposable.isDisposed) disposable.dispose()
    }
}