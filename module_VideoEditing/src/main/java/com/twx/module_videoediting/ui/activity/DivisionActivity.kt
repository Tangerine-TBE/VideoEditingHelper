package com.twx.module_videoediting.ui.activity

import android.content.Intent
import android.text.TextUtils
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityDivisionBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.*
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.viewmodel.VideoCutViewModel
import io.microshow.rxffmpeg.RxFFmpegInvoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DivisionActivity : BaseViewActivity<ActivityDivisionBinding>() {

    override fun getLayoutView(): Int = R.layout.activity_division


    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, divisionContainer)
            setStatusBarDistance(
                this@DivisionActivity,
                divisionTitleBar,
                LayoutType.CONSTRAINTLAYOUT
            )

            mVideoDivisionContainer.initPlayerLayout()
            lifecycle.addObserver(mVideoDivisionContainer.getPlayerView())
        }

        MakeBackLiveData.observe(this@DivisionActivity, {
            if (it) loadingPopup.dismiss()
        })

    }



    private var outPathOne=""
    private var outPathTwo=""

    override fun initEvent() {
        binding.apply {
            divisionTitleBar.setBarEventAction(this@DivisionActivity){
                for (i in 0 until 2){
                    if (i == 0) {
                            outPathOne = VideoPathUtil.generateVideoPath2(i)
                            divisionVideo(0L, mVideoDivisionContainer.getCurrentTime(), outPathOne)
                        } else {
                            outPathTwo = VideoPathUtil.generateVideoPath2(i)
                            divisionVideo(mVideoDivisionContainer.getCurrentTime(), mVideoDivisionContainer.getTotalTime(), outPathTwo)
                    }
                }
                loadingPopup.showPopupView(divisionContainer)
            }

            loadingPopup.cancelMake {
                FFmpegHelper.exitCommand()
            }

        }
    }


    private  fun divisionVideo(beginTime:Long,endTime:Long,outPath:String){
        binding.apply {
            mScope.launch(Dispatchers.IO) {
                FFmpegHelper.startCommand(FFmpegHelper.divisionVideo(beginTime, endTime, mVideoDivisionContainer.getVideoPath(), outPath), object : RxFFmpegInvoke.IFFmpegListener {
                    override fun onFinish() {
                        LogUtils.i("----FFmpegHelper----------onFinish-------")
                        if (!TextUtils.isEmpty(outPathOne) || !TextUtils.isEmpty(outPathTwo)) {
                            FileUtils.saveAlbum(this@DivisionActivity, outPathOne)
                            FileUtils.saveAlbum(this@DivisionActivity, outPathTwo)
                            MainViewActivity.toFileFragment(this@DivisionActivity)
                        }
                        MakeBackLiveData.setMakeFinishState(true)
                    }

                    override fun onProgress(progress: Int, progressTime: Long) {
                        mScope.launch(Dispatchers.Main) {
                            loadingPopup.setProgress(progress)
                        }
                        MakeBackLiveData.setMakeFinishState(false)
                        LogUtils.i("------FFmpegHelper--------onProgress-----$progress--$progressTime")
                    }

                    override fun onCancel() {
                        if (!TextUtils.isEmpty(outPathOne) || !TextUtils.isEmpty(outPathTwo)) {
                            FileUtils.deleteFile(File(outPathOne))
                            FileUtils.deleteFile(File(outPathTwo))
                        }
                        MakeBackLiveData.setMakeFinishState(true)
                    }

                    override fun onError(message: String?) {
                        MakeBackLiveData.setMakeFinishState(true)
                        LogUtils.i("---FFmpegHelper-----------onError-------")
                    }
                })
            }
        }
    }

    override fun release() {
        super.release()
        mJob.cancel()
        FFmpegHelper.exitCommand()
    }

}