package com.mitsuki.ehit.crutch.extend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.disposables.Disposable

class RxLife(private val disposable: Disposable, private val owner: LifecycleOwner) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestroy() {
        if (!disposable.isDisposed) disposable.dispose()
        owner.lifecycle.removeObserver(this)
    }
}