package com.twx.module_videoediting.ui.activity

import android.view.KeyEvent
import com.example.module_ad.ad.ad_help.AdController
import com.example.module_ad.advertisement.AdType
import com.example.module_ad.utils.Contents
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.base.BasePopup
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.MyStatusBarUtil
import com.twx.module_base.utils.initUm
import com.twx.module_base.utils.toOtherActivity
import com.twx.module_base.widget.popup.AgreementPopup
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityBeginBinding
import com.twx.module_videoediting.viewmodel.BeginViewModel
import com.umeng.analytics.MobclickAgent

class BeginActivity : BaseVmViewActivity<ActivityBeginBinding, BeginViewModel>() {


    private var showCount = 0

    private val mAdController by lazy {
        AdController.Builder(this)
            .setPage(AdType.START_PAGE)
            .setContainer(hashMapOf(AdController.ContainerType.TYPE_SPLASH to  binding.mAdContainer))
            .create()
            .setSplashAction { toOtherActivity<MainViewActivity>(this,true){} }
    }


    private val mAgreementPopup by lazy {
        AgreementPopup(this)
    }

    override fun getViewModelClass(): Class<BeginViewModel> {
        return BeginViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_begin

    override fun initView() {
        sp.putBoolean(Contents.NO_BACK, true)
       viewModel.loadAdMsg()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        MyStatusBarUtil.fullScreenWindow(hasFocus, this)
        if (showCount < 1) {
            if (hasFocus) {
                sp.apply {
                    if (getBoolean(Constants.IS_FIRST, true)) {
                        mAgreementPopup?.showPopupView(binding.mAdContainer)
                        showCount++
                    } else {
                      mAdController.show()
                     //   goHome()
                    }
                }
            }
        }
    }


    override fun initEvent() {
        mAgreementPopup.setOnActionClickListener(object : BasePopup.OnActionClickListener {
            override fun sure() {
                sp.putBoolean(Constants.SP_AGREE,true)
                initUm(BaseApplication.application)
                MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
                // goHome()
                mAdController.show()
            }

            override fun cancel() {
                finish()
            }
        })
    }

    fun goHome() {
        toOtherActivity<MainViewActivity>(this@BeginActivity, true) {}
    }

    //禁用返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        if (keyCode == KeyEvent.KEYCODE_BACK) true
        else super.onKeyDown(keyCode, event)


    override fun release() {
       sp.putBoolean(Contents.NO_BACK, false)
        mAgreementPopup.dismiss()
    }

}