package com.twx.module_videoediting.domain

import android.graphics.RectF

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.domain
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/25 11:34:12
 * @class describe
 */
data class CropConfig(val gho:Int=0,val orientation:Int=0,val rect: RectF) {
}