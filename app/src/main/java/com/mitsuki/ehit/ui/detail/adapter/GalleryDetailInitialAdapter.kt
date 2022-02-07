package com.mitsuki.ehit.ui.detail.adapter

import androidx.core.view.isVisible
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.LoadStateFitBinding

class GalleryDetailInitialAdapter(private val retryAction: () -> Unit) :
    SingleItemBindingAdapter<LoadStateFitBinding>(
        R.layout.load_state_fit,
        LoadStateFitBinding::bind
    ) {


    var errorMsg: Throwable? = null
        set(value) {
            if (field != value) {
                field = value
                if (isEnable) notifyItemChanged(0)
            }
        }


    override val onViewHolderCreate: ViewHolder<LoadStateFitBinding>.() -> Unit = {
        binding.loadStateRetry.setOnClickListener {
            retryAction()
        }
//        binding.loadStateError.setOnLongClickListener {
//            true
//        }
    }

    override val onViewHolderBind: ViewHolder<LoadStateFitBinding>.() -> Unit = {
        binding.loadStateProgress.isVisible = errorMsg == null
        binding.loadStateRetry.isVisible = errorMsg != null
        binding.loadStateError.apply {
            isVisible = errorMsg != null
            text = errorMsg?.toString()
        }
    }

}