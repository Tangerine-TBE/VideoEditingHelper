package com.twx.module_videoediting.ui.adapter.recycleview.video.join

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.ugc.TXVideoEditer
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemJoinCutContainerBinding
import com.twx.module_videoediting.domain.VideoEditorInfo
import com.twx.module_videoediting.ui.widget.video.cut.VideoCutView
import com.twx.module_videoediting.ui.widget.video.join.JoinEditorManager
import com.twx.module_videoediting.ui.widget.video.join.JoinHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 16:25:52
 * @class describe
 */
class JoinCutAdapter : RecyclerView.Adapter<JoinCutAdapter.MyHolder>() {

   inner class MyHolder(itemView: View,val itemBinding:ItemJoinCutContainerBinding) : RecyclerView.ViewHolder(itemView) {
       fun setItemData(videoEditorInfo: VideoEditorInfo, position: Int) {
           itemBinding?.apply {
               cutSliderView.setMediaFileInfo(videoEditorInfo.joinHelper.getVideoInfo())
               cutSliderView.addBitmapList(videoEditorInfo.joinHelper.getBitmapList())
                itemView.setOnClickListener {
                //    mOnListener?.selectClick(videoEditorInfo)
                }
           }
       }


   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemJoinCutContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_join_cut_container, parent, false)
        return MyHolder(itemBinding.root,itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.setItemData(editorList[position],position)
    }

    override fun getItemCount(): Int =editorList.size


    private val mList = ArrayList<Bitmap>()
    private val mEditerList = ArrayList<TXVideoEditer>()
    private val editorList = ArrayList<VideoEditorInfo>()

    fun setBitmap(list:MutableList<Bitmap>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


    fun setVideoEditorInfo(list:MutableList<VideoEditorInfo>){
        editorList.clear()
        editorList.addAll(list)
        notifyDataSetChanged()
    }

    fun setTXVideoEditer(list:MutableList<TXVideoEditer>){
        mEditerList.clear()
        mEditerList.addAll(list)
        notifyDataSetChanged()
    }


    private var mOnListener:OnListener?=null

    fun setOnListener(listener:OnListener){
        mOnListener=listener
    }

    interface OnListener{
        fun selectClick(videoEditorInfo: VideoEditorInfo)
    }


}