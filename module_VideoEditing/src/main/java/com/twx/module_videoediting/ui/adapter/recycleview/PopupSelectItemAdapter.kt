package com.twx.module_videoediting.ui.adapter.recycleview

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemSelectPopupContainerBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.MediaInformation

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.popup
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/14 10:56:54
 * @class describe
 */
class PopupSelectItemAdapter: BaseQuickAdapter<ItemBean, BaseDataBindingHolder<ItemSelectPopupContainerBinding>>(R.layout.item_select_popup_container) {
    override fun convert(
            holder: BaseDataBindingHolder<ItemSelectPopupContainerBinding>,
            item: ItemBean
    ) {
        holder.dataBinding?.apply {
            itemText.text="${item.title}"
        }
    }

}