package com.twx.module_videoediting.repository

import com.twx.module_videoediting.R
import com.twx.module_videoediting.domain.ItemBean

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.repository
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 11:24:17
 * @class describe
 */
object DataProvider {

    val navigationLis= arrayListOf(
            ItemBean(title="首页",icon = R.mipmap.icon_home_normal),
            ItemBean(title="我的文件",icon = R.mipmap.icon_file_normal),
            ItemBean(title="设置",icon = R.mipmap.icon_set_normal),)


    val bottomLis= arrayListOf(
            ItemBean(title="配乐",icon = R.mipmap.icon_bottom_music),
            ItemBean(title="倒放",icon = R.mipmap.icon_bottom_back),
            ItemBean(title="变速",icon = R.mipmap.icon_bottom_speed),
            ItemBean(title="比例",icon = R.mipmap.icon_bottom_size),
            )

    val setLis= arrayListOf(
            ItemBean(title="主题切换"),
            ItemBean(title="检查更新"),
            ItemBean(title="关于我们"),
            ItemBean(title="意见反馈"),
            ItemBean(title="用户协议"),
            ItemBean(title="隐私政策"),
            ItemBean(title="清除默认方式"),

    )


}