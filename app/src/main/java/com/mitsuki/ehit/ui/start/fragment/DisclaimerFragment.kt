package com.mitsuki.ehit.ui.start.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.isClick
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.databinding.GeneralRecyclerViewBinding
import com.mitsuki.ehit.ui.main.MainActivity
import com.mitsuki.ehit.ui.start.adapter.DisclaimerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisclaimerFragment : BindingFragment<GeneralRecyclerViewBinding>(
    R.layout.general_recycler_view,
    GeneralRecyclerViewBinding::bind
) {

    private val mAdapter by lazy { DisclaimerAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? MainActivity)?.setDrawerEnable(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.listUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mAdapter.receiver<Boolean>("disclaimer")
            .isClick()
            .observe(viewLifecycleOwner) {
                if (it == true) nextNav() else requireActivity().finish()
            }
    }

    private fun nextNav() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_disclaimer_fragment_to_login_fragment_first_time)
    }
}