package com.twx.module_videoediting.ui.adapter.recycleview.video.tags

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemPasterContainerBinding
import com.twx.module_videoediting.domain.PasterInfo
import com.twx.module_videoediting.base.BaseAdapter

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.tags
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/20 14:07:56
 * @class describe
 */
class PasterAdapter: BaseAdapter<PasterInfo, BaseDataBindingHolder<ItemPasterContainerBinding>>(R.layout.item_paster_container) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemPasterContainerBinding>,
        item: PasterInfo
    ) {
        holder.dataBinding?.apply {
            pasterIcon.setImageResource(item.icon)
            pasterTitle.text="${item.name}"
            pasterTitle.setTextColor(ContextCompat.getColor(context, if(themeState) R.color.black else R.color.white ))
        }
    }


    fun getListData()=data

}