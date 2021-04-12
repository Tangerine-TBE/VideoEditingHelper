package com.twx.module_videoediting.ui.adapter.recycleview.video.cut

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemExportContainerBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.utils.Constants

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.cut
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/12 15:27:58
 * @class describe
 */
class ExportAdapter:BaseQuickAdapter<ItemBean,BaseDataBindingHolder<ItemExportContainerBinding>>(R.layout.item_export_container) {

    override fun convert(holder: BaseDataBindingHolder<ItemExportContainerBinding>, item: ItemBean) {
        holder.dataBinding?.apply {
            editIcon.setImageResource(item.icon)
            editTitle.text=item.title
            viewThemeColor(SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE),editTitle)
        }
    }
}