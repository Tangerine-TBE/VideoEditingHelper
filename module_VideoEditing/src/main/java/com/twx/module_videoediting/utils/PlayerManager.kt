package com.twx.module_videoediting.utils

import android.util.Log
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.qcloud.ugckit.module.PlayerManagerKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.ugc.TXVideoEditer
import java.util.*

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 16:31:03
 * @class describe
 */
object PlayerManager : TXVideoEditer.TXVideoPreviewListener {

    private var mCurrentState = PlayState.STATE_NONE
    private var mPreviewAtTime: Long = 0
    var isPreviewFinish = false
    private var mProgressListenerList: MutableList<OnPreviewListener> = ArrayList()
    private var mStateListenerList: MutableList<OnPlayStateListener> =  ArrayList()
    private val mVideoEditerSDK by lazy {
        VideoEditerSDK.getInstance()
    }


    /**
     * 开始视频预览
     */
    fun startPlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
                if (editer != null) {
                    addPreviewListener()
                    editer.startPlayFromTime(0, txVideoInfo.duration)
                    mCurrentState = PlayState.STATE_PLAY
                    notifyStart()
                }
                isPreviewFinish = false
            }
        }
    }


    fun startPlayCutTime() {
        mVideoEditerSDK.apply {
            addPreviewListener()
            if (editer != null) {
                editer.startPlayFromTime(0, txVideoInfo.duration)
                notifyStart()
            }
            mCurrentState = PlayState.STATE_PLAY
        }

    }

    /**
     * 停止视频预览
     */
    fun stopPlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY || mCurrentState == PlayState.STATE_PREVIEW_AT_TIME || mCurrentState == PlayState.STATE_PAUSE) {
                editer.stopPlay()
                removePreviewListener()
                notifyStop()
            }
            mCurrentState = PlayState.STATE_STOP
        }

    }

    fun resumePlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
                startPlay()
            } else {
                editer.resumePlay()
                notifyResume()
            }
            mCurrentState = PlayState.STATE_RESUME
        }

    }

    fun pausePlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
                editer.pausePlay()
                notifyPause()
            }
            mCurrentState = PlayState.STATE_PAUSE
        }

    }

    fun restartPlay() {
        stopPlay()
        startPlay()
    }

    /**
     * 调用mTXVideoEditer.previewAtTime后，需要记录当前时间，下次播放时从当前时间开始
     *
     * @param timeMs
     */
    fun previewAtTime(timeMs: Long) {
        mVideoEditerSDK.apply {
            pausePlay()
            isPreviewFinish = false
            editer.previewAtTime(timeMs)
            mPreviewAtTime = timeMs
            mCurrentState = PlayState.STATE_PREVIEW_AT_TIME
        }

    }

    fun addPreviewListener() {
        mVideoEditerSDK.editer.setTXVideoPreviewListener(this)
    }

    fun removePreviewListener() {
        mVideoEditerSDK.editer.setTXVideoPreviewListener(null)
    }

   override fun onPreviewProgress(time: Int) {
        // 转化为ms
        notifyPreviewProgress(time / 1000)
    }

    override fun onPreviewFinished() {
        isPreviewFinish = true
        mCurrentState = PlayState.STATE_NONE
        restartPlay()
        notifyPreviewFinish()
    }

    fun playVideo(isMotionFilter: Boolean) {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
                startPlay()
            } else if ((mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) && !isMotionFilter) {
                pausePlay()
            } else if (mCurrentState == PlayState.STATE_PAUSE) {
                resumePlay()
            } else if (mCurrentState == PlayState.STATE_PREVIEW_AT_TIME) {
                if ((mPreviewAtTime >= cutterEndTime || mPreviewAtTime <= cutterStartTime) && !isMotionFilter) {
                    startPlay(cutterStartTime, cutterEndTime)
                } else if (!VideoEditerSDK.getInstance().isReverse) {
                    startPlay(mPreviewAtTime, cutterEndTime)
                } else {
                    startPlay(cutterStartTime, mPreviewAtTime)
                }
            }
        }

    }

    fun startPlay(startTime: Long, endTime: Long) {
        mVideoEditerSDK.apply {
            if (editer != null) {
                addPreviewListener()
                editer.startPlayFromTime(startTime, endTime)
                mCurrentState = PlayState.STATE_PLAY
            }
            isPreviewFinish = false
        }

    }

    fun addOnPreviewListener(listener: OnPreviewListener) {
    mProgressListenerList.add(listener)
    }

    fun removeOnPreviewListener(listener:OnPreviewListener) {
       mProgressListenerList.remove(listener)
    }

    fun removeAllPreviewListener() {
     mProgressListenerList.clear()
    }

    fun notifyPreviewProgress(time: Int) {
            mProgressListenerList.forEach {
                it.onPreviewProgress(time)
        }
    }

    fun notifyPreviewFinish() {
            mProgressListenerList.forEach {
                it.onPreviewFinish()
            }
    }

    fun addOnPlayStateListener(listener: OnPlayStateListener) {
      mStateListenerList.add(listener)
    }

    fun removeOnPlayStateListener(listener: PlayerManager.OnPlayStateListener) {
        mStateListenerList.remove(listener)
    }

    fun removeAllPlayStateListener() {
      mStateListenerList.clear()
    }

    fun notifyStart() {
            mStateListenerList.forEach {
                it.onPlayStateStart()
            }
    }

    fun notifyStop() {
            mStateListenerList.forEach {
                it.onPlayStateStop()
            }
    }

    fun notifyResume() {
            mStateListenerList.forEach {
                it.onPlayStateResume()
            }
    }

    fun notifyPause() {
            mStateListenerList.forEach {
                it.onPlayStatePause()
            }
    }

    fun getCurrentState(): Int {
        return mCurrentState
    }

    interface OnPlayStateListener {
        fun onPlayStateStart()
        fun onPlayStateResume()
        fun onPlayStatePause()
        fun onPlayStateStop()
    }

    interface OnPreviewListener {
        fun onPreviewProgress(time: Int)
        fun onPreviewFinish()
    }


}