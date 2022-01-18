package com.mitsuki.ehit.ui.download.activity

import android.graphics.Color
import android.os.Bundle
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityDownloadBinding
import com.mitsuki.ehit.databinding.ActivityMoreInfoBinding

class DownloadActivity : BaseActivity() {

    private val binding by viewBinding(ActivityDownloadBinding::inflate)
    private val controller by windowController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller.window(
            navigationBarLight = true,
            statusBarLight = true,
            navigationBarColor = Color.WHITE,
            statusBarColor = Color.WHITE
        )
        binding.topBar.topBarText.text = getText(R.string.text_download)

        //提供侧滑删除
        //封面，标题，作者，作者下面空出的部分用作显示进度条，右上角提供扩展菜单（扩展菜单中有导出和批量导出，批量导出进入多选效果）
        //所有下载将下载至内部缓存
        //选择导出时才会申请权限
        //点击下载弹出范围选择窗口

        //同一个画廊做数据合并，如何处理不连续的下载内容

        //如何去做一个下载机制，以及如何去做一个进度条通知，以及通知栏的通知
        //下载本身就是一个后台子线程的行为，该页面仅做部分内容上的展示

        /**
         * 1、点击下载按钮
         */
    }


}