package com.twx.module_videoediting.ui.adapter.recycleview.video.join

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemJoinContainerBinding
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.formatDuration

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/15 13:48:49
 * @class describe
 */
class JoinAdapter:RecyclerView.Adapter<JoinAdapter.MyHolder>() {

    private val mDataList = ArrayList<MediaInformation>()

    class MyHolder(itemView: View,val itemBinding:ItemJoinContainerBinding) : RecyclerView.ViewHolder(itemView) {
        fun setItemData(mediaInformation: MediaInformation, position: Int) {
            itemBinding?.apply {
                Glide.with(joinIcon.context).load(mediaInformation.path).into(joinIcon)
                joinTitle.text="${mediaInformation.path}"
                joinDuration.text="时长：${formatDuration(mediaInformation.duration)}"
                joinTitle.isSelected=true
                viewThemeColor(SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE),joinTitle,joinDuration)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemJoinContainerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_join_container,
            parent,
            false
        )
        return MyHolder(itemBinding.root,itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setItemData(mDataList[position],position)
    }

    override fun getItemCount(): Int=mDataList.size

    fun getData()=mDataList

    fun setItemList(list:MutableList<MediaInformation>){
        mDataList.clear()
        mDataList.addAll(list)
        notifyDataSetChanged()
    }
}