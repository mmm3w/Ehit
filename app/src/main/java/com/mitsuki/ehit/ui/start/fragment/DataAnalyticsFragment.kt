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
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.databinding.GeneralRecyclerViewBinding
import com.mitsuki.ehit.ui.start.adapter.DataAnalyticsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DataAnalyticsFragment : BindingFragment<GeneralRecyclerViewBinding>(
    R.layout.general_recycler_view,
    GeneralRecyclerViewBinding::bind
) {

    private val mAdapter by lazy { DataAnalyticsAdapter() }

    @Inject
    lateinit var shareData: ShareData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding?.listUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mAdapter.receiver<Boolean>("event")
            .isClick()
            .observe(viewLifecycleOwner) {
                shareData.spDataAnalytics = it
                nextNav()
            }
    }

    private fun nextNav() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_data_analytics_fragment_to_login_fragment_first_time)
    }
}