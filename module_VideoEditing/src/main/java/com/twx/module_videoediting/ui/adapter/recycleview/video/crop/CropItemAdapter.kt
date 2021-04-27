package com.twx.module_videoediting.ui.adapter.recycleview.video.crop

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.base.BaseAdapter
import com.twx.module_videoediting.databinding.ItemCropItemContainerBinding
import com.twx.module_videoediting.domain.ItemBean

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.crop
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/23 15:42:37
 * @class describe
 */
class CropItemAdapter:BaseAdapter<ItemBean,BaseDataBindingHolder<ItemCropItemContainerBinding>>(R.layout.item_crop_item_container) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCropItemContainerBinding>,
        item: ItemBean
    ) {
        holder.dataBinding?.apply {
            cropIcon.setImageResource(item.icon)
            cropTitle.text="${item.title}"
            viewThemeColor(themeState,cropIcon,cropTitle)

            if (isSize) {
                if (holder.adapterPosition == mPosition) {
                    cropIcon.setColorFilter(ContextCompat.getColor(context,R.color.theme_color))
                    cropTitle.setTextColor(ContextCompat.getColor(context,R.color.theme_color))
                }
            }
        }
    }



    private var isSize=false
    private var mPosition=-1


    fun setModelPosition(sizeState:Boolean,position:Int){
        mPosition=position
        isSize=sizeState
        notifyDataSetChanged()
    }

}