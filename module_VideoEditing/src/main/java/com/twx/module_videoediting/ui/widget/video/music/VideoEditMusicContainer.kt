package com.twx.module_videoediting.ui.widget.video.music

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.bgm.view.IEditMusicPannel
import com.tencent.qcloud.ugckit.module.effect.utils.DraftEditer
import com.tencent.qcloud.ugckit.module.record.MusicInfo
import com.twx.module_base.utils.showToast
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoMusiceContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import java.io.IOException

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.music
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/9 16:19:53
 * @class describe
 */
class VideoEditMusicContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr) {
    private var onClickMusic:()->Unit={}
    private val binding = DataBindingUtil.inflate<LayoutVideoMusiceContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_musice_container,
        this,
        true
    )

    private val videoEditor by lazy {
        mVideoEditorHelper.editer
    }

    init {
        mVideoEditorHelper.apply {
            releaseSDK()
            clear()
            initSDK()
        }
        initEvent()

      //  binding.mVideoPlayerView.hideControl()
    }


     fun initEvent() {
         binding.apply {
             selectMusic?.setOnClickListener {
                 onClickMusic()
             }

             mEditMusicView.setOnMusicChangeListener(object:IEditMusicPannel.MusicChangeListener{
                 override fun onMicVolumeChanged(volume: Float) {
                     videoEditor.setVideoVolume(volume)
                 }

                 override fun onMusicVolumChanged(volume: Float) {
                     videoEditor.setBGMVolume(volume)
                 }

                 override fun onMusicTimeChanged(startTime: Long, endTime: Long) {
                     videoEditor.setBGMStartTime(startTime,endTime)
                 }

                 override fun onMusicReplace() {
                     onClickMusic()
                 }

                 override fun onMusicDelete() {
                     videoEditor.setBGM(null)

                     videoEditor.startPlayFromTime(0L,mVideoEditorHelper.txVideoInfo.duration)
                 }
             })
         }
    }


    fun setSelectMusic(block: () -> Unit){
        onClickMusic=block
    }

     fun setVideoInfo(videoPath: String) {
        mVideoEditorHelper.setVideoPathInfo(videoPath)
        binding.mVideoPlayerView.initPlayerLayout()
    }

     fun setMusicInfo(musicInfo: MusicInfo) {
        videoEditor.let {
            val result =it.setBGM(musicInfo.path)
            if (result != 0) {
                showToast("视频编辑失败或不支持此格式")
            }
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(musicInfo.path)
                mediaPlayer.prepare()
                musicInfo.duration = mediaPlayer.duration.toLong()
                mediaPlayer.release()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            it.setBGMStartTime(0, musicInfo.duration)
            it.setBGMVolume(0.5f)
            it.setVideoVolume(0.5f)

            DraftEditer.getInstance().saveRecordMusicInfo(musicInfo)

            musicInfo.videoVolume = 0.5f
            musicInfo.bgmVolume = 0.5f
            musicInfo.startTime = 0
            musicInfo.endTime = musicInfo.duration

            binding.mEditMusicView.setMusicInfo(musicInfo)

        }

    }

   fun getPlayerView()=binding.mVideoPlayerView


    fun showMusicEditor(){
       binding.apply {
           mEditMusicView.visibility=View.VISIBLE
           selectMusic.visibility=View.GONE
       }
    }


}