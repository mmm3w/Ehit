package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mitsuki.ehit.R
import kotlinx.android.synthetic.main.fragment_disclaimer.*

class DisclaimerFragment : Fragment(R.layout.fragment_disclaimer){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        test_btn.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigate(R.id.action_disclaimer_fragment_to_login_fragment)
        }
    }
}