package com.twx.module_videoediting.ui.widget.video.cut

import com.tencent.qcloud.ugckit.basic.UGCKitResult

/**
 * @author wujinming QQ:1245074510
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.cut
 * @class describe
 * @time 2021/3/31 17:09:20
 * @class describe
 */
interface IVideoCut {
    /**
     * 设置视频裁剪的源路径
     *
     * @param videoPath 视频裁剪的源路径
     */
    fun setVideoPath(videoPath: String?)

    /**
     * 开始播放视频
     */
    fun startPlay()

    /**
     * 停止播放视频
     */
    fun stopPlay()

    /**
     * 暂停播放视频
     */
    fun pausePlay()

    /**
     * 继续播放视频
     */
    fun resumePlay()

    /**
     * 释放资源
     */
    fun release()

    /**
     * 设置视频裁剪的监听器
     */
    fun setOnCutListener(listener: OnCutListener?)

    interface OnCutListener {
        /**
         * 视频裁剪操作完成
         */
        fun onCutterCompleted(ugcKitResult: UGCKitResult?)

        /**
         * 视频裁剪操作取消
         */
        fun onCutterCanceled()
    }

    /**
     * 是否进行视频编辑
     *
     * @param enable {@code true} 进行视频特效编辑<br>
     *               {@code false} 直接输出裁剪完视频<br>
     *               默认为true
     */
    fun setVideoEditFlag(enable: Boolean)

    /**
     * 仅当调用 [.setVideoEditFlag] 参数为 `false` 时，有效
     *
     * @return 裁剪完的视频路径
     */
    fun getVideoOutputPath(): String?
}