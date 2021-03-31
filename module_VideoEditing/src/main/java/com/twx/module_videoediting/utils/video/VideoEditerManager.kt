package com.twx.module_videoediting.utils.video

import android.graphics.Bitmap
import android.util.Log
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.DraftEditer
import com.tencent.qcloud.ugckit.module.effect.utils.EffectEditer
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXVideoInfo
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoEditer.TXThumbnailListener
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.utils.LogUtils

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/31 14:05:02
 * @class describe
 */
object VideoEditerManager {

    private  var mCutterDuration=0L // 裁剪的总时长
    private  var mCutterStartTime =0L// 裁剪开始的时间
    private  var mCutterEndTime=0L// 裁剪结束的时间

    private var mVideoEditer:TXVideoEditer?=null
    private var mTXVideoInfo: TXVideoInfo? = null

    private var mVideoPath: String? = null
    private var mPublishFlag = false
    private var mIsReverse = false


    /**
     * 设置视频信息
     * @param info TXVideoInfo
     */
    fun setVideoInfo(info: TXVideoInfo){
        mTXVideoInfo=info
    }

    /**
     * 获取视频的信息
     * FIXBUG：不能判断是否为空，如果更换频路径，
     * @return
     */
    fun getTXVideoInfo(): TXVideoInfo? {
        mTXVideoInfo = TXVideoInfoReader.getInstance(BaseApplication.application).getVideoFileInfo(mVideoPath)
        return mTXVideoInfo
    }


    /**
     * 设置视频路径
     * @param videoPath String
     */
    fun setVideoPath(videoPath: String?){
        mVideoPath=videoPath
    }


    fun clear(){
        mVideoEditer?.setTXVideoPreviewListener(null)
        mVideoEditer = null

        mCutterDuration = 0
        mCutterStartTime = 0
        mCutterEndTime = 0

        mThumbnailList.clear()

        DraftEditer.getInstance().clear()
        EffectEditer.getInstance().clear()

        synchronized(mPreviewWrapperList) { mPreviewWrapperList.clear() }
        mIsReverse = false
    }

    /**
     * 初始化新的TXVideoEditer
     */
    fun initSDK() {
        mVideoEditer = TXVideoEditer(BaseApplication.application)
    }

    /**
     * 释放XVideoEditer
     */
    fun releaseSDK() {
        mVideoEditer?.release()
    }

    /**
     * 获取以前创建的TXVideoEditer
     *
     * @return
     */
    fun getEditer(): TXVideoEditer? {
        return mVideoEditer
    }

    /**
     * 裁剪后的时间
     * @param newVideoDuration
     */
    fun setCutterDuration(newVideoDuration: Long) {
        mCutterDuration = newVideoDuration
    }
    /**
     * 获取裁剪后的时间
     *
     * @return
     */
    fun geCutterDuration(): Long {
        return mCutterDuration
    }

    fun setCutterStartTime(startTime: Long, endTime: Long) {
        mCutterStartTime = startTime
        mCutterEndTime = endTime
        mCutterDuration = endTime - startTime
    }
    fun getCutterStartTime(): Long {
        return mCutterStartTime
    }

    fun getCutterEndTime(): Long {
        return mCutterEndTime
    }

    /**
     * ======================================================预览相关======================================================
     */
    /**
     * 预览相关
     * 由于SDK没有提供多个Listener的预览进度的回调，所以在上层包装一下
     */
    private var mPreviewWrapperList: MutableList<TXVideoPreviewListenerWrapper> = ArrayList()

    fun setReverse(isReverse: Boolean) {
        mIsReverse = isReverse
    }

    fun isReverse(): Boolean {
        return mIsReverse
    }
    private val mPreviewListener: TXVideoEditer.TXVideoPreviewListener = object : TXVideoEditer.TXVideoPreviewListener {
        override fun onPreviewProgress(time: Int) {
            synchronized(mPreviewWrapperList) {
                for (wrapper in mPreviewWrapperList) {
                    wrapper.onPreviewProgressWrapper((time / 1000))
                }
            }
        }

        override fun onPreviewFinished() {
            synchronized(mPreviewWrapperList) {
                for (wrapper in mPreviewWrapperList) {
                    wrapper.onPreviewFinishedWrapper()
                }
            }
        }
    }

