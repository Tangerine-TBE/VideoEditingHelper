package com.twx.module_videoediting.ui.widget.video.tags

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.component.timeline.VideoProgressController
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoTagsContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import com.twx.module_videoediting.utils.video.PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.tags
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/19 16:58:39
 * @class describe
 */
class VideoTagsContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr), VideoProgressController.VideoProgressSeekListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoTagsContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_tags_container,
        this,
        true
    )

    init {
        binding.apply {
            timelineView.initVideoProgressLayout()
        }
    }


   override  fun initPlayerLayout() {
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


    override fun onVideoProgressSeek(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }

    override fun onVideoProgressSeekFinish(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }

}