package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXPreviewParam
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoContainerBinding
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 9:47:53
 * @class describe
 */
class VideoCutPlayerControl @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr), PlayerManager.OnPlayStateListener,
    PlayerManager.OnPreviewListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_container,
        this,
        true
    )
    private var mDuration = ""

    private var textTime=0L
    /**
     * 初始化预览播放器
     */
   override fun initPlayerLayout() {
        val param = TXPreviewParam()
        param.videoView = binding.mVideoPlayerView
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
        mVideoEditorHelper.editer?.initWithPreview(param)
        mDuration=  formatDuration(mVideoEditorHelper.txVideoInfo.duration)
        textTime=mVideoEditorHelper.txVideoInfo.duration
        //添加播放状态监听
        PlayerManager.addOnPlayStateListener(this)
        PlayerManager.addOnPreviewListener(this)
        initEvent()
    }

    private fun initEvent() {
        // 播放动作
       binding.playerControl.apply {
            videoPlayAction.setOnClickListener {
                PlayerManager.playVideo(false)
            }
        }
    }

    private var mCurrentTime = 0L
    override fun onPreviewProgress(time: Int) {
        LogUtils.i("------onPreviewProgress:$time---------总时长:$textTime----------")
        mCurrentTime = time.toLong()
        binding.playerControl.videoTime.text = "${formatDuration(time.toLong())}/$mDuration"
        previewAction(time)

    }

    private var previewAction:(Int)->Unit={}
    fun previewProgressAction(block:(Int)->Unit){
        previewAction=block
    }
    fun getCurrentTime()=mCurrentTime

    override fun onPlayState(state: Int) {
        playState(state)
        binding.playerControl.apply {
            LogUtils.i("------onPlayState-------------$state-------------------")
            when (state) {
                PlayState.STATE_PLAY, PlayState.STATE_RESUME -> {
                    videoPlayAction.setImageResource(R.mipmap.icon_player_stop)
                }
                PlayState.STATE_STOP, PlayState.STATE_PAUSE, PlayState.STATE_NONE -> {
                    videoPlayAction.setImageResource(R.mipmap.icon_player_start)
                }
            }
        }
    }

    private var playState:(Int)->Unit={}

    fun setPlayState(action:(Int)->Unit){
        playState=action
    }

    override fun onPreviewFinish() {

    }


    override fun release() {
        super.release()
        PlayerManager.removeOnPreviewListener(this)
        PlayerManager.removeOnPlayStateListener(this)

    }

}