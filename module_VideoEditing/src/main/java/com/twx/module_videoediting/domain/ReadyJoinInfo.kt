package com.twx.module_videoediting.domain

import android.graphics.Bitmap

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.domain
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/10 9:38:06
 * @class describe
 */
data class ReadyJoinInfo(var bitmapList:MutableList<Bitmap>, var videoPath:String, var cutStartTime:Long, var cutEndTime:Long)
