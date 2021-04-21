package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.ugc.TXVideoEditConstants
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoPasterContainerBinding
import com.twx.module_videoediting.databinding.LayoutVideoPasterPlayerBinding
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.ganeral
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/20 17:30:48
 * @class describe
 */
class VideoPasterPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding =
        DataBindingUtil.inflate<LayoutVideoPasterPlayerBinding>(
            LayoutInflater.from(context),
            R.layout.layout_video_paster_player,
            this,
            true
        )

    /**
     * 初始化预览播放器
     */
    init {
        val param = TXVideoEditConstants.TXPreviewParam()
        param.videoView = binding.mPasterPlayer
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
        VideoEditerSDK.getInstance().editer?.initWithPreview(param)
    }


}