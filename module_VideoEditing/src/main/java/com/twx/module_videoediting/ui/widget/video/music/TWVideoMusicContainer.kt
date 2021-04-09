package com.twx.module_videoediting.ui.widget.video.music

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.bgm.view.IEditMusicPannel
import com.tencent.qcloud.ugckit.module.effect.utils.DraftEditer
import com.tencent.qcloud.ugckit.module.record.MusicInfo
import com.tencent.qcloud.ugckit.utils.DialogUtil
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoMusiceContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi
import com.twx.module_videoediting.utils.video.PlayerManager
import java.io.IOException

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.music
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/9 16:19:53
 * @class describe
 */
class TWVideoMusicContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr),IVideoMusic, PlayerManager.OnPreviewListener {
    private var onClickMusic:()->Unit={}
    private val binding = DataBindingUtil.inflate<LayoutVideoMusiceContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_musice_container,
        this,
        true
    )
    init {
        PlayerManager.addOnPreviewListener(this)
        mVideoEditorHelper.apply {
            releaseSDK()
            clear()
            initSDK()
        }
        initEvent()
    }

     fun initEvent() {
         binding.apply {
             selectMusic?.setOnClickListener {
                 onClickMusic()
             }


             mEditMusicView.setOnMusicChangeListener(object:IEditMusicPannel.MusicChangeListener{
                 override fun onMicVolumeChanged(volume: Float) {

                 }

                 override fun onMusicVolumChanged(volume: Float) {

                 }

                 override fun onMusicTimeChanged(startTime: Long, endTime: Long) {

                 }

                 override fun onMusicReplace() {

                 }

                 override fun onMusicDelete() {

                 }
             })


         }


    }

    fun setSelectMusic(block: () -> Unit){
        onClickMusic=block
    }

    override fun setVideoInfo(videoPath: String) {
        mVideoEditorHelper.setVideoPathInfo(videoPath)
        binding.mVideoPlayerView.initPlayerLayout()
    }

    override fun setMusicInfo(musicPath: String, musicName: String) {
        mVideoEditorHelper.editer.let {
            val musicInfo = MusicInfo()
            val result =it.setBGM(musicInfo.path)
            if (result != 0) {
                DialogUtil.showDialog(
                    context,
                    resources.getString(R.string.tc_bgm_setting_fragment_video_edit_failed),
                    resources.getString(R.string.tc_bgm_setting_fragment_background_sound_only_supports_mp3_or_m4a_format),
                    null
                )
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


        }

    }



    override fun onPreviewProgress(time: Int) {

    }

    override fun onPreviewFinish() {
       PlayerManager.restartPlay()
    }




    override fun release() {
        super.release()
        PlayerManager.removeOnPreviewListener(this)
    }

}