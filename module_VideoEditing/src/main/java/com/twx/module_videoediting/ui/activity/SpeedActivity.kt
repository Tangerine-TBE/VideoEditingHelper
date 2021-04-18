package com.twx.module_videoediting.ui.activity

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivitySpeedBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.widget.video.speed.SpeedView
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.SpeedViewModel


class SpeedActivity : BaseVmViewActivity<ActivitySpeedBinding, SpeedViewModel>() {

    private val player by lazy {
      SimpleExoPlayer.Builder(this@SpeedActivity).build()
    }

    override fun getViewModelClass(): Class<SpeedViewModel> {
        return SpeedViewModel::class.java
    }

    override fun getLayoutView(): Int =R.layout.activity_speed


    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@SpeedActivity, speedTitleBar, LayoutType.CONSTRAINTLAYOUT)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                playerView.player=player
                val mediaItem = MediaItem.fromUri(Uri.parse(it))
                player.apply {
                    setMediaItem(mediaItem)
                  //  repeatMode=SimpleExoPlayer.REPEAT_MODE_ALL
                    prepare()
                    play()
                }

            }

        }

    }



    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@SpeedActivity, {
                    speedTitleBar.setThemeChange(it)
                    viewThemeColor(it, speedContainer)
                })

            }
        }
    }

    override fun initEvent() {
        binding.apply {
            speedTitleBar.setBarEventAction(this@SpeedActivity) {}

            speedView.setOnSpeedListener(object :SpeedView.OnSpeedListener{
                override fun selectSpeed(speed: Float) {
                    player.setPlaybackParameters(PlaybackParameters(speed))
                    speedHint.text="${String.format("%.2f",speed)}X"
                    LogUtils.i("---setPlaybackParameters---------${player.playbackParameters}---------------")
                }
            })

            completeSpeed.setOnClickListener {

            }

        }
    }

    override fun release() {
        player.release()
    }

}