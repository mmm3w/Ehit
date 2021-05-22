package com.mitsuki.ehit.ui.adapter

import android.widget.Button
import android.widget.RadioGroup
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.network.Url
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


class LoginHeader : SingleItemAdapter(true) {
    override val layoutRes: Int = R.layout.item_login_header
    override val onViewHolderCreate: ViewHolder.() -> Unit = {}
    override val onViewHolderBind: ViewHolder.() -> Unit = {}
}

class LoginExtend : SingleItemAdapter(true) {
    private var mIsCookieLogin = false

    private val mSwitchEvent: PublishSubject<Boolean> = PublishSubject.create()
    private val mSkipEvent: PublishSubject<Int> = PublishSubject.create()

    val onSwitch: Observable<Boolean> get() = mSwitchEvent.hideWithMainThread()
    val onSkip: Observable<Int> get() = mSkipEvent.hideWithMainThread()

    override val layoutRes: Int = R.layout.item_login_extend
    override val onViewHolderCreate: ViewHolder.() -> Unit = {

        view<Button>(R.id.login_extend_skip)?.setOnClickListener { mSkipEvent.onNext(-1) }

        view<Button>(R.id.login_extend_switch)?.setOnClickListener {
            mIsCookieLogin = !mIsCookieLogin
            (it as? Button)?.apply {
                text = if (mIsCookieLogin) context.getText(R.string.text_login_by_account)
                else context.getText(R.string.text_login_by_cookie)
            }
            mSwitchEvent.onNext(mIsCookieLogin)
        }
    }
    override val onViewHolderBind: ViewHolder.() -> Unit = {}
}

class LoginDomain : SingleItemAdapter(true) {

    private var checkRadioGroup: RadioGroup? = null

    override val layoutRes: Int = R.layout.item_login_domain

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        checkRadioGroup = itemView.findViewById<RadioGroup>(R.id.domain_check_radio)?.apply {
            setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.domain_check_e -> ShareData.spDomain = Url.domain[0]
                    R.id.domain_check_ex -> ShareData.spDomain = Url.domain[1]
                }
            }
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        when (ShareData.spDomain) {
            Url.domain[0] -> checkRadioGroup?.check(R.id.domain_check_e)
            Url.domain[1] -> checkRadioGroup?.check(R.id.domain_check_ex)
        }
    }
}