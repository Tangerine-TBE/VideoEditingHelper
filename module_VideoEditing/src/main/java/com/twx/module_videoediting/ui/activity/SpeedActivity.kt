package com.twx.module_videoediting.ui.activity

import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivitySpeedBinding
import com.twx.module_videoediting.ui.widget.video.speed.SpeedView
import com.twx.module_videoediting.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class SpeedActivity : BaseViewActivity<ActivitySpeedBinding>() {
    private var mSrcFile = ""
    private var mVideoOutputPath = ""
    private var mSpeed = 1.0f
    private val callback by lazy {
        ffCallback(onComplete = {
            LogUtils.i("---SpeedActivity--------------${ FileUtils.getVideoDuration(mVideoOutputPath)}---------------------")
            FileUtils.saveAlbum(this, mVideoOutputPath)
            ExportActivity.toExportPage(this, true, mVideoOutputPath)
        }, onProgress = {
            loadingPopup.setProgress(it)
        }, onCancel = {
            if (!TextUtils.isEmpty(mVideoOutputPath)) {
                FileUtils.deleteFile(File(mVideoOutputPath))
            }
        })
    }
    private val player by lazy {
        SimpleExoPlayer.Builder(this@SpeedActivity).build()
    }


    override fun getLayoutView(): Int = R.layout.activity_speed


    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, speedContainer,speedHint)
            setStatusBarDistance(this@SpeedActivity, speedTitleBar, LayoutType.CONSTRAINTLAYOUT)
            //获取视频路径播放
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                mSrcFile = it
                LogUtils.i("----getVideoDuration--------------------${FileUtils.getVideoDuration(mSrcFile)}---------------")
                playerView.player = player.apply {
                    repeatMode=Player.REPEAT_MODE_ALL
                    setMediaItem(MediaItem.fromUri(Uri.parse(it)))
                    prepare()
                    play()
                }
            }

        }
        MakeBackLiveData.observe(this@SpeedActivity, {
            if (it) loadingPopup.dismiss()
        })

    }


    override fun initEvent() {
        binding.apply {
            speedTitleBar.setBarEventAction(this@SpeedActivity) {}

            //调整速度
            speedView.setOnSpeedListener(object : SpeedView.OnSpeedListener {
                override fun selectSpeed(speed: Float) {
                    mSpeed = speed
                    player.setPlaybackParameters(PlaybackParameters(speed))
                    speedHint.text = "${String.format("%.2f", speed)}X"

                }
            })

            //输出视频
            completeSpeed.setOnClickListener {
                if (!TextUtils.isEmpty(mSrcFile)) {
                    mScope.launch(Dispatchers.IO) {
                        mVideoOutputPath = VideoPathUtil.generateVideoPath()
                        FFmpegHelper.startCommand(FFmpegHelper.changeVideoSpeed(mSpeed, mSrcFile, mVideoOutputPath), callback)

                    }
                    player.stop()
                    loadingPopup.showPopupView(speedContainer)
                }
            }

        }

        //取消输出视频
        loadingPopup.cancelMake {
            FFmpegHelper.exitCommand()
        }
    }


    override fun release() {
        super.release()
        player.release()
        mJob.cancel()
        FFmpegHelper.clean()
    }

}