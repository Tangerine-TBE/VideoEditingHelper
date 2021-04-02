package com.twx.module_videoediting.ui.widget.video.ganeral.`interface`

import androidx.annotation.DrawableRes

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.ganeral.`interface`
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/2 11:13:56
 * @class describe
 */
interface ITWTimeLineView {
    /**
     * 初始化进度布局
     */
    fun initVideoProgressLayout()

    /**
     * 根据显示的特效类型更新UI
     *
     * @param type [UGCKitConstants.TYPE_EDITER_BGM] 添加背景音<br></br>
     * [UGCKitConstants.TYPE_EDITER_MOTION] 添加动态滤镜<br></br>
     * [UGCKitConstants.TYPE_EDITER_SPEED] 添加时间特效<br></br>
     * [UGCKitConstants.TYPE_EDITER_FILTER] 添加静态滤镜<br></br>
     * [UGCKitConstants.TYPE_EDITER_PASTER] 添加贴纸<br></br>
     * [UGCKitConstants.TYPE_EDITER_SUBTITLE] 添加字幕<br></br>
     */
    fun updateUIByFragment(type: Int)

    /**
     * 当前时间点的Icon
     */
    fun setCurrentProgressIconResource(@DrawableRes resid: Int)
}