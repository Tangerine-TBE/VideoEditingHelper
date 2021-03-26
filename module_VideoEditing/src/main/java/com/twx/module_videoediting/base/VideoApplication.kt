package com.twx.module_videoediting.base

import com.twx.module_base.base.BaseApplication
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.ugc.TXUGCBase




/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.base
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 20:06:49
 * @class describe
 */
class VideoApplication : BaseApplication() {
    var ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/ebb8e35f8de1b1937129c4032ca48e85/TXUgcSDK.licence"
    var ugcKey = "8cd54276b496819824c7bd1e1e1f7ff1"
    override fun initData() {
        TXUGCBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey)
        val string = TXUGCBase.getInstance().getLicenceInfo(this)
        UGCKit.init(this);
    }
}