package com.twx.module_videoediting.ui.activity

import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReverseBinding
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction

class ReverseActivity : BaseViewActivity<ActivityReverseBinding>() {


    override fun getLayoutView(): Int = R.layout.activity_reverse


    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, reverseContainer)
            setStatusBarDistance(this@ReverseActivity, reverseTitleBar, LayoutType.CONSTRAINTLAYOUT)
            lifecycle.addObserver(mTWVideoReverseContainer.getPlayerView())
        }
    }


    override fun initEvent() {
        binding.apply {
            mTWVideoReverseContainer.setOnCutListener(outPutVideo(loadingPopup,this@ReverseActivity))
            reverseTitleBar.setBarEventAction(this@ReverseActivity){
                mTWVideoReverseContainer.startExportVideo()
                loadingPopup.showPopupView(reverseContainer)
            }

            loadingPopup.cancelMake {
                cancelMake(false)
            }


        }

    }

}