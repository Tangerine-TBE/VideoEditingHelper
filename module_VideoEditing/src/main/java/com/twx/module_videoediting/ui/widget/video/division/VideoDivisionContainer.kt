package com.twx.module_videoediting.ui.widget.video.division

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoCutContainerBinding
import com.twx.module_videoediting.databinding.LayoutVideoDivisionContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import com.twx.module_videoediting.utils.video.PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.division
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/14 17:26:50
 * @class describe
 */
class VideoDivisionContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr) {

    private val binding = DataBindingUtil.inflate<LayoutVideoDivisionContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_division_container,
        this,
        true
    )


    override fun setVideoPath(videoPath: String?) {
        mVideoEditorHelper.let {
            binding.apply {
             //   loadVideoInfo()
                // 初始化播放器界面[必须在setPictureList/setVideoPath设置数据源之后]
                mVideoCutPlayerControl.initPlayerLayout()
                it.resetDuration()



            }
        }
    }


    fun getPlayerView() = binding.mVideoCutPlayerControl

}