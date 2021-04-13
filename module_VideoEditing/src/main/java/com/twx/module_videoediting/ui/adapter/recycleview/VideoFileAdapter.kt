package com.twx.module_videoediting.ui.adapter.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemVideoFileContainerBinding
import com.twx.module_videoediting.domain.MediaInformation
import java.util.HashSet

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/13 14:48:34
 * @class describe
 */
class VideoFileAdapter : RecyclerView.Adapter<VideoFileAdapter.MyHolder>() {

    private var mSelectItemList = HashSet<MediaInformation>()
    private var mEditAction = false
    private var mSelectAllState=false


    fun selectAllItems(){
        mSelectItemList.clear()
        mSelectItemList.addAll(mList.toSet())
        mSelectAllState=true
        notifyDataSetChanged()
    }

    fun getSelectState()=mSelectAllState

    fun getSelectList() = mSelectItemList

    fun clearAllItems(){
        mSelectItemList.clear()
        mSelectAllState=false
        notifyDataSetChanged()
    }

    fun setEditAction(edit: Boolean) {
        mSelectItemList.clear()
        mEditAction = edit
        notifyDataSetChanged()
    }


    inner class MyHolder(itemView: View, val itemBinding: ItemVideoFileContainerBinding) : RecyclerView.ViewHolder(itemView) {

        fun setItemData(media: MediaInformation, position: Int) {
            itemBinding?.apply {
                media?.let {
                    Glide.with(videoThumbnail.context)
                            .load(it.bitmap)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
                            .error(R.mipmap.icon_login_logo).into(videoThumbnail)
                    videoName.text = "${it.name}"
                    videoName.isSelected=true
                    videoDate.text = "${it.date}"
                    videoDuration.text = "时长：${it.duration}"
                }


                videoSelect.visibility = if (mEditAction) View.VISIBLE else View.GONE

                if (mSelectItemList.contains(media)) {
                    if (!mSelectAllState){
                        mSelectItemList.add(media)
                    }
                    videoSelect.setImageResource(R.mipmap.icon_video_select)
                } else {
                    mSelectItemList.remove(media)
                    videoSelect.setImageResource(R.mipmap.icon_item_bili)
                }


                itemView.setOnClickListener {
                    if (mEditAction) {
                        if (mSelectItemList.contains(media)) {
                            mSelectItemList.remove(media)
                            videoSelect.setImageResource(R.mipmap.icon_item_bili)
                        } else {
                            mSelectItemList.add(media)
                            videoSelect.setImageResource(R.mipmap.icon_video_select)
                        }
                    }
                    mSelectAllState=mSelectItemList.size==mList.size
                    mListener?.onItemClick(media, position,itemView)
                }

                videoMore.setOnClickListener {
                    if (mEditAction) {
                        return@setOnClickListener
                    }
                    mListener?.onItemSubClick(media, position)
                }


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemVideoFileContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_video_file_container, parent, false)
        return MyHolder(itemBinding.root, itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setItemData(mList[position], position)
    }

    override fun getItemCount(): Int = mList.size

    private val mList = ArrayList<MediaInformation>()

    fun setItemList(list: MutableList<MediaInformation>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(item: MediaInformation, position: Int,view: View)

        fun onItemSubClick(item: MediaInformation, position: Int)
    }

}