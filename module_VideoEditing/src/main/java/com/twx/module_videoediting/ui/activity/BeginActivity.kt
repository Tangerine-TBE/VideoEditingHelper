package com.twx.module_videoediting.ui.activity

import android.Manifest
import android.view.KeyEvent
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

class BeginActivity : BaseVmViewActivity<ActivityBeginBinding, BeginViewModel>() {

    val askAllPermissionLis= arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    private var showConut = 0
   /* private val mSplashHelper by lazy {
        SplashHelper(this, binding.mAdContainer, MainViewActivity::class.java)
    }*/
    private val mAgreementPopup by lazy {
        AgreementPopup(this)
    }

    override fun getViewModelClass(): Class<BeginViewModel> {
        return BeginViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_begin

    override fun initView() {
     /*   sp.putBoolean(Contents.NO_BACK, true)
       viewModel.loadAdMsg()*/
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        MyStatusBarUtil.fullScreenWindow(hasFocus, this)
        if (showConut < 1) {
            if (hasFocus) {
                sp.apply {
                    if (getBoolean(Constants.IS_FIRST, true)) {
                        mAgreementPopup?.showPopupView(binding.mAdContainer)
                        showConut++
                    } else {
                    //   mSplashHelper.showAd()
                        goHome()
                    }
                }
            }
        }
    }


    override fun initEvent() {
        mAgreementPopup.setOnActionClickListener(object : BasePopup.OnActionClickListener {
            override fun sure() {
                sp.putBoolean(Constants.SP_AGREE,true)
                initUm(this@BeginActivity)
                goHome()
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
      //  sp.putBoolean(Contents.NO_BACK, false)
        mAgreementPopup.dismiss()
    }

}