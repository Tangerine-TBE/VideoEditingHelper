package com.twx.module_videoediting.utils.video

import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.ugc.TXVideoEditer
import com.twx.module_base.utils.LogUtils
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
    private val mProgressObject = Any()
    private val mStateObject = Any()


    /**
     * 开始视频预览
     */
    fun startPlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
                if (editer != null) {
                    addPreviewListener()
                    editer?.startPlayFromTime(0, txVideoInfo.duration)
                    mCurrentState = PlayState.STATE_PLAY
                    notifyPlayerState(PlayState.STATE_PLAY)
                }
                isPreviewFinish = false
            }
        }
    }

    /**
     * 播放区间视频
     * @param startTime Long 开始时间
     * @param endTime Long  结束时间
     */
     fun startPlay(startTime: Long, endTime: Long) {
        mVideoEditerSDK.apply {
            if (editer != null) {
                addPreviewListener()
                editer?.startPlayFromTime(startTime, endTime)
                mCurrentState = PlayState.STATE_PLAY
                notifyPlayerState(PlayState.STATE_PLAY)
            }
            isPreviewFinish = false
        }

    }

    /**
     * 开始预览剪辑视频
     */
    fun startPlayCutTime() {
        mVideoEditerSDK.apply {
            addPreviewListener()
            if (editer != null) {
                editer.startPlayFromTime(0, txVideoInfo.duration)
                 notifyPlayerState(PlayState.STATE_PLAY)
            }
            mCurrentState = PlayState.STATE_PLAY
        }

    }

    /**
     * 停止视频预览
     */
    fun stopPlay() {
        mVideoEditerSDK?.apply {
            if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY || mCurrentState == PlayState.STATE_PREVIEW_AT_TIME || mCurrentState == PlayState.STATE_PAUSE) {
                editer?.stopPlay()
                removePreviewListener()
                notifyPlayerState(PlayState.STATE_STOP)
            }
            mCurrentState = PlayState.STATE_STOP
        }

    }

    fun resumePlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
                startPlay()
            } else {
                editer?.resumePlay()
                notifyPlayerState(PlayState.STATE_RESUME)
            }
            mCurrentState = PlayState.STATE_RESUME
        }

    }

    fun pausePlay() {
        mVideoEditerSDK.apply {
            if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
                editer?.pausePlay()
                notifyPlayerState(PlayState.STATE_PAUSE)
            }
            mCurrentState = PlayState.STATE_PAUSE
        }

    }

    /**
     * 重新预览视频
     */
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
        mVideoEditerSDK?.apply {
            pausePlay()
            isPreviewFinish = false
            editer?.previewAtTime(timeMs)
            mPreviewAtTime = timeMs
            LogUtils.i("------previewAtTime-------playmanager----------$timeMs------")
            mCurrentState = PlayState.STATE_PREVIEW_AT_TIME
        }
    }

    /**
     * 添加预览监听
     */
    private fun addPreviewListener() {
        mVideoEditerSDK.editer?.setTXVideoPreviewListener(this)
    }

    /**
     * 移除预览监听
     */
    private fun removePreviewListener() {
        mVideoEditerSDK.editer?.setTXVideoPreviewListener(null)
    }


    /**
     * 播放进度
     * @param time Int
     */
   override fun onPreviewProgress(time: Int) {
        // 转化为ms
        notifyPreviewProgress(time / 1000)
    }

    /**
     * 播放完成
     */
    override fun onPreviewFinished() {
        isPreviewFinish = true
        mCurrentState = PlayState.STATE_NONE
        notifyPlayerState(PlayState.STATE_NONE)
      //  restartPlay()
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
              /*  if ((mPreviewAtTime >= cutterEndTime || mPreviewAtTime <= cutterStartTime) && !isMotionFilter) {
                    startPlay(cutterStartTime, cutterEndTime)
                } else if (!VideoEditerSDK.getInstance().isReverse) {
                    startPlay(mPreviewAtTime, cutterEndTime)
                } else {
                    startPlay(cutterStartTime, mPreviewAtTime)
                }*/
                val totalDuration = txVideoInfo.duration
                LogUtils.i("--playVideo---$mPreviewAtTime-------$totalDuration----------")
                if (mPreviewAtTime >=totalDuration) {
                    startPlay(0, totalDuration)
                }else if (!isReverse)
                    startPlay(mPreviewAtTime, totalDuration)
                else {
                    startPlay(0, mPreviewAtTime)
                }
            }
        }

    }


    fun addOnPreviewListener(listener: OnPreviewListener) {
        synchronized(mProgressObject) { mProgressListenerList.add(listener) }
    }

    fun removeOnPreviewListener(listener: OnPreviewListener) {
        synchronized(mProgressObject) { mProgressListenerList.remove(listener) }
    }


    fun removeAllPreviewListener() {
        synchronized(mProgressObject) { mProgressListenerList.clear() }
    }


    private  fun notifyPreviewProgress(time: Int) {
        synchronized(mProgressObject) {
            mProgressListenerList.forEach {
                it.onPreviewProgress(time)
            }
        }
    }

   private fun notifyPreviewFinish() {
        synchronized(mProgressObject) {
            mProgressListenerList.forEach {
                it.onPreviewFinish()
            }
        }
    }


    /**
     * 添加播放状态监听
     * @param listener OnPlayStateListener
     */
    fun addOnPlayStateListener(listener: OnPlayStateListener) {
        synchronized(mStateObject) { mStateListenerList.add(listener) }
    }

    /**
     * 移除播放状态监听
     * @param listener OnPlayStateListener
     */
    fun removeOnPlayStateListener(listener: OnPlayStateListener) {
        synchronized(mStateObject) { mStateListenerList.remove(listener) }
    }

    /**
     * 移除所有播放状态监听
     */
    fun removeAllPlayStateListener() {
        synchronized(mStateObject) { mStateListenerList.clear() }
    }

    /**
     * 开始播放通知
     */
   private  fun notifyPlayerState(state:Int) {
        synchronized(mStateObject) {
            mStateListenerList.forEach {
                it.onPlayState(state)
            }
        }
    }

    /**
     * 获取当前播放状态
     * @return Int
     */
    fun getCurrentState(): Int {
        return mCurrentState
    }

    interface OnPlayStateListener {
        fun onPlayState(state:Int)
    }

    interface OnPreviewListener {
        fun onPreviewProgress(time: Int)
        fun onPreviewFinish()
    }


}