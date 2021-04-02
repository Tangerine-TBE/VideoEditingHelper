package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.DiyVideoCutViewBinding
import com.twx.module_videoediting.domain.ThumbnailInfo
import com.twx.module_videoediting.ui.adapter.recycleview.video.cut.ThumbnailCutAdapter
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi
import com.twx.module_videoediting.utils.video.PlayerManager

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
) : BaseVideoUi(context, attrs, defStyleAttr),ICutView, PlayerManager.OnPreviewListener {
    private val binding= DataBindingUtil.inflate<DiyVideoCutViewBinding>(LayoutInflater.from(context), R.layout.diy_video_cut_view, this, true)
    private val mThumbnailCutAdapter by lazy { ThumbnailCutAdapter() }
    private val mTotalDurationMs = 0L
    private var mCurrentTimeMs = 0L
    private val mCurrentScroll = 0f
    private var mScrollState = 0
    private var mIsTouching = false
    private var mThumbnailPicListDisplayWidth = 0f // 视频缩略图列表的宽度
    private val mVideoProgressDisplayWidth = 0f// 视频进度条可显示宽度

    /**
     * 单个缩略图的宽度
     */
    private var mSingleThumbnailWidth = 0

    /**
     * 所有缩略图的宽度
     */
    private var mAllThumbnailWidth = 0

    private val mOnScrollListener by lazy {
        object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE -> {

                    }
                }
                mScrollState=newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        }
    }

    init {
        binding.apply {
            thumbnailContainer.apply {
                layoutManager=LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter=mThumbnailCutAdapter
                addOnScrollListener(mOnScrollListener)
                mSingleThumbnailWidth = resources.getDimensionPixelOffset(R.dimen.video_thumbnail_width)
                PlayerManager.addOnPreviewListener(this@CutView)

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

    /**
     * 获取缩略图列表的长度，需要在设置完数据之后调用，否则返回0
     *
     * @return
     */
   override fun getAllThumbnailListWidth(): Float {
        if (mThumbnailPicListDisplayWidth == 0f) {
            mThumbnailPicListDisplayWidth = mThumbnailCutAdapter.getThumbnailCount() * mThumbnailCutAdapter.getSingleThumbnailWidth()
        }
        return mThumbnailPicListDisplayWidth
    }

    /**
     * 设置缩略图个数
     *
     * @param count
     */
    fun setCount(count: Int) {
     /*   val layoutParams = layoutParams
        var width = count * mSingleWidth
        mAllWidth = width
        val resources = resources
        val dm = resources.displayMetrics
        val screenWidth = dm.widthPixels
        if (width > screenWidth) {
            width = screenWidth
        }
        layoutParams.width = width + 2 * resources.getDimensionPixelOffset(R.dimen.ugc_cut_margin)
        setLayoutParams(layoutParams)*/
    }


    override fun addThumbnail(position: Int, data: ThumbnailInfo){
        mThumbnailCutAdapter.setData(position, data)
    }

    override  fun clearThumbnail(){
        mThumbnailCutAdapter.clearData()
    }


    override fun setCurrentTime(currentTime: Long) {
        mCurrentTimeMs = currentTime
        val rate: Float = mCurrentTimeMs.toFloat() / mTotalDurationMs
        val scrollBy: Float = rate * getAllThumbnailListWidth() - mCurrentScroll
    //    binding.thumbnailContainer.scrollBy(scrollBy.toInt(), 0)
    }



    override fun onPreviewProgress(time: Int) {
        val currentState = PlayerManager.getCurrentState()
        if (currentState == PlayState.STATE_PLAY || currentState == PlayState.STATE_RESUME) {
            setCurrentTime(time.toLong())
        }
    }

    override fun onPreviewFinish() {

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mThumbnailCutAdapter.clearData()
    }
}