package com.twx.module_videoediting.utils

import android.app.Activity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.VideoGenerateKit
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.toOtherActivity
import com.twx.module_base.widget.popup.LoadingPopup
import com.twx.module_videoediting.ui.activity.ExportActivity
import com.twx.module_videoediting.ui.widget.TitleBar
import com.twx.module_videoediting.ui.widget.video.cut.IVideoCut
import io.microshow.rxffmpeg.RxFFmpegInvoke


fun TitleBar.setBarEventAction(activity: Activity?, block: () -> Unit){
    setOnBarActionListener(object : TitleBar.OnBarActionListener {
        override fun backAction() {
            activity?.finish()
        }

        override fun rightAction() {
            block()
        }

    })
}

fun formatVideoTime(timeTemp: Long): String {
    val second = timeTemp % 60
    val minuteTemp = timeTemp / 60
    return if (minuteTemp > 0) {
        val minute = minuteTemp % 60
        val hour = minuteTemp / 60
        if (hour > 0) {
            ((if (hour > 10) hour.toString() + "" else "0$hour") + ":" + (if (minute >= 10) minute.toString() + "" else "0$minute")
                    + ":" + if (second >= 10) second.toString() + "" else "0$second")
        } else {
            ("00:" + (if (minute >= 10) minute.toString() + "" else "0$minute") + ":"
                    + if (second >= 10) second.toString() + "" else "0$second")
        }
    } else {
        "00:00:" + if (second >= 10) second.toString() + "" else "0$second"
    }
}


fun formatDuration(durationMs: Long): String {
    val duration = durationMs / 1000
    val h = duration / 3600
    val m = (duration - h * 3600) / 60
    val s = duration - (h * 3600 + m * 60)
    return if (h == 0L) {
        DateTimeUtil.asTwoDigit(m) + ":" + DateTimeUtil.asTwoDigit(s)
    } else {
        DateTimeUtil.asTwoDigit(h) + ":" + DateTimeUtil.asTwoDigit(m) + ":" + DateTimeUtil.asTwoDigit(s)
    }
}


fun videoTimeInterval(duration: Long):Int{
    val time = duration / 1000/ 30
    return if (duration / 1000<20) {
            1000
    } else {
        if (time > 0) {
            time.toInt()*2000
        } else {
            2000
        }
    }
}

//输出视频
fun outPutVideo(loadingPopup: LoadingPopup, activity: Activity?)=
    object: IVideoCut.OnCutListener{
        override fun onCutterCompleted(ugcKitResult: UGCKitResult) {
            if (ugcKitResult.errorCode == 0) {
                LogUtils.i("-------onCutterCompleted----------------${ugcKitResult.outputPath}--")
                toOtherActivity<ExportActivity>(activity, true){
                    putExtra(Constants.KEY_VIDEO_PATH, ugcKitResult.outputPath)
                }
                loadingPopup.dismiss()
                MakeBackLiveData.setMakeFinishState(true)
            }
        }
        override fun onCutterCanceled() {
            MakeBackLiveData.setMakeFinishState(true)
        }
        override fun onCutterProgress(progress: Float) {
            LogUtils.i("-----onCutterProgress---${Thread.currentThread().name}----------${(progress * 100).toInt()}-------------")
            loadingPopup.setProgress((progress * 100).toInt())
            MakeBackLiveData.setMakeFinishState(false)
        }
}

//取消制作
fun cancelMake(isProcess: Boolean) {
    if (isProcess) {
        ProcessKit.getInstance().stopProcess()
    } else {
        VideoGenerateKit.getInstance().stopGenerate()
    }
    MakeBackLiveData.setMakeFinishState(true)
}


fun ffCallback(onProgress: (Int) -> Unit = {}, onComplete: () -> Unit = {}, onCancel: () -> Unit = {})=
    object : RxFFmpegInvoke.IFFmpegListener{
        override fun onFinish() {
            LogUtils.i("-ffCallback--onFinish-------------- ")
              onComplete()
              MakeBackLiveData.setMakeFinishState(true)
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            LogUtils.i("-ffCallback--onProgress--------------     $progress  ")
            MakeBackLiveData.setMakeFinishState(false)
            BaseApplication.mHandler.post {
                if (progress>=0){
                    onProgress(progress)
                }
            }
        }

        override fun onCancel() {
            LogUtils.i("-ffCallback--onCancel-------------- ")
            onCancel()
            MakeBackLiveData.setMakeFinishState(true)
        }

        override fun onError(message: String?) {
            LogUtils.i("--ffCallback-onError--------------     $message  ")
            MakeBackLiveData.setMakeFinishState(true)
        }

    }

      fun <T>String.formatList(): T = Gson().fromJson(this, object : TypeToken<List<T>>(){}.type)


