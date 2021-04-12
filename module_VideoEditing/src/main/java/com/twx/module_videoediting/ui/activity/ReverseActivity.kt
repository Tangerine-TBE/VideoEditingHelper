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
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ReverseViewModel

class ReverseActivity : BaseVmViewActivity<ActivityReverseBinding, ReverseViewModel>() {

    override fun getViewModelClass(): Class<ReverseViewModel> {
        return ReverseViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_reverse


    private val mOnCutListener by lazy {
        object: IVideoCut.OnCutListener{
            override fun onCutterCompleted(ugcKitResult: UGCKitResult) {
                if (ugcKitResult.errorCode == 0) {
                    LogUtils.i("-------onCutterCompleted----------------${ugcKitResult.outputPath}--")
                    loadingPopup.dismiss()
                    toOtherActivity<ExportActivity>(this@ReverseActivity,true){
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
            mTWVideoReverseContainer.setOnCutListener(mOnCutListener)
            reverseTitleBar.setBarEventAction(this@ReverseActivity){
                mTWVideoReverseContainer.startExportVideo()
                loadingPopup.showPopupView(reverseContainer)
            }

            loadingPopup.cancelMake {
                mTWVideoReverseContainer.stopExportVideo()
            }


        }

    }

}