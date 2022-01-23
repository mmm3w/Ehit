package com.mitsuki.ehit.ui.start.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.OpenGate
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extend.observe
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentDisclaimerBinding
import com.mitsuki.ehit.ui.main.MainActivity
import com.mitsuki.ehit.ui.start.adapter.DisclaimerAdapter

class DisclaimerFragment : Fragment(R.layout.fragment_disclaimer) {

    private val mAdapter by lazy { DisclaimerAdapter() }
    private val binding by viewBinding(FragmentDisclaimerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (ShareData.spWaringConfirm) nextNav()

        binding?.disclaimerUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mAdapter.receiver<Boolean>("disclaimer").observe(viewLifecycleOwner) {
            if (it == true) nextNav() else requireActivity().finish()
        }
    }

    private fun nextNav() {
        ShareData.spWaringConfirm = true
        with(OpenGate.nextNav) {
            if (this == -1) {
                (requireActivity() as MainActivity).navDestination(R.id.nav_stack_main, null)
            } else {
                (requireActivity() as MainActivity).navigate(this)
            }
        }
    }
}