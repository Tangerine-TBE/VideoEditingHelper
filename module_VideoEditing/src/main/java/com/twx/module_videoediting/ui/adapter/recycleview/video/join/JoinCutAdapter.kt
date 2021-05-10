package com.twx.module_videoediting.ui.adapter.recycleview.video.join

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.module.effect.utils.Edit
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemJoinCutContainerBinding
import com.twx.module_videoediting.databinding.ItemJoinCutTwoContainerBinding
import com.twx.module_videoediting.domain.ReadyJoinInfo

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 16:25:52
 * @class describe
 */
class JoinCutAdapter : RecyclerView.Adapter<JoinCutAdapter.MyHolder>() {

    inner class MyHolder(itemView: View, val itemBinding: ItemJoinCutContainerBinding) : RecyclerView.ViewHolder(itemView) {
        fun setItemData(data: ReadyJoinInfo, position: Int) {
            itemBinding?.apply {
                data?.apply {
                    cutSliderView.setMediaFileInfo(TXVideoInfoReader.getInstance(cutSliderView.context).getVideoFileInfo(data.videoPath))
                    cutSliderView.addBitmapList(data.bitmapList)
                    cutSliderView.setCutChangeListener(object : Edit.OnCutChangeListener {
                        override fun onCutClick() {
                            if (mReadyList.size > adapterPosition) {
                                mOnListener?.click(mReadyList[adapterPosition])
                            }

                        }

                        override fun onCutChangeKeyDown() {

                        }

                        override fun onCutChangeKeyUp(startTime: Long, endTime: Long, type: Int) {
                            if (mReadyList.size > adapterPosition) {
                                mReadyList[adapterPosition].let {
                                    it.cutStartTime = startTime
                                    it.cutEndTime = endTime
                                    mOnListener?.selectClick(it, position, type)
                                }
                                LogUtils.i("-----readyJoinInfo---${mReadyList.hashCode()}-----${mReadyList[position].videoPath}---------------")
                            }
                        }

                        override fun onDeleteItem() {
                            if (mReadyList.size > adapterPosition) {
                                mReadyList.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                                mOnListener?.deleteItem(mReadyList)
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemJoinCutContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_join_cut_container, parent, false)
    //    val itemBindingSpecial= DataBindingUtil.inflate<ItemJoinCutTwoContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_join_cut_two_container, parent, false)
        return MyHolder(itemBinding.root, itemBinding)

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setItemData(mReadyList[position], position)
    }

    override fun getItemCount(): Int = mReadyList.size


    private val mReadyList = ArrayList<ReadyJoinInfo>()

    fun setReadyJoinList(list: MutableList<ReadyJoinInfo>) {
        mReadyList.clear()
        mReadyList.addAll(list)
        notifyDataSetChanged()
    }

    fun getReadyJoinList() = mReadyList


    companion object{
        const val NORMAL_HOLDER=1
        const val SPECIAL_HOLDER=2
    }






    private var mOnListener: OnListener? = null
    fun setOnListener(listener: OnListener) {
        mOnListener = listener
    }
    interface OnListener {
        fun selectClick(data: ReadyJoinInfo, position: Int, slideType: Int)

        fun click(data: ReadyJoinInfo)

        fun deleteItem(list: MutableList<ReadyJoinInfo>)
    }



}