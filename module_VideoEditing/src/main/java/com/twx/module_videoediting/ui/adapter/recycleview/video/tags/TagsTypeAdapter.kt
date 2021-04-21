package com.twx.module_videoediting.ui.adapter.recycleview.video.tags

import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemTagsTypeContainerBinding
import com.twx.module_videoediting.domain.ItemBean

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.tags
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/20 10:27:05
 * @class describe
 */
class TagsTypeAdapter:BaseQuickAdapter<ItemBean,BaseDataBindingHolder<ItemTagsTypeContainerBinding>>(R.layout.item_tags_type_container) {

    private var mCurrentPosition=0

    fun setPosition(position:Int){
        mCurrentPosition=position
        notifyDataSetChanged()
    }


    override fun convert(holder: BaseDataBindingHolder<ItemTagsTypeContainerBinding>, item: ItemBean) {
          holder.dataBinding?.apply {
              val isPosition = mCurrentPosition == holder.adapterPosition
              tagsTitle.text="${item.title}"
              tagsTitle.setTextColor(ContextCompat.getColor(tagsTitle.context,if (isPosition) R.color.white else R.color.white_56))
              indicate.visibility= if (isPosition) View.VISIBLE else View.GONE
          }
    }
}