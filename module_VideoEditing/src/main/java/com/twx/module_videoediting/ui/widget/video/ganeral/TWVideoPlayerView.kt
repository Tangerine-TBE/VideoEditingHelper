package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXPreviewParam
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoContainerBinding

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 9:47:53
 * @class describe
 */
class TWVideoPlayerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr) {
    private val mView=DataBindingUtil.inflate<LayoutVideoContainerBinding>(LayoutInflater.from(context), R.layout.layout_video_container, this, true)
    /**
     * 初始化预览播放器
     */
    fun initPlayerLayout() {
        val param = TXPreviewParam()
        param.videoView = mView.mVideoContainer
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
        val videoEditer = VideoEditerSDK.getInstance().editer
        videoEditer?.initWithPreview(param)

       // VideoEditorsManager.getEditer()?.initWithPreview(param)

    }


}