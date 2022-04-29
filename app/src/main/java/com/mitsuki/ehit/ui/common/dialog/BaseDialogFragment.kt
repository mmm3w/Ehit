package com.mitsuki.ehit.ui.common.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.text

abstract class BaseDialogFragment : DialogFragment() {

    private val baseInfo: BaseDialogInfo = BaseDialogInfo()

    private var createdAction: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myContainer = inflater.inflate(R.layout.dialog_base_container, container, false)
        val subView = onCreateSubView(inflater, savedInstanceState)
        subView?.apply {
            myContainer.findViewById<FrameLayout>(R.id.dialog_base_sub).addView(subView)
        }
        return myContainer
    }

    abstract fun onCreateSubView(inflater: LayoutInflater, savedInstanceState: Bundle?): View?

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0x00000000));
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        baseInfo.apply(this, view)
        createdAction?.invoke()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isAdded) return
        super.show(manager, tag)
    }

    override fun dismiss() {
        if (!isAdded) return
        super.dismiss()
    }

    fun title(text: CharSequence? = null, res: Int = -1) {
        if (text.isNullOrEmpty() && res == -1) return
        baseInfo.title = if (text.isNullOrEmpty()) {
            text(res)
        } else {
            text
        }
    }

    fun positiveBtn(text: CharSequence? = null, res: Int = -1, action: (DialogFragment) -> Unit) {
        if (text.isNullOrEmpty() && res == -1) {
            baseInfo.positiveText = ""
            baseInfo.positiveAction = null
            return
        }
        baseInfo.positiveText = if (text.isNullOrEmpty()) {
            text(res)
        } else {
            text
        }
        baseInfo.positiveAction = action
    }

    fun negativeButton(
        text: CharSequence? = null,
        res: Int = -1,
        action: (DialogFragment) -> Unit
    ) {
        if (text.isNullOrEmpty() && res == -1) {
            baseInfo.negativeText = ""
            baseInfo.negativeAction = null
            return
        }
        baseInfo.negativeText = if (text.isNullOrEmpty()) {
            text(res)
        } else {
            text
        }
        baseInfo.negativeAction = action
    }

    fun neutralButton(text: CharSequence? = null, id: Int = -1, action: (DialogFragment) -> Unit) {
        if (text.isNullOrEmpty() && id == -1) {
            baseInfo.neutralText = ""
            baseInfo.neutralAction = null
            return
        }
        baseInfo.neutralText = if (text.isNullOrEmpty()) {
            text(id)
        } else {
            text
        }
        baseInfo.neutralAction = action
    }

    fun created(action: () -> Unit) {
        createdAction = action
    }

    class BaseDialogInfo(
        var title: CharSequence = "",
        var neutralText: CharSequence = "",
        var neutralAction: ((DialogFragment) -> Unit)? = null,
        var negativeText: CharSequence = "",
        var negativeAction: ((DialogFragment) -> Unit)? = null,
        var positiveText: CharSequence = "",
        var positiveAction: ((DialogFragment) -> Unit)? = null,
    ) {
        fun apply(dialog: DialogFragment, view: View) {
            view.findViewById<TextView>(R.id.dialog_base_title).apply {
                isVisible = title.isNotEmpty()
                text = title
            }
            view.findViewById<TextView>(R.id.dialog_base_extend_btn).apply {
                isVisible = neutralText.isNotEmpty()
                text = neutralText
                setOnClickListener { neutralAction?.invoke(dialog) }
            }
            view.findViewById<TextView>(R.id.dialog_base_positive_btn).apply {
                isVisible = positiveText.isNotEmpty()
                text = positiveText
                setOnClickListener { positiveAction?.invoke(dialog) }
            }
            view.findViewById<TextView>(R.id.dialog_base_negative_btn).apply {
                isVisible = negativeText.isNotEmpty()
                text = negativeText
                setOnClickListener { negativeAction?.invoke(dialog) }
            }
        }
    }
}

fun <V : BaseDialogFragment> V.show(
    manager: FragmentManager,
    tag: String?,
    action: V.() -> Unit
) {
    action()
    show(manager, tag)
}

