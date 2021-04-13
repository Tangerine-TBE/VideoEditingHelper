package com.twx.module_videoediting.ui.activity

import android.content.Intent
import com.tencent.liteav.demo.videoediter.TCVideoEditerActivity
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityVidelCutBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.widget.video.cut.IVideoCut
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class VideoCutActivity : BaseVmViewActivity<ActivityVidelCutBinding, VideoCutViewModel>() {




    override fun getViewModelClass(): Class<VideoCutViewModel> {
        return VideoCutViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_videl_cut

    override fun initView() {
        binding.apply {
            data=viewModel
            setStatusBarDistance(this@VideoCutActivity, cutTitleBar, LayoutType.CONSTRAINTLAYOUT)
            val videoPath = intent.getStringExtra(Constants.KEY_VIDEO_PATH)
            mTWVideoCutContainer.setVideoPath(videoPath)
            lifecycle.addObserver(mTWVideoCutContainer)
        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@VideoCutActivity, {
                    cutTitleBar.setThemeChange(it)
                    viewThemeColor(it, videoCutContainer)
                })

            }
        }
    }


    override fun initEvent() {
        binding.apply {
            mTWVideoCutContainer.setOnCutListener(outPutVideo(loadingPopup,this@VideoCutActivity))
            cutTitleBar.setBarEventAction(this@VideoCutActivity) {
                mTWVideoCutContainer.startExportVideo()
                loadingPopup.showPopupView(videoCutContainer)
            }

            loadingPopup.cancelMake {
                cancelMake(false)
            }

        }
    }





}

