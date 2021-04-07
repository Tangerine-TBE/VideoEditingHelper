package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.DiyVideoCutViewBinding
import com.twx.module_videoediting.domain.ThumbnailInfo
import com.twx.module_videoediting.ui.adapter.recycleview.video.cut.ThumbnailCutAdapter
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.cut
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/2 14:25:39
 * @class describe
 */
class CutView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr),ICutView {
    private val binding= DataBindingUtil.inflate<DiyVideoCutViewBinding>(LayoutInflater.from(context), R.layout.diy_video_cut_view, this, true)
    private val mThumbnailCutAdapter by lazy { ThumbnailCutAdapter() }
    private var mTotalDurationMs = 0L
    private var mCurrentTimeMs = 0L
    private var mCurrentScroll = 0f
    private var mScrollState = 0
    private var mIsTouching = false
    private var mThumbnailPicListDisplayWidth = 0f // 视频缩略图列表的宽度
    private var mVideoProgressSeekListener:ICutView.VideoProgressSeekListener?=null

    private val mOnScrollListener by lazy {
        object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        mVideoProgressSeekListener?.onVideoProgressSeekFinish(mCurrentTimeMs)
                        LogUtils.i("-time--onScrollStateChanged--------------------------$mCurrentTimeMs--")
                    }
                }
                mScrollState=newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mCurrentScroll += dx
                val rate = mCurrentScroll /mThumbnailPicListDisplayWidth
                val currentTimeUs = (rate * mTotalDurationMs).toLong()
                if (mIsTouching || mScrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mVideoProgressSeekListener?.onVideoProgressSeek(currentTimeUs)
                        LogUtils.i("-time--onScrolled-------- $mCurrentScroll ---$mTotalDurationMs----- $dx ---------- $mCurrentTimeMs --")
                }
                mCurrentTimeMs = currentTimeUs
            }
        }
    }

    init {
        binding.apply {
            thumbnailContainer.apply {
                layoutManager=LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter=mThumbnailCutAdapter
                addOnScrollListener(mOnScrollListener)
            }
            initEvent()
        }
    }

    private fun initEvent() {
        binding.thumbnailContainer.setOnTouchListener { v, event ->
            val eventId: Int = event.action
            when (eventId) {
                MotionEvent.ACTION_DOWN -> mIsTouching = true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mIsTouching = false
            }
            return@setOnTouchListener false
        }
    }

   override fun setAllThumbnailListWidth(number:Int): Float {
        if (mThumbnailPicListDisplayWidth == 0f) {
            mThumbnailCutAdapter.apply {
                mThumbnailPicListDisplayWidth= number*getSingleThumbnailWidth()
            }
        }
        return mThumbnailPicListDisplayWidth
    }


    override fun addThumbnail(position: Int, data: ThumbnailInfo){
       // mThumbnailCutAdapter.setData(position, data)
    }

    fun setThumbnailList(list:MutableList<VideoEditerSDK.ThumbnailBitmapInfo>){
        mThumbnailCutAdapter.setThumbnailList(list)
    }



    override  fun clearThumbnail(){
        mThumbnailCutAdapter.clearData()
    }


    override fun setCurrentTime(currentTime: Long) {
        mCurrentTimeMs = currentTime
        var rate = mCurrentTimeMs.toFloat()/ mTotalDurationMs
        val scrollBy = rate * mThumbnailPicListDisplayWidth - mCurrentScroll
        binding.thumbnailContainer.scrollBy(scrollBy.toInt(), 0)
        LogUtils.i("--setCurrentTime-------------  $currentTime  $mTotalDurationMs   $rate    ${mThumbnailPicListDisplayWidth*rate}     $mCurrentScroll     $scrollBy   ")
    }


    override fun setTotalDuration(totalTime: Long) {
        mTotalDurationMs=totalTime
    }


    override fun setVideoProgressSeekListener(listener: ICutView.VideoProgressSeekListener) {
        mVideoProgressSeekListener=listener
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mThumbnailCutAdapter.clearData()
    }
}