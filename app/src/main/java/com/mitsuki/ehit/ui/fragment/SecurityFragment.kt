package com.mitsuki.ehit.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mitsuki.ehit.R
import kotlinx.android.synthetic.main.fragment_security.*

/**
 * 图案指纹锁定
 *  [上上下下左右左右双击结界]，请按照手指方向滑动屏幕并注意停顿松手
 */
class SecurityFragment : Fragment(R.layout.fragment_security) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        test_btn.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.main_nav_fragment).navigateUp()
        }
    }
}