package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoContainerBinding
import com.twx.module_videoediting.domain.VideoEditorInfo
import com.twx.module_videoediting.ui.widget.video.join.JoinHelper

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.ganeral
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 15:31:05
 * @class describe
 */
class VideoJoinPlayerControl @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : BaseUi(context, attrs, defStyleAttr), TXVideoEditer.TXVideoPreviewListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoContainerBinding>(
            LayoutInflater.from(context),
            R.layout.layout_video_container,
            this,
            true
    )


    fun initPlayerLayout(joinHelper: JoinHelper) {
            val param = TXVideoEditConstants.TXPreviewParam()
            param.videoView = binding.mVideoPlayerView
            param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
            joinHelper.getEditor()?.apply {
                initWithPreview(param)
                startPlayFromTime(0, joinHelper.getVideoInfo()?.duration?:0)
                setTXVideoPreviewListener(this@VideoJoinPlayerControl)
            }
    }


    override fun onPreviewProgress(time: Int) {

    }

    override fun onPreviewFinished() {

    }


}