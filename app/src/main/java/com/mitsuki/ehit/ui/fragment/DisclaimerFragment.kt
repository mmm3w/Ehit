package com.mitsuki.ehit.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.FragmentDisclaimerBinding
import com.mitsuki.ehit.ui.adapter.DisclaimerAdapter

class DisclaimerFragment : Fragment(R.layout.fragment_disclaimer) {

    private val mAdapter by lazy { DisclaimerAdapter() }
    private val binding by viewBinding(FragmentDisclaimerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.disclaimerUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mAdapter.onEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                    .navigate(R.id.action_disclaimer_fragment_to_login_fragment)
            } else {
                //TODO:退出APP
            }
        })
    }
}