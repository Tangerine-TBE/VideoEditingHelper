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
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class VideoCutActivity : BaseVmViewActivity<ActivityVidelCutBinding, VideoCutViewModel>() {


    private val mOnCutListener by lazy {
        object: IVideoCut.OnCutListener{
            override fun onCutterCompleted(ugcKitResult: UGCKitResult) {
                if (ugcKitResult.errorCode == 0) {
                    LogUtils.i("-------onCutterCompleted----------------${ugcKitResult.outputPath}--")
                    toOtherActivity<ExportActivity>(this@VideoCutActivity,true){
                        putExtra(Constants.KEY_VIDEO_PATH,ugcKitResult.outputPath)
                    }
                }
            }
            override fun onCutterCanceled() {

            }
            override fun onCutterProgress(progress: Float) {
                LogUtils.i("-----onCutterProgress---${Thread.currentThread().name}----------${(progress * 100).toInt()}-------------")
                loadingPopup.setProgress((progress*100).toInt())
            }
        }
    }

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
            mTWVideoCutContainer.setOnCutListener(mOnCutListener)
            cutTitleBar.setBarEventAction(this@VideoCutActivity) {
                mTWVideoCutContainer.startExportVideo()
                loadingPopup.showPopupView(videoCutContainer)
            }

            loadingPopup.cancelMake {
                mTWVideoCutContainer.stopExportVideo()
            }

        }
    }





}

