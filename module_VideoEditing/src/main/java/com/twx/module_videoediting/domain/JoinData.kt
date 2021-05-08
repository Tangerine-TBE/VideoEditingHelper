package com.twx.module_videoediting.domain

import android.graphics.Bitmap

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.domain
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/8 15:29:30
 * @class describe
 */
data class JoinData(val bitmapList:MutableList<MutableList<Bitmap>>,val pathList:MutableList<String>){
}