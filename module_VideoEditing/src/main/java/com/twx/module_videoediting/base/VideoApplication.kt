package com.twx.module_videoediting.base

import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.twx.module_base.base.BaseApplication
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.ugc.TXUGCBase
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager


/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.base
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 20:06:49
 * @class describe
 */
class VideoApplication : BaseApplication() {
    var ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/1b592d2799a3f2883bf67909d7c618aa/TXUgcSDK.licence"
    var ugcKey = "ddabc6115bdc625158612c14d21b8b28"
    override fun initData() {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        TXUGCBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey)
        TXUGCBase.getInstance().getLicenceInfo(this)
        UGCKit.init(this);
    }
}