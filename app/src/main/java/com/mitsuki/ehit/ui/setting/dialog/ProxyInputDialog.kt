package com.mitsuki.ehit.ui.setting.dialog

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.ValueFinder
import com.mitsuki.ehit.crutch.extensions.showSelectMenu
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.databinding.DialogProxyInputBinding
import com.mitsuki.ehit.ui.common.dialog.BindingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProxyInputDialog(
    private val onConfirm: (Int, String) -> Unit
) : BindingDialogFragment<DialogProxyInputBinding>(
    R.layout.dialog_proxy_input,
    DialogProxyInputBinding::bind
) {

    init {
        title(res = R.string.text_proxy)
        positiveButton(res = R.string.text_confirm) {
            binding?.apply {
                val host = proxyHost.text.toString().trim()
                if (host.isEmpty()) {
                    proxyHostLayout.isErrorEnabled = true
                    proxyHostLayout.error = text(R.string.error_content_empty)
                    return@positiveButton
                } else {
                    proxyHostLayout.isErrorEnabled = false
                }

                val portStr = proxyPort.text.toString().trim()
                if (portStr.isEmpty()) {
                    proxyPortLayout.isErrorEnabled = true
                    proxyPortLayout.error = text(R.string.error_content_empty)
                    return@positiveButton
                } else {
                    proxyPortLayout.isErrorEnabled = false
                }

                val port = portStr.toIntOrNull()
                if (port == null || port < 0 || port > 65535) {
                    proxyPortLayout.isErrorEnabled = true
                    proxyPortLayout.error = text(R.string.error_invalid_port)
                    return@positiveButton
                } else {
                    proxyPortLayout.isErrorEnabled = false

                }

                shareData.spProxyIp = host
                shareData.spProxyPort = port
                shareData.spProxyMode = currentMode
                memoryData.setProxy(currentMode, host, port)

                onConfirm(currentMode, "$host:$port")
            }

            dismiss()
        }
    }

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    private var currentMode: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentMode = shareData.spProxyMode
        binding?.proxyModeSelect?.apply {
            setText(ValueFinder.proxySummary(currentMode))
            setOnClickListener {
                showSelectMenu(requireContext(), it, R.menu.menu_proxy) { menuIndex ->
                    val index = idTrans(menuIndex)
                    setText(ValueFinder.proxySummary(index))
                    setProxyInputVisible(index)
                    currentMode = index
                }
            }
        }
        setProxyInputVisible(currentMode)
    }


    private fun setProxyInputVisible(mode: Int) {
        when (mode) {
            0, 1 -> {
                binding?.proxyHostLayout?.isVisible = false
                binding?.proxyPortLayout?.isVisible = false
            }
            2, 3 -> {
                binding?.proxyHostLayout?.isVisible = true
                binding?.proxyPortLayout?.isVisible = true
                binding?.proxyHost?.setText(shareData.spProxyIp)
                val port = shareData.spProxyPort.takeIf { it >= 0 }?.toString() ?: ""
                binding?.proxyPort?.setText(port)
            }
        }
    }

    private fun idTrans(id: Int): Int {
        return when (id) {
            R.id.proxy_direct -> 0
            R.id.proxy_system -> 1
            R.id.proxy_http -> 2
            R.id.proxy_socks -> 3
            else -> throw  IllegalArgumentException()
        }
    }

}