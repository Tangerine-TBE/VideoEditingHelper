package com.twx.module_videoediting.utils.video

import com.tencent.qcloud.ugckit.module.editer.TailWaterMarkConfig
import com.tencent.qcloud.ugckit.module.editer.WaterMarkConfig

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/1 18:43:34
 * @class describe
 */
interface IVideoGenerate {

    /**
     * 设置视频分辨率
     *
     * @param resolution
     */
    fun setVideoResolution(resolution: Int)
    /**
     * 自定义视频码率
     *
     * @param videoBitrate
     */
    fun setCustomVideoBitrate(videoBitrate: Int)
    /**
     * 开始合成视频
     */
    fun startGenerate()
    /**
     * 停止合成视频包括一些异常操作导致的合成取消
     */
    fun stopGenerate()
    /**
     * 添加片尾水印
     */
    fun addTailWaterMark()
    /**
     * 获取生成视频输出路径
     *
     * @return
     */
    fun getVideoOutputPath():String?

    /**
     * 获取视频封面路径
     */
    fun getCoverPath(): String?

    /**
     * 保存到相册
     * @param flag Boolean
     */
    fun saveVideoToDCIM(flag: Boolean)
    /**
     * 设置封面
     * @param coverGenerate Boolean
     */
    fun setCoverGenerate(coverGenerate: Boolean)
    /**
     * 设置水印
     * @param waterMark WaterMarkConfig
     */
    fun setWaterMark(waterMark: WaterMarkConfig)
    /**
     * 设置片尾水印
     * @param waterMark WaterMarkConfig
     */
    fun setTailWaterMark(tailWaterMarkConfig: TailWaterMarkConfig)

    /**
     * 释放资源
     */
    fun release()
}