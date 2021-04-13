package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReverseBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.widget.video.cut.IVideoCut
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ReverseViewModel

class ReverseActivity : BaseVmViewActivity<ActivityReverseBinding, ReverseViewModel>() {

    override fun getViewModelClass(): Class<ReverseViewModel> {
        return ReverseViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_reverse


    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@ReverseActivity, reverseTitleBar, LayoutType.CONSTRAINTLAYOUT)
            lifecycle.addObserver(mTWVideoReverseContainer.getPlayerView())
        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@ReverseActivity, {
                    reverseTitleBar.setThemeChange(it)
                    viewThemeColor(it, reverseContainer)
                })

            }
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