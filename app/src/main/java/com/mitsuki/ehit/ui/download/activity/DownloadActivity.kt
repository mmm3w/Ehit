package com.mitsuki.ehit.ui.download.activity

import android.graphics.Color
import android.os.Bundle
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseActivity
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.crutch.windowController
import com.mitsuki.ehit.databinding.ActivityDownloadBinding

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


        //点击下载 显示范围选择弹窗，默认全范围，生成一个downloadTask，然后通过startService带给service
        //先在info表查询旧数据，然后进行数据块合并更新数据源，然后插入新数据，最后再整合校对
        //如果total和node表中查出来的数量对不上怎么办
        //那么以node表中的数据为准

        //下载线程池依托于service吧，否则应用划掉了，下载也就无了

        //单次下载为一个task，单个task按每张图片画风为一个任务，当所有下载请求调用过之后
        //会产生一个当前task的执行结果，包含成功了多少，失败了多少
        //一个task往往包含画廊信息

        //如果使用binder通信的话，需要绑定service实例
        //如果使用广播的话，在有需要的时候注册广播应该就没问题了
        //而且，界面上数据的更新一般也是依托于数据库的flow
        //后续如果不依托flow刷新界面，或许可以依托广播自己处理相关数据

        //子线程中的数据传出来可能还是要依赖广播，否则可能会引用相关实例在
    }


}