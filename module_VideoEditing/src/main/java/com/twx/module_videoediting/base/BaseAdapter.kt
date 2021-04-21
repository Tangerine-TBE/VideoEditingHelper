package com.twx.module_videoediting.base

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.twx.module_base.utils.SPUtil
import com.twx.module_videoediting.utils.Constants

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/21 11:31:33
 * @class describe
 */
abstract class BaseAdapter<T,VH: BaseViewHolder>(layoutResId: Int, data: MutableList<T>?=null) : BaseQuickAdapter<T, VH>(layoutResId, data) {

    protected val themeState=SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE)
}