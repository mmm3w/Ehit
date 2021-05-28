package com.mitsuki.ehit.ui.temp.adapter

import android.widget.Button
import android.widget.EditText
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class LoginCookieAdapter : SingleItemAdapter(false) {

    private val mLoginEvent: PublishSubject<LoginCookie> = PublishSubject.create()

    val onLogin: Observable<LoginCookie> get() = mLoginEvent.hideWithMainThread()

    override val layoutRes: Int = R.layout.item_login_cookie

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        view<EditText>(R.id.login_ipb_member_id)?.setText(BuildConfig.ipb_member_id)
        view<EditText>(R.id.login_ipb_pass_hash)?.setText(BuildConfig.ipb_pass_hash)
        view<EditText>(R.id.login_igneous)?.setText(BuildConfig.igneous)

        view<Button>(R.id.login_login_btn)?.setOnClickListener {
            val member = view<EditText>(R.id.login_ipb_member_id)?.text.toString()
            val pass = view<EditText>(R.id.login_ipb_pass_hash)?.text.toString()
            val igneous = view<EditText>(R.id.login_igneous)?.text.toString()
            mLoginEvent.onNext(LoginCookie(member, pass, igneous))
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {}

    data class LoginCookie(val memberId: String, val passHash: String, val igneous: String)
}