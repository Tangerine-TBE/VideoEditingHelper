package com.twx.module_videoediting.repository

import com.twx.module_videoediting.R
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.PasterInfo

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.repository
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 11:24:17
 * @class describe
 */
object DataProvider {

    const val STATIC_PASTER=1
    const val DYNAMIC_PASTER=2



    val navigationList= arrayListOf(
            ItemBean(title="首页",icon = R.mipmap.icon_home_normal),
            ItemBean(title="我的文件",icon = R.mipmap.icon_file_normal),
            ItemBean(title="设置",icon = R.mipmap.icon_set_normal),)


    val bottomList= arrayListOf(
            ItemBean(title="配乐",icon = R.mipmap.icon_bottom_music),
            ItemBean(title="倒放",icon = R.mipmap.icon_bottom_back),
            ItemBean(title="变速",icon = R.mipmap.icon_bottom_speed),
            ItemBean(title="比例",icon = R.mipmap.icon_bottom_size),
            )

    val setList= arrayListOf(
            ItemBean(title="主题切换"),
            ItemBean(title="检查更新"),
            ItemBean(title="关于我们"),
            ItemBean(title="意见反馈"),
            ItemBean(title="用户协议"),
            ItemBean(title="隐私政策"),
    )


    val cropItemList= arrayListOf(
        ItemBean(title="垂直翻转",icon = R.mipmap.icon_crop_sx),
        ItemBean(title="左右翻转",icon = R.mipmap.icon_crop_sx),
        ItemBean(title="旋转",icon = R.mipmap.icon_crop_xuanzhuan),
        ItemBean(title="还原",icon = R.mipmap.icon_crop_huanyuan),
        ItemBean(title="原始比例",icon = R.mipmap.icon_crop_xuanzhuan),
        ItemBean(title="自由",icon = R.mipmap.icon_crop_xuanzhuan),
        ItemBean(title="1:1",icon = R.mipmap.icon_crop_xuanzhuan),
        ItemBean(title="16:9",icon = R.mipmap.icon_crop_xuanzhuan),
        ItemBean(title="9:16",icon = R.mipmap.icon_crop_xuanzhuan),
    )


    val editList= arrayListOf(
            ItemBean(title="剪辑",icon = R.mipmap.icon_item_jianji),
        ItemBean(title="拼接",icon = R.mipmap.icon_item_pingjie),
            ItemBean(title="分割",icon = R.mipmap.icon_item_fenge),
            ItemBean(title="贴纸",icon = R.mipmap.icon_item_tiezhi),
            ItemBean(title="配乐",icon = R.mipmap.icon_item_peiyue),
            ItemBean(title="倒放",icon = R.mipmap.icon_item_daofang),
            ItemBean(title="变速",icon = R.mipmap.icon_item_biansu),
            ItemBean(title="比例",icon = R.mipmap.icon_item_bili),
    )

    val itemSelectPopupList= arrayListOf(
            ItemBean(title = "分享"),
            ItemBean(title = "重命名"),
            ItemBean(title = "删除"),
    )

    val tagsTypeList= arrayListOf(
            ItemBean(title = "卡通水果"),
            ItemBean(title = "万圣节"),
            ItemBean(title = "彩虹小马"),
            ItemBean(title = "圣诞贴纸"),
            ItemBean(title = "动物贴纸"),
    )


    val tagsFruitsList= arrayListOf(
          PasterInfo(R.mipmap.static_tags_fruits_chengzi,"橙子",STATIC_PASTER),
          PasterInfo(R.mipmap.static_tags_fruits_caomei,"草莓",STATIC_PASTER),
          PasterInfo(R.mipmap.static_tags_fruits_yingtao,"樱桃",STATIC_PASTER),
          PasterInfo(R.mipmap.static_tags_fruits_mangguo,"柚子",STATIC_PASTER),
          PasterInfo(R.mipmap.static_tags_fruits_boluo,"菠萝",STATIC_PASTER)
    )
    val tagsAnimalList= arrayListOf(
            PasterInfo(R.mipmap.static_tags_animal_hu,"虎",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_niu,"牛",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_she,"蛇",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_yang,"羊",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_shu,"鼠",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_ji,"鸡",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_hou,"猴",STATIC_PASTER),
            PasterInfo(R.mipmap.static_tags_animal_long,"龙",STATIC_PASTER),
    )

    val tagsTransportList= arrayListOf(
        PasterInfo(R.mipmap.static_tags_transpor_bus,"巴士",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_transpor_feiji,"飞机",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_transpor_dianche,"电动摩托",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_transpor_xiaoche,"轿车",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_transpor_lanche,"缆车",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_transpor_huoche,"货车",STATIC_PASTER),
    )


    val tagsChristList= arrayListOf(
        PasterInfo(R.mipmap.static_tags_christ_xueren,"圣诞雪人",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_christ_sdlr,"圣诞老人",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_christ_lw,"圣诞礼物",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_christ_pg,"圣诞苹果",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_christ_ld,"圣诞铃铛",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_christ_lutou,"圣诞鹿",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_christ_buwawa,"圣诞娃娃",STATIC_PASTER),
    )

    val tagsHalloweenList= arrayListOf(
        PasterInfo(R.mipmap.static_tags_halloween_bangbangtang,"棒棒糖",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_bianfu,"蝙蝠",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_guozi,"水果",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_mao,"猫",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_maotouying,"猫头鹰",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_bianfu,"蝙蝠",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_zhuwang,"蜘蛛网",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_nangua1,"南瓜头1",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_nangua2,"南瓜头2",STATIC_PASTER),
        PasterInfo(R.mipmap.static_tags_halloween_sapba,"扫把",STATIC_PASTER),
    )



}