    fun addTXVideoPreviewListenerWrapper(listener: TXVideoPreviewListenerWrapper?) {
        synchronized(mPreviewWrapperList) {
            if (mPreviewWrapperList.contains(listener)) {
                return
            }
            mPreviewWrapperList.add(listener!!)
        }
    }

    fun removeTXVideoPreviewListenerWrapper(listener: TXVideoPreviewListenerWrapper?) {
        synchronized(mPreviewWrapperList) { mPreviewWrapperList.remove(listener) }
    }

    fun constructVideoInfo(videoInfo: TXVideoInfo, duration: Long) {
        videoInfo.width = 100
        videoInfo.height = 100
        videoInfo.duration = duration
        mTXVideoInfo = videoInfo
    }

    fun resetDuration() {
        if (mCutterEndTime - mCutterStartTime != 0L) {
            mCutterDuration = mCutterEndTime - mCutterStartTime
            mCutterStartTime = 0
            mCutterEndTime = mCutterDuration
        } else {
            val videoInfo = getTXVideoInfo()
            if (videoInfo != null) {
                mCutterDuration = videoInfo.duration
            }
        }
        mVideoEditer?.setCutFromTime(0, mCutterDuration)
    }

    fun getVideoDuration(): Long {
        return mCutterDuration
    }


    /**
     * ======================================================缩略图相关======================================================
     */

    /**
     * 缩略图相关
     */
    private val mThumbnailList  : MutableList<ThumbnailBitmapInfo> = ArrayList()// 将已经加在好的Bitmap缓存起来

    /**
     * 初始化缩略图
     *
     * @param listener
     * @param interval 缩略图的时间间隔
     */
    fun getThumbnailList(listener: TXThumbnailListener?, interval: Int) {
        if (interval == 0) {
            return
        }
         getTXVideoInfo()?.let {
            mVideoEditer?.apply {
                setRenderRotation(0)
                // FIXBUG：获取缩略图之前需要设置缩略图的开始和结束时间点，SDK内部会根据开始时间和结束时间出缩略图
                setCutFromTime(0, it.duration)
                getThumbnail( (it.duration/ interval).toInt(), IVideoCutLayout.DEFAULT_THUMBNAIL_WIDTH, IVideoCutLayout.DEFAULT_THUMBNAIL_HEIGHT, true, listener)
            }
        }
    }

    fun setPublishFlag(flag: Boolean) {
        mPublishFlag = flag
    }

    fun isPublish(): Boolean {
        return mPublishFlag
    }

    /**
     * 在特效页面设置特效，会调用SDK，特效点击"取消"后，还原设置进入SDK的特效
     */
    fun restore() {
        val effectEditer = EffectEditer.getInstance()
        mVideoEditer?.apply {
            setBGM(effectEditer.bgmPath)
            setBGMVolume(effectEditer.bgmVolume)
            setVideoVolume(effectEditer.videoVolume)
        }
    }

    /**
     * 由于SDK没有提供多个界面的预览进度的回调，所以在上层包装一下
     */
    interface TXVideoPreviewListenerWrapper {
        /**
         * @param time
         */
        fun onPreviewProgressWrapper(time: Int)
        fun onPreviewFinishedWrapper()
    }

    /**
     * 获取已经加载的缩略图
     *
     * @return
     */
    fun getThumbnailList(startPts: Long, endPts: Long): List<Bitmap> {
        val list: MutableList<Bitmap> = java.util.ArrayList()
        for ((ptsMs, bitmap) in mThumbnailList) {
            if (ptsMs >= startPts && ptsMs <= endPts) {
                list.add(bitmap)
            }
        }
        return list
    }

    fun getAllThumbnails(): List<Bitmap> {
        return getThumbnailList(0, mTXVideoInfo!!.duration)
    }

    fun addThumbnailBitmap(timeMs: Long, bitmap: Bitmap) {
        mThumbnailList.add(ThumbnailBitmapInfo(timeMs, bitmap))
    }

    /**
     * 清空缩略图列表
     */
    fun clearThumbnails() {
        mThumbnailList.clear()
    }

    fun getThumbnailSize(): Int {
        return mThumbnailList?.size ?: 0
    }


    data class ThumbnailBitmapInfo(var ptsMs: Long, var bitmap: Bitmap)
}