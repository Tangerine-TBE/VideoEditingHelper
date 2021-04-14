package com.twx.module_videoediting.ui.adapter.recycleview

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemAudioContainerBinding
import com.twx.module_videoediting.databinding.ItemSelectPopupContainerBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.MediaInformation

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/14 14:47:45
 * @class describe
 */
class AudioAdapter: BaseQuickAdapter<MediaInformation, BaseDataBindingHolder<ItemAudioContainerBinding>>(
    R.layout.item_audio_container) {



    override fun convert(
        holder: BaseDataBindingHolder<ItemAudioContainerBinding>,
        item: MediaInformation
    ) {
        holder.dataBinding?.apply {
            audioTitle.text = "${item.name}"
            audioTitle.isSelected=true
            viewThemeColor(SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE),audioTitle,audioThumbnail)

        }


    }

}