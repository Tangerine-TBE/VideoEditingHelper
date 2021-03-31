package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout.OnRotateVideoListener
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.Edit.OnCutChangeListener
import com.tencent.rtmp.TXLog
import com.tencent.ugc.TXVideoEditConstants.TXVideoInfo
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.MyVideoCutKitBinding
import com.twx.module_videoediting.ui.widget.video.BaseVideoUi
import com.twx.module_videoediting.utils.video.PlayerManager

class TWVideoCutLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr), IVideoCutLayout, View.OnClickListener, OnCutChangeListener {
    private var mRotation = 0
    private var mOnRotateVideoListener: OnRotateVideoListener? = null
    private val binding=DataBindingUtil.inflate<MyVideoCutKitBinding>(LayoutInflater.from(context),R.layout.my_video_cut_kit,this,true)

    init {
        binding.apply {
            ivRotate.setOnClickListener(this@TWVideoCutLayout)
            videoEditView.setCutChangeListener(this@TWVideoCutLayout)
        }

    }

    override fun onClick(view: View) {
        if (view.id == R.id.iv_rotate) {
            // 当旋转角度大于等于270度的时候，下一次就是0度；
            mRotation = if (mRotation < 270) mRotation + 90 else 0
            mOnRotateVideoListener?.onRotate(mRotation)
        }
    }

    override fun onCutClick() {}
    override fun onCutChangeKeyDown() {
        PlayerManager.stopPlay()
    }

    override fun onCutChangeKeyUp(startTime: Long, endTime: Long, type: Int) {
        val duration = (endTime - startTime) / 1000
        val str = resources.getString(R.string.tc_video_cutter_activity_load_video_success_already_picked) + duration + "s"
        binding.tvChooseDuration.text = str
        PlayerManager.startPlay()
        VideoEditerSDK.getInstance().setCutterStartTime(startTime, endTime)
    }

    override fun setVideoInfo(videoInfo: TXVideoInfo) {
        binding.apply {
            mRotation = 0
            var durationS = (videoInfo.duration / 1000).toInt()
            val thumbCount = durationS / 3
            if (durationS >= IVideoCutLayout.MAX_DURATION) {
                durationS = IVideoCutLayout.MAX_DURATION
            }
            tvChooseDuration.text = resources.getString(R.string.tc_video_cutter_activity_load_video_success_already_picked) + durationS + "s"
            VideoEditerSDK.getInstance().setCutterStartTime(0, (durationS * 1000).toLong())
           videoEditView.setMediaFileInfo(videoInfo)
           videoEditView.setCount(thumbCount)
        }
    }


    override fun addThumbnail(index: Int, bitmap: Bitmap) {
        binding.videoEditView.addBitmap(index, bitmap)
    }

    fun clearThumbnail() {
        binding.videoEditView.clearAllBitmap()
    }

    override fun setOnRotateVideoListener(listener: OnRotateVideoListener) {
        mOnRotateVideoListener = listener
    }

    companion object {
        private const val TAG = "VideoCutLayout"
    }
}