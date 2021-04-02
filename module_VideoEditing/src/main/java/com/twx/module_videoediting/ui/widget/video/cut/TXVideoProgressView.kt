package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.component.timeline.ThumbnailAdapter
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.TwLayoutVideoProgressBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.ganeral
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/2 10:59:22
 * @class describe
 */
class TXVideoProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr) {
    private var mViewWidth = 0
    private var mThumbnailList: List<Bitmap> = ArrayList()
    private var mThumbnailAdapter: ThumbnailAdapter? = null

    private val binding=DataBindingUtil.inflate<TwLayoutVideoProgressBinding>(LayoutInflater.from(context), R.layout.tw_layout_video_progress, this, true)

    init {
        binding.rvVideoThumbnail.layoutManager=LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }


    fun setViewWidth(viewWidth: Int) {
        mViewWidth = viewWidth
    }

    fun setThumbnailData(thumbnailList: List<Bitmap>) {
        mThumbnailList = thumbnailList
        mThumbnailAdapter = ThumbnailAdapter(mViewWidth, mThumbnailList)
        binding.rvVideoThumbnail.adapter = mThumbnailAdapter
        mThumbnailAdapter?.notifyDataSetChanged()
    }

    fun getRecyclerView(): RecyclerView{
        return binding.rvVideoThumbnail
    }
    fun getThumbnailCount(): Int {
        return mThumbnailAdapter?.itemCount?:0- 2
    }

    fun getSingleThumbnailWidth(): Float {
        return resources.getDimension(com.tencent.qcloud.ugckit.R.dimen.video_thumbnail_width)
    }

    fun getParentView(): View {
        return binding.root
    }
}