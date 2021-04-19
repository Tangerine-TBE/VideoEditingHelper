package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.VideoGenerateKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_videoediting.ui.widget.video.cut.IVideoCut
import com.twx.module_videoediting.utils.video.PlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 10:23:56
 * @class describe
 */
open  class BaseVideoEditUi @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),LifecycleObserver {
    protected val mVideoEditorHelper by lazy {
        VideoEditerSDK.getInstance()

    }

    protected val mJob = Job()
    protected var mScope = CoroutineScope(mJob)

     fun startExportVideo() {
        PlayerManager.stopPlay()
        //如果图片没有加载完，先停止加载
        ProcessKit.getInstance().stopProcess()
        VideoGenerateKit.getInstance().startGenerate()
    }

     fun stopExportVideo() {
        VideoGenerateKit.getInstance().stopGenerate()
    }


   open fun initPlayerLayout() {

    }


     fun setOnCutListener(listener: IVideoCut.OnCutListener?) {
        if (listener == null) {
            ProcessKit.getInstance().setOnUpdateUIListener(null)
            VideoGenerateKit.getInstance().setOnUpdateUIListener(null)
            return
        }
        // 裁剪后输出视频
        VideoGenerateKit.getInstance().setOnUpdateUIListener(object : OnUpdateUIListener {
            override fun onUIProgress(progress: Float) {
                listener.onCutterProgress(progress)
            }

            override fun onUIComplete(retCode: Int, descMsg: String) {
                if (listener != null) {
                    val ugcKitResult = UGCKitResult()
                    ugcKitResult.errorCode = retCode
                    ugcKitResult.descMsg = descMsg
                    ugcKitResult.outputPath = VideoGenerateKit.getInstance().videoOutputPath
                    ugcKitResult.coverPath = VideoGenerateKit.getInstance().coverPath
                    listener.onCutterCompleted(ugcKitResult)
                }
            }

            override fun onUICancel() {
                if (listener != null) {
                    listener.onCutterCanceled()
                }
            }
        })
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startPlay() {
        PlayerManager.startPlay()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopPlay() {
        PlayerManager.stopPlay()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun release(){
    //    mVideoEditorHelper.releaseSDK()
    //    mVideoEditorHelper.clear()
    }

}