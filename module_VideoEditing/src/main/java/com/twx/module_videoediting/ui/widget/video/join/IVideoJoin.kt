package com.twx.module_videoediting.ui.widget.video.join

import com.tencent.ugc.TXVideoEditConstants

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/15 16:27:23
 * @class describe
 */
interface IVideoJoin {
    //--------------------------播放器行为-----------------
    fun setVideoSourceList(videoSourceList:MutableList<String>,block:()->Unit)

    fun startPlay()

    fun stopPlay()

    fun resumePlay()

    fun pausePlay()

    fun videoPlay()


    fun release()


    //---------------------------制作行为-----------------------
    fun startGenerateVideo()

    fun stopGenerateVideo()


    interface OnJoinListener{

       fun  onJoinProgress(progress:Float)


       fun onJoinComplete(result: TXVideoEditConstants.TXJoinerResult,videoOutputPath:String)
    }

}