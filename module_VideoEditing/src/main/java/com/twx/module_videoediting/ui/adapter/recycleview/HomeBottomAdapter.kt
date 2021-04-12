package com.twx.module_videoediting.ui.adapter.recycleview

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.utils.setBgDrawableStyle
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemBottomContainerBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_base.utils.viewThemeColor

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.adapter
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 11:23:17
 * @class describe
 */
class HomeBottomAdapter : BaseQuickAdapter<ItemBean, BaseDataBindingHolder<ItemBottomContainerBinding>>(R.layout.item_bottom_container) {

    private var mThemeState=false

    fun setThemeChangeState(state: Boolean){
        mThemeState=state
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseDataBindingHolder<ItemBottomContainerBinding>, item: ItemBean) {
        holder.dataBinding?.apply {

            itemInclude.setBgDrawableStyle(mThemeState,R.drawable.shape_home_bottom_false_bg,R.drawable.shape_home_bottom_true_bg)
            title. text=item.title
            icon.setImageResource(item.icon)
            viewThemeColor(mThemeState,title,icon)
        }
    }
}