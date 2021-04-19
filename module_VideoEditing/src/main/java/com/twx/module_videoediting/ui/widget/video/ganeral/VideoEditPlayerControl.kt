package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.ugc.TXVideoEditConstants
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoPlayerControlBinding
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager


/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.ganeral
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/12 12:01:00
 * @class describe
 */
class VideoEditPlayerControl @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : BaseVideoEditUi(context, attrs, defStyleAttr), PlayerManager.OnPlayStateListener, PlayerManager.OnPreviewListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoPlayerControlBinding>(LayoutInflater.from(context), R.layout.layout_video_player_control, this, true)
    private var maxDuration=0L
    /**
     * 初始化预览播放器
     */
   override fun initPlayerLayout() {
        val param = TXVideoEditConstants.TXPreviewParam()
        param.videoView = binding.mPlayerView
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
        mVideoEditorHelper.editer.initWithPreview(param)
        maxDuration= mVideoEditorHelper.txVideoInfo.duration
        binding.playerControl.progressBar.max=maxDuration.toInt()
        //添加播放状态监听
        PlayerManager.addOnPlayStateListener(this)
        PlayerManager.addOnPreviewListener(this)
        initEvent()

    }

    private var isUser=false

    private fun initEvent() {
        binding.apply {
            playerControl.apply {
                videoPlayAction.setOnClickListener {
                    PlayerManager.playVideo(false)
                }


                progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                    ) {
                        if (fromUser) {
                            PlayerManager.previewAtTime(progress.toLong())
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        isUser=true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        isUser=false
                        PlayerManager.playVideo(false)
                    }
                })
            }

        }
    }

    fun hideControl(){
        binding.playerControl.root.visibility=View.GONE
    }

    override fun onPlayState(state: Int) {
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

    override fun onPreviewProgress(time: Int) {
        LogUtils.i("------onPreviewProgress:$time---------totalTime:$maxDuration----------")
        binding.playerControl.apply {
            if (!isUser) {
                progressBar.progress = time
            }
            beginTime.text = formatDuration(time.toLong())
            endTime.text = formatDuration(maxDuration)
            
        }
    }

    override fun onPreviewFinish() {
        PlayerManager.restartPlay()
    }


    override fun release() {
        super.release()
        PlayerManager.removeOnPreviewListener(this)
        PlayerManager.removeOnPlayStateListener(this)

    }


}