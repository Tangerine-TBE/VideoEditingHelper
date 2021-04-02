package com.twx.module_videoediting.ui.adapter.recycleview.video.cut

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tamsiree.rxkit.RxDeviceTool
import com.twx.module_base.base.BaseApplication
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemVideoCutContainerBinding
import com.twx.module_videoediting.domain.ThumbnailInfo

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.cut
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/2 14:04:41
 * @class describe
 */
class ThumbnailCutAdapter : RecyclerView.Adapter<ThumbnailCutAdapter.MyHolder>() {
    private val TYPE_HEADER = 1
    private val TYPE_FOOTER = 2
    private val TYPE_THUMBNAIL = 3
    private val dataList: MutableList<ThumbnailInfo> = ArrayList()

    class MyHolder(itemView: View, val itemBinding: ItemVideoCutContainerBinding? = null) : RecyclerView.ViewHolder(itemView) {

        fun setItemData(thumbnailBitmapInfo: ThumbnailInfo, position: Int) {
            itemBinding?.apply {
                time.text = thumbnailBitmapInfo.timeMs.toString()
                thumbnail.setImageBitmap(thumbnailBitmapInfo.thumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemVideoCutContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_video_cut_container, parent, false)
        when (viewType) {
            TYPE_HEADER, TYPE_FOOTER -> {
                val itemView = View(parent.context)
                itemView.layoutParams = ViewGroup.LayoutParams(RxDeviceTool.getScreenWidth(parent.context) / 2, ViewGroup.LayoutParams.MATCH_PARENT)
                itemView.setBackgroundColor(Color.TRANSPARENT)
                return MyHolder(itemView)
            }
            TYPE_THUMBNAIL -> {
                return MyHolder(itemBinding.root, itemBinding)
            }
        }
        return MyHolder(itemBinding.root, itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        if (position != 0 && position != dataList.size + 1) {
            holder.setItemData(dataList[position-1], position)
        }

    }

    override fun getItemCount(): Int {
        return if (dataList.size == 0) {
            0
        } else {
            dataList.size + 2
        }
    }

    fun getThumbnailCount() = dataList.size


    fun getSingleThumbnailWidth(): Float {
        return BaseApplication.application.resources.getDimension(com.tencent.qcloud.ugckit.R.dimen.video_thumbnail_width)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                TYPE_HEADER
            }
            dataList.size + 1 -> {
                TYPE_FOOTER
            }
            else -> {
                TYPE_THUMBNAIL
            }
        }
    }

    fun setData(position: Int, data: ThumbnailInfo) {
        dataList.add(data)
        notifyItemInserted(position)
    }


    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }
}
