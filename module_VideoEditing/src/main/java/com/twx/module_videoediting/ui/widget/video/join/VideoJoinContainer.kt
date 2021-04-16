package com.twx.module_videoediting.ui.widget.video.join

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.DialogUtil
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXPreviewParam
import com.tencent.ugc.TXVideoJoiner
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoJoinContainerBinding

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/15 16:14:54
 * @class describe
 */
class VideoJoinContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),IVideoJoin,TXVideoJoiner.TXVideoPreviewListener,LifecycleObserver,
    TXVideoJoiner.TXVideoJoinerListener {
    private var mCurrentState = PlayState.STATE_NONE // 播放器当前状态

    private val mJoinHelper by lazy {
        TXVideoJoiner(context)
    }
    private val mVideoInfoReader by lazy {
        TXVideoJoiner(context)
    }

    private val binding = DataBindingUtil.inflate<LayoutVideoJoinContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_join_container,
        this,
        true
    )

    init {
        mJoinHelper.setTXVideoPreviewListener(this)
    }

    override fun setVideoSourceList(videoSourceList: MutableList<String>, block: () -> Unit){
        val mRet = mJoinHelper.setVideoPathList(videoSourceList)
        if (mRet == TXVideoEditConstants.ERR_UNSUPPORT_VIDEO_FORMAT) {
            DialogUtil.showDialog(
                context, "视频合成失败", "本机型暂不支持此视频格式"
            ) {
                block()
            }
        } else if (mRet == TXVideoEditConstants.ERR_UNSUPPORT_AUDIO_FORMAT) {
            DialogUtil.showDialog(
                context, "视频合成失败", "暂不支持非单双声道的视频格式"
            ) {
                block() }
        }
        initPlayerLayout()

    }


   private  fun initPlayerLayout() {
        val param = TXPreviewParam()
        param.videoView =binding.mVideoView
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
        mJoinHelper.initWithPreview(param)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun startPlay() {
        if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP || mCurrentState == PlayState.STATE_PREVIEW_AT_TIME) {
            mJoinHelper.startPlay()
            mCurrentState = PlayState.STATE_PLAY
         //   mIbPlay.setImageResource(R.drawable.ic_pause)
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopPlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY || mCurrentState == PlayState.STATE_PREVIEW_AT_TIME || mCurrentState == PlayState.STATE_PAUSE) {
            mJoinHelper.stopPlay()
            mCurrentState = PlayState.STATE_STOP
         //   mIbPlay.setImageResource(com.tencent.liteav.demo.videojoiner.R.drawable.ic_play)
        }
    }

    override fun resumePlay() {
        if (mCurrentState == PlayState.STATE_PAUSE) {
            mJoinHelper.resumePlay()
            mCurrentState = PlayState.STATE_RESUME
           // mIbPlay.setImageResource(com.tencent.liteav.demo.videojoiner.R.drawable.ic_pause)
        }
    }

    override fun pausePlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            mJoinHelper.pausePlay()
            mCurrentState = PlayState.STATE_PAUSE
         //   mIbPlay.setImageResource(com.tencent.liteav.demo.videojoiner.R.drawable.ic_play)
        }
    }

    override fun videoPlay() {
        if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
            startPlay()
        } else if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            pausePlay()
        } else if (mCurrentState == PlayState.STATE_PAUSE) {
            resumePlay()
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun release() {
        mJoinHelper.apply {
             stopPlay()
             cancel()
             setTXVideoPreviewListener(null)
             setVideoJoinerListener(null)
        }

    }

    private var mVideoOutputPath=""

    override fun startGenerateVideo() {
        // 停止播放
        stopPlay()
        // 处于生成状态
        mCurrentState = PlayState.STATE_GENERATE
        mJoinHelper.setVideoJoinerListener(this)
        mVideoOutputPath= VideoPathUtil.generateVideoPath()
        mJoinHelper.joinVideo(
            TXVideoEditConstants.VIDEO_COMPRESSED_720P,
            mVideoOutputPath
        )
    }

    override fun stopGenerateVideo() {
        if (mCurrentState == PlayState.STATE_GENERATE) {
            mCurrentState = PlayState.STATE_NONE
            mJoinHelper.setVideoJoinerListener(null)
            mJoinHelper.cancel()
        }
    }

    override fun onPreviewProgress(time: Int) {

    }

    override fun onPreviewFinished() {
        mCurrentState=PlayState.STATE_STOP
        startPlay()
    }


    private var mListener:IVideoJoin.OnJoinListener?=null

    fun setJoinListener(listener: IVideoJoin.OnJoinListener){
        mListener=listener
    }


    override fun onJoinProgress(progress: Float) {
        mListener?.onJoinProgress(progress)

    }

    override fun onJoinComplete(result: TXVideoEditConstants.TXJoinerResult) {
        mListener?.onJoinComplete(result,mVideoOutputPath)
        mCurrentState = PlayState.STATE_NONE
    }

}