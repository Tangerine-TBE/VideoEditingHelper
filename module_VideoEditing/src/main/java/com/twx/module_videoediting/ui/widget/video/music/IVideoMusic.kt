package com.twx.module_videoediting.ui.widget.video.music

import com.tencent.qcloud.ugckit.module.record.MusicInfo

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.music
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/9 16:27:30
 * @class describe
 */
interface IVideoMusic {


    fun setVideoInfo(videoPath:String)


    fun setMusicInfo(musicInfo: MusicInfo)

}