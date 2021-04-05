package com.twx.module_videoediting.ui.widget.video.cut

import com.twx.module_videoediting.domain.ThumbnailInfo

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.cut
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/2 16:33:53
 * @class describe
 */
interface ICutView {

    //-------------recycleView--------------------------
    /**
     * 添加缩略图
     * @param position Int
     * @param data ThumbnailInfo
     */
    fun addThumbnail(position: Int, data: ThumbnailInfo)

    /**
     * 清除缩略图
     */
    fun clearThumbnail()

    /**
     * 获取缩略图列表的长度，需要在设置完数据之后调用，否则返回0
     * @return Float
     */
    fun setAllThumbnailListWidth(number: Int): Float

    /**
     * 设置随视频进度自动滑动
     * @param currentTime Long
     */
    fun setCurrentTime(currentTime: Long)

    /**
     * 设置总时长
     * @param totalTime Long
     */
    fun setTotalDuration(totalTime: Long)


    fun setVideoProgressSeekListener(listener:VideoProgressSeekListener)

    interface VideoProgressSeekListener{
        fun onVideoProgressSeek(currentTimeMs: Long)

        fun onVideoProgressSeekFinish(currentTimeMs: Long)
    }

}