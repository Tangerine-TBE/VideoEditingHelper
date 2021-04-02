package com.twx.module_videoediting.utils.video

import android.graphics.Bitmap
import android.util.Log
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.qcloud.ugckit.basic.BaseGenerateKit
import com.tencent.rtmp.TXLog
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXThumbnail
import com.tencent.ugc.TXVideoEditer
import com.twx.module_base.utils.LogUtils

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/1 19:13:38
 * @class describe
 */
object ProcessManager: BaseGenerateKit(),IProcess, TXVideoEditer.TXThumbnailListener, TXVideoEditer.TXVideoProcessListener {
    private const val TAG = "ProcessManager"
    /**
     * 开始预处理视频
     * FIXBUG：缩略图getThumbnail和processVideo同时使用时，会有两个进度条，一个是视频预处理的进度条，一个是缩略图生成进度的进度条。
     * 缩略图setThumbnail和processVideo同时使用，只有一个进度条。
     * <p>
     * 1.录制进入编辑：缩略图使用预处理的进度条
     * 2.裁剪进入编辑：使用预处理的进度条，此时使用getThumbnail会产生两个进度条，进度条会来回闪烁
     */
    override fun startProcess() {
        VideoEditorsManager.clearThumbnails()
        val cutterStartTime = VideoEditorsManager.getCutterStartTime()
        val cutterEndTime = VideoEditorsManager.getCutterEndTime()
        val thumbnailCount = (cutterEndTime - cutterStartTime).toInt() / 1000
        LogUtils.i("startProcess----------$cutterStartTime----------$cutterEndTime-----------------$thumbnailCount")

        val thumbnail = TXThumbnail()
        thumbnail.count = thumbnailCount
        thumbnail.width = 100
        thumbnail.height = 100

        val editer = VideoEditorsManager.getEditer()
        if (editer != null) {
            TXCLog.i(TAG, "[UGCKit][VideoProcess]generate thumbnail start time:$cutterStartTime,end time:$cutterEndTime,thumbnail count:$thumbnailCount")
            editer.setThumbnail(thumbnail)
            editer.setThumbnailListener(this)
            editer.setVideoProcessListener(this)
            editer.setCutFromTime(cutterStartTime, cutterEndTime)
            TXCLog.i(TAG, "[UGCKit][VideoProcess]generate video start time:$cutterStartTime,end time:$cutterEndTime")
            editer.processVideo()
        }
    }
    /**
     * 停止预处理视频包括一些异常操作导致的合成取消
     */
    override fun stopProcess() {
        Log.d(TAG, "stopProcess")
        //FIXBUG:如果上一次如生成缩略图没有停止，先停止，在进行下一次生成
        val editer = VideoEditorsManager.getEditer()
        if (editer != null) {
            editer.cancel()
            editer.setVideoProcessListener(null)
        }
        mOnUpdateUIListener?.onUICancel()

    }

    override fun onThumbnail(index: Int, timeMs: Long, bitmap: Bitmap) {
        TXLog.d(TAG, "onThumbnail index:$index,timeMs:$timeMs")
        VideoEditorsManager.addThumbnailBitmap(timeMs, bitmap)
    }

    override fun onProcessProgress(progress: Float) {
        mOnUpdateUIListener?.onUIProgress(progress)
    }

    override fun onProcessComplete(result: TXVideoEditConstants.TXGenerateResult) {
        TXLog.d(TAG, "onProcessComplete")
        val editer = VideoEditorsManager.getEditer()
        editer?.setVideoProcessListener(null)
        mOnUpdateUIListener?.onUIComplete(result.retCode, result.descMsg)

    }
}