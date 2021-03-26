package com.twx.module_videoediting.ui.adapter.recycleview

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemSetCotainerBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_base.utils.viewThemeColor

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.adapter.recycleview
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/25 10:07:29
 * @class describe
 */
class SetAdapter:BaseQuickAdapter<ItemBean,BaseDataBindingHolder<ItemSetCotainerBinding>>( R.layout.item_set_cotainer) {
    private var mThemeState=false
    fun setThemeChangeState(state: Boolean){
        mThemeState=state
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseDataBindingHolder<ItemSetCotainerBinding>, item: ItemBean) {
        holder.dataBinding?.apply {
            data=item
            viewThemeColor(mThemeState,setTitle,setGo)
        }
    }

}