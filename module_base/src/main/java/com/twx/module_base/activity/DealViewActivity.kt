package com.twx.module_base.activity

import android.graphics.Color
import com.twx.module_base.R
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.databinding.ActivityDealBinding
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.MyStatusBarUtil
import com.twx.module_base.utils.PackageUtil
import com.twx.module_base.utils.toolbarEvent


class DealViewActivity : BaseViewActivity<ActivityDealBinding>()  {


    var mTitleMsg="用户协议"
    var mContent=R.string.user


    override fun setFullScreenWindow() {
        MyStatusBarUtil.setColor(this, Color.TRANSPARENT)
    }
    override fun getLayoutView(): Int = R.layout.activity_deal
    override fun initView() {
        when (intent.getIntExtra(Constants.SET_DEAL1, 0)) {
            1 -> {
                mTitleMsg="用户协议"
                mContent=R.string.user
            }
            2-> {
                mTitleMsg="隐私政策"
                mContent=R.string.privacy
            }
            3->{
                mTitleMsg="功能说明"
                mContent=R.string.shareText
            }

        }
        binding.privacyToolbar.setTitle(mTitleMsg)
        binding.text.text = PackageUtil.difPlatformName(this,mContent)
    }

    override fun initEvent() {
        binding. privacyToolbar.toolbarEvent(this) {}
    }

}