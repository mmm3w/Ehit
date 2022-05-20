package com.mitsuki.ehit.ui.favourite

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogFavouriteSelectBinding
import com.mitsuki.ehit.ui.common.dialog.BindingBottomDialogFragment
import com.mitsuki.ehit.ui.common.dialog.BottomDialogFragment

class FavouriteSelectPanel : BindingBottomDialogFragment<DialogFavouriteSelectBinding>(
    R.layout.dialog_favourite_select,
    DialogFavouriteSelectBinding::bind
) {

    private val mAdapter: FavouriteItemAdapter by lazy { FavouriteItemAdapter() }

    var onFavouriteSelect: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter.receiver<Int>("check").observe(this) {
            onFavouriteSelect?.invoke(it - 1)
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireDialog().setCanceledOnTouchOutside(true)
        isCancelable = true

        binding?.favouriteTarget?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    fun postCountData(data: Array<Pair<String, Int>>) {
        mAdapter.postUpdate(lifecycle, data = data.toList())
    }

}