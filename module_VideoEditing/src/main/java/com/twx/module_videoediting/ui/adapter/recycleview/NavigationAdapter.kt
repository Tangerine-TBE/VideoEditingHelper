package com.twx.module_videoediting.ui.adapter.recycleview

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemNavigationContainerBinding
import com.twx.module_videoediting.domain.ItemBean

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.adapter
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 11:27:24
 * @class describe
 */
class NavigationAdapter : BaseQuickAdapter<ItemBean, BaseDataBindingHolder<ItemNavigationContainerBinding>>(R.layout.item_navigation_container) {
    private var mThemeState=false
    private var mSelectPosition = 0

    fun setSelectPosition(position: Int) {
        mSelectPosition = position
        notifyDataSetChanged()
    }
    fun setThemeChangeState(state: Boolean){
        mThemeState=state
        notifyDataSetChanged()
    }


    override fun convert(holder: BaseDataBindingHolder<ItemNavigationContainerBinding>, item: ItemBean) {
        holder.dataBinding?.apply {
            bottomTitle.text = item.title
            bottomIcon.setImageResource(item.icon)

            bottomIcon.setColorFilter(ContextCompat.getColor(context, when {
                holder.adapterPosition == mSelectPosition -> R.color.color_home_text
                mThemeState -> R.color.color_home_icon_false
                else -> R.color.white
            }))
            bottomTitle.setTextColor(ContextCompat.getColor(context, when {
                holder.adapterPosition == mSelectPosition -> R.color.color_home_text
                mThemeState -> R.color.black
                else -> R.color.white
            }))
        }
    }
}