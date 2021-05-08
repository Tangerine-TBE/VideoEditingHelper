package com.twx.module_videoediting.ui.adapter.recycleview.video.join

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.component.slider.RangeSlider
import com.tencent.qcloud.ugckit.module.effect.utils.Edit
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemJoinCutContainerBinding
import com.twx.module_videoediting.domain.JoinData
import com.twx.module_videoediting.ui.widget.video.join.JoinHelper

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
       fun setItemData(data: JoinData?, position: Int) {
           itemBinding?.apply {
              //
               data?.apply {
                   cutSliderView.setMediaFileInfo(TXVideoInfoReader.getInstance(cutSliderView.context).getVideoFileInfo(data.pathList[position]))
                   cutSliderView.addBitmapList(data.bitmapList[position])
                   cutSliderView.setCutChangeListener(object : Edit.OnCutChangeListener {
                       override fun onCutClick() {
                           // LogUtils.i("-----adapterPosition--------${adapterPosition}----------------")
                       }

                       override fun onCutChangeKeyDown() {
                           LogUtils.i("-----adapterPosition--------${adapterPosition}----------------")
                       }

                       override fun onCutChangeKeyUp(startTime: Long, endTime: Long, type: Int) {
                           mOnListener?.selectClick(data, position,startTime)
                       }

                   })

               }

           }
       }


   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemJoinCutContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_join_cut_container, parent, false)
        return MyHolder(itemBinding.root,itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setItemData(mJoinData,position)
    }

    override fun getItemCount(): Int =mJoinData?.pathList?.size?:0


    private val mList = ArrayList<MutableList<Bitmap>>()
    private val mEditerList = ArrayList<TXVideoEditer>()
    private val editorList = ArrayList<JoinHelper>()

    fun setBitmap(list:MutableList<MutableList<Bitmap>>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


    fun setVideoEditorInfo(list:MutableList<JoinHelper>){
        editorList.clear()
        editorList.addAll(list)
        notifyDataSetChanged()
    }

    fun setTXVideoEditer(list:MutableList<TXVideoEditer>){
        mEditerList.clear()
        mEditerList.addAll(list)
        notifyDataSetChanged()
    }

    private var mJoinData: JoinData?=null

    fun setJoinData(data:JoinData){
        mJoinData=data
        notifyDataSetChanged()
    }



    private var mOnListener:OnListener?=null

    fun setOnListener(listener:OnListener){
        mOnListener=listener
    }

    interface OnListener{
        fun selectClick(data: JoinData, position: Int,startTime:Long)


    }




}