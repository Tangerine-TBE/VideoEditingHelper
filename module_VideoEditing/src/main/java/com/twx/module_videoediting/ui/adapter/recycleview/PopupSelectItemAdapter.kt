package com.twx.module_videoediting.ui.adapter.recycleview

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemSelectPopupContainerBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.MediaInformation

/**
 * @name VidelPlayer
 * @class name：com.example.module_video.ui.adapter.recycleview
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/8 17:04:24
 * @class describe
 */
class PopupSelectItemAdapter:BaseQuickAdapter<MediaInformation,BaseDataBindingHolder<ItemSelectPopupContainerBinding>>(R.layout.item_select_popup_container) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSelectPopupContainerBinding>,
        item: MediaInformation
    ) {
        holder.dataBinding?.apply {
            itemText.text="${item.name}"
        }
    }

}