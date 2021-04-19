package com.twx.module_videoediting.ui.widget.video.reverse

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoReverseContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import com.twx.module_videoediting.utils.video.PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.reverse
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/7 14:23:01
 * @class describe
 */
class VideoEditReverseContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr){
    private val binding = DataBindingUtil.inflate<LayoutVideoReverseContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_reverse_container,
        this,
        true
    )

    init {
        binding.mVideoPlayerView.initPlayerLayout()
        initEvent()
    }
     fun initEvent() {
        binding.apply {
            makeReverse.setOnClickListener {
                changeReverse()
            }
        }
    }

    fun getPlayerView()=binding.mVideoPlayerView

   private fun changeReverse(){
       mVideoEditorHelper.apply {
           mVideoEditorHelper.editer.setReverse(!isReverse)
           mVideoEditorHelper.isReverse=!isReverse
           PlayerManager.restartPlay()
           binding.makeReverse.text=if (isReverse) "还原" else "倒放"
       }
    }

}