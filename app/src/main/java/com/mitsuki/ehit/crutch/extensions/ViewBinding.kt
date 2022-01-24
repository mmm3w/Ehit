package com.mitsuki.ehit.crutch.extensions

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <VB : ViewBinding> Activity.viewBinding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).apply { setContentView(root) }
}

fun <VB : ViewBinding> Fragment.viewBinding(bind: (View) -> VB) = FragmentViewBindingHolder(bind)

class FragmentViewBindingHolder<VB : ViewBinding>(private val bind: (View) -> VB) :
    ReadOnlyProperty<Fragment, VB?> {

    private var binding: VB? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB? {
        if (binding == null) {
            binding = thisRef.view?.run { bind(this) }
            if (binding != null) thisRef.viewLifecycleOwner.lifecycle.addObserver(object :
                DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                    owner.lifecycle.removeObserver(this)
                }
            })
        }
        return binding
    }
}

fun <VB : ViewBinding> RecyclerView.ViewHolder.viewBinding(bind: (View) -> VB) =
    lazy { bind(itemView) }







