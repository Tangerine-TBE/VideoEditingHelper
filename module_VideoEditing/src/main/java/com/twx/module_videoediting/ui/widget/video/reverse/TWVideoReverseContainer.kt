package com.twx.module_videoediting.ui.widget.video.reverse

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoReverseContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.reverse
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/7 14:23:01
 * @class describe
 */
class TWVideoReverseContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr), PlayerManager.OnPreviewListener,
    PlayerManager.OnPlayStateListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoReverseContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_reverse_container,
        this,
        true
    )
    private val mEndTime=mVideoEditorHelper.txVideoInfo.duration

    init {
        initEvent()
        binding.mVideoPlayerView.initPlayerLayout()
        binding.reverseControl.progressBar.max=mEndTime.toInt()
        //添加播放状态监听
        PlayerManager.addOnPlayStateListener(this)
        PlayerManager.addOnPreviewListener(this)

    }

    private var isUser=false
    private fun initEvent() {
        binding.apply {
            makeReverse.setOnClickListener {
                changeReverse()
            }

            // 播放动作
            reverseControl.apply {
                videoPlayAction.setOnClickListener {
                    PlayerManager.playVideo(false)
                }

                progressBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
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

   private fun changeReverse(){
       mVideoEditorHelper.apply {
           mVideoEditorHelper.editer.setReverse(!isReverse)
           mVideoEditorHelper.isReverse=!isReverse
           PlayerManager.restartPlay()
           binding.makeReverse.text=if (isReverse) "还原" else "倒放"
       }

    }

    override fun onPreviewProgress(time: Int) {
        LogUtils.i("------onPreviewProgress-------------$time----------$mEndTime---------")
            binding.reverseControl.apply {
                progressBar.progress = time
                beginTime.text = formatDuration(time.toLong())
                endTime.text = formatDuration(mEndTime)
        }
    }

    override fun onPreviewFinish() {
     //   PlayerManager.restartPlay()
    }

    override fun onPlayState(state: Int) {
        binding.reverseControl.apply {
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

    override fun release() {
        super.release()
        PlayerManager.apply {
            removeOnPlayStateListener(this@TWVideoReverseContainer)
            removeOnPreviewListener(this@TWVideoReverseContainer)
        }
    }
}