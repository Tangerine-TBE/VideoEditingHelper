package com.twx.module_videoediting.ui.widget.video.tags

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoPasterPlayerBinding
import com.twx.module_videoediting.databinding.LayoutVideoPasterPlayerControlBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager


/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.tags
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/20 17:34:55
 * @class describe
 */
class PasterPlayerControl @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr), PlayerManager.OnPlayStateListener,
    PlayerManager.OnPreviewListener {
    private val binding =
        DataBindingUtil.inflate<LayoutVideoPasterPlayerControlBinding>(
            LayoutInflater.from(context),
            R.layout.layout_video_paster_player_control,
            this,
            true
        )
    private var mDuration = ""
    private var textTime=0L

    override fun initPlayerLayout() {
        mDuration=  formatDuration(mVideoEditorHelper.txVideoInfo.duration)
        textTime=mVideoEditorHelper.txVideoInfo.duration
        //添加播放状态监听
        PlayerManager.addOnPlayStateListener(this)
        PlayerManager.addOnPreviewListener(this)
        initEvent()
    }

    fun getDeleteAction()=binding.deletePaster

    private fun initEvent() {
        // 播放动作
        binding.apply {
            videoPlayAction.setOnClickListener {
                PlayerManager.playVideo(false)
            }
        }
    }

    private var playState:(Int)->Unit={}

    fun setPlayState(action:(Int)->Unit){
        playState=action
    }

    override fun onPlayState(state: Int) {
        binding.apply {
            playState(state)
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
        binding.videoTime.text = "${formatDuration(time.toLong())}/$mDuration"
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