package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.qcloud.ugckit.component.slider.RangeSlider.OnRangeChangeListener
import com.tencent.qcloud.ugckit.module.effect.time.TCVideoEditerAdapter
import com.tencent.qcloud.ugckit.module.effect.utils.Edit.OnCutChangeListener
import com.tencent.rtmp.TXLog
import com.tencent.ugc.TXVideoEditConstants.TXVideoInfo
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.MyItemEditViewBinding
import com.twx.module_videoediting.ui.widget.video.BaseVideoUi

/**
 * 裁剪View
 */
class TWVideoCutView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr), OnRangeChangeListener
  {
    private val TAG = "VideoCutView"
    private var mCurrentScroll = 0f

    /**
     * 单个缩略图的宽度
     */
    private var mSingleWidth = 0

    /**
     * 所有缩略图的宽度
     */
    private var mAllWidth = 0

    /**
     * 整个视频的时长
     */
    private var mVideoDuration: Long = 0

    /**
     * 控件最大时长16s
     */
    private var mViewMaxDuration: Long = 0

    /**
     * 如果视频时长超过了控件的最大时长，底部在滑动时最左边的起始位置时间
     */
    private var mStartTime: Long = 0

    /**
     * 裁剪的起始时间，最左边是0
     */
    private var mViewLeftTime = 0

    /**
     * 裁剪的结束时间，最右边最大是16000ms
     */
    private var mViewRightTime = 0

    /**
     * 最终视频的起始时间
     */
    private var mVideoStartPos: Long = 0

    /**
     * 最终视频的结束时间
     */
    private var mVideoEndPos: Long = 0
    private var mRangeChangeListener: OnCutChangeListener? = null
    private val binding= DataBindingUtil.inflate<MyItemEditViewBinding>(LayoutInflater.from(context), R.layout.my_item_edit_view,this,true)
    private val mAdapter by lazy {
        TCVideoEditerAdapter(context)
    }
    private val mOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            TXCLog.i(TAG, "onScrollStateChanged, new state = $newState")
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> onTimeChanged()
                RecyclerView.SCROLL_STATE_DRAGGING -> mRangeChangeListener?.onCutChangeKeyDown()
                RecyclerView.SCROLL_STATE_SETTLING -> {
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            mCurrentScroll += dx
            val rate = mCurrentScroll / mAllWidth
            mStartTime = if (mCurrentScroll + binding.recyclerView.width >= mAllWidth) {
                mVideoDuration - mViewMaxDuration
            } else {
                (rate * mVideoDuration).toLong()
            }
        }
    }

    init{
        binding.apply {
            rangeSlider.setRangeChangeListener(this@TWVideoCutView)
            val manager = LinearLayoutManager(context)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.layoutManager = manager
            recyclerView.addOnScrollListener(mOnScrollListener)
            recyclerView.adapter = mAdapter
            mSingleWidth = resources.getDimensionPixelOffset(R.dimen.ugc_item_thumb_height)
        }

    }

    /**
     * 设置缩略图个数
     *
     * @param count
     */
    fun setCount(count: Int) {
        val layoutParams = layoutParams
        var width = count * mSingleWidth
        mAllWidth = width
        val resources = resources
        val dm = resources.displayMetrics
        val screenWidth = dm.widthPixels
        if (width > screenWidth) {
            width = screenWidth
        }
        layoutParams.width = width + 2 * resources.getDimensionPixelOffset(R.dimen.ugc_cut_margin)
        setLayoutParams(layoutParams)
    }

    /**
     * 设置裁剪Listener
     *
     * @param listener
     */
    fun setCutChangeListener(listener: OnCutChangeListener?) {
        mRangeChangeListener = listener
    }

    fun setMediaFileInfo(videoInfo: TXVideoInfo?) {
        if (videoInfo == null) {
            return
        }
        mVideoDuration = videoInfo.duration
        mViewMaxDuration = if (mVideoDuration >= 16000) {
            16000
        } else {
            mVideoDuration
        }
        mViewLeftTime = 0
        mViewRightTime = mViewMaxDuration.toInt()
        mVideoStartPos = 0
        mVideoEndPos = mViewMaxDuration
    }

    fun addBitmap(index: Int, bitmap: Bitmap?) {
        mAdapter.add(index, bitmap)
    }

    fun clearAllBitmap() {
        mAdapter.clearAllBitmap()
    }

    override fun onKeyDown(type: Int) {
        mRangeChangeListener?.onCutChangeKeyDown()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAdapter.clearAllBitmap()
    }

    override fun onKeyUp(type: Int, leftPinIndex: Int, rightPinIndex: Int) {
        mViewLeftTime = (mViewMaxDuration * leftPinIndex / 100).toInt() //ms
        mViewRightTime = (mViewMaxDuration * rightPinIndex / 100).toInt()
        onTimeChanged()
    }

    private fun onTimeChanged() {
        mVideoStartPos = mStartTime + mViewLeftTime
        mVideoEndPos = mStartTime + mViewRightTime
        mRangeChangeListener?.onCutChangeKeyUp(mVideoStartPos, mVideoEndPos, 0)
    }


}