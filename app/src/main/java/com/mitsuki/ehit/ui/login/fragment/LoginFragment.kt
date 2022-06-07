package com.mitsuki.ehit.ui.login.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BindingFragment
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.event.receiver
import com.mitsuki.ehit.crutch.extensions.isClick
import com.mitsuki.ehit.crutch.extensions.observe
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.databinding.GeneralRecyclerViewBinding
import com.mitsuki.ehit.ui.login.activity.LoginActivity
import com.mitsuki.ehit.ui.login.adapter.LoginAccountAdapter
import com.mitsuki.ehit.ui.login.adapter.LoginCookieAdapter
import com.mitsuki.ehit.ui.login.adapter.LoginDomain
import com.mitsuki.ehit.ui.login.adapter.LoginExtend
import com.mitsuki.ehit.ui.main.MainActivity
import com.mitsuki.ehit.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment :
    BindingFragment<GeneralRecyclerViewBinding>(
        R.layout.general_recycler_view,
        GeneralRecyclerViewBinding::bind
    ) {

    private val mViewModel: LoginViewModel
            by createViewModelLazy(LoginViewModel::class, { viewModelStore })

    private val mAccountAdapter by lazy { LoginAccountAdapter() }
    private val mCookieAdapter by lazy { LoginCookieAdapter() }
    private val mDomain by lazy { LoginDomain(memoryData) }
    private val mExtend by lazy { LoginExtend() }

    private val mAdapter by lazy {
        ConcatAdapter(
            mAccountAdapter,
            mCookieAdapter,
            mDomain,
            mExtend
        )
    }

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.listUi?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.receiver<Int>("next")
            .isClick()
            .observe(viewLifecycleOwner) { nextNav() }

        mViewModel.receiver<String>("toast")
            .isClick()
            .observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            }
        mExtend.receiver<Boolean>("switch")
            .isClick()
            .observe(viewLifecycleOwner) {
                mAccountAdapter.isEnable = !it
                mCookieAdapter.isEnable = it
            }

        mExtend.receiver<Int>("skip")
            .isClick()
            .observe(viewLifecycleOwner) { nextNav() }

        mAccountAdapter.receiver<LoginAccountAdapter.Account>("login")
            .isClick()
            .observe(viewLifecycleOwner) {
                mViewModel.login(it.account, it.password)
            }

        mCookieAdapter.receiver<LoginCookieAdapter.LoginCookie>("login")
            .isClick()
            .observe(viewLifecycleOwner) { mViewModel.login(it.memberId, it.passHash, it.igneous) }
    }

    private fun nextNav() {
        shareData.spInitial = false
        when (activity) {
            is MainActivity -> Navigation.findNavController(requireView())
                .navigate(R.id.action_global_first_time_back)

            is LoginActivity -> {
                requireActivity().apply {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}