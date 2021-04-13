package com.twx.module_videoediting.domain

import android.graphics.Bitmap

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.domain
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/13 14:25:13
 * @class describe
 */
data class MediaInformation(val id:Long=0L,var name:String="", var duration:String?="", var date:String?="", var path:String="", var uri :String="", val bitmap: Bitmap?=null)
