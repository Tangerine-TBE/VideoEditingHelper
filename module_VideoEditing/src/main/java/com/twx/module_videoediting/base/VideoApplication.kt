package com.twx.module_videoediting.base

import com.example.module_ad.advertisement.TTAdManagerHolder
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.ugc.TXUGCBase
import com.twx.module_base.base.BaseApplication
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
    //测试
/*    var ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/1b592d2799a3f2883bf67909d7c618aa/TXUgcSDK.licence"
    var ugcKey = "ddabc6115bdc625158612c14d21b8b28" */


    override fun initData() {

    }

    companion object{
        private val ugcLicenceUrl ="https://license.vod2.myqcloud.com/license/v2/1305741322_1/v_cube.license"
        private val ugcKey = "5ebd5f4adf8aeb086ca0d565117b7549"
        fun initSdk(){
            TTAdManagerHolder.init(application)
            PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
            TXUGCBase.getInstance().setLicence(application, ugcLicenceUrl, ugcKey)
            TXUGCBase.getInstance().getLicenceInfo(application)
            UGCKit.init(application);
        }
    }
}