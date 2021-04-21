package com.twx.module_videoediting.ui.adapter.recycleview.video.division

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_videoediting.R
import com.twx.module_videoediting.base.BaseAdapter
import com.twx.module_videoediting.databinding.ItemThumbnailDivContainerBinding

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.division
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/21 15:52:01
 * @class describe
 */
class ThumbnailDivAdapter:BaseAdapter<VideoEditerSDK.ThumbnailBitmapInfo, BaseDataBindingHolder<ItemThumbnailDivContainerBinding>>(R.layout.item_thumbnail_div_container) {
    override fun convert(holder: BaseDataBindingHolder<ItemThumbnailDivContainerBinding>, item: VideoEditerSDK.ThumbnailBitmapInfo) {
        holder.dataBinding?.apply {
            Glide.with(divThumbnail.context).load(item.bitmap).into(divThumbnail)
        }
    }
}