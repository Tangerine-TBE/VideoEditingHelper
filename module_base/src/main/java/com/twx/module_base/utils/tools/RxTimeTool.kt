package com.twx.module_base.utils.tools


import java.text.SimpleDateFormat
import java.util.*

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_base.utils.tools
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/16 10:44:00
 * @class describe
 */
object RxTimeTool {
    const val DATE_FORMAT_DETACH = "yyyy-MM-dd HH:mm:ss"

    val DEFAULT_SDF = SimpleDateFormat(DATE_FORMAT_DETACH, Locale.getDefault())
    /**
     * 将Date类型转为时间字符串
     *
     * 格式为用户自定义
     *
     * @param time   Date类型时间
     * @param format 时间格式
     * @return 时间字符串
     */
    /**
     * 将Date类型转为时间字符串
     *
     * 格式为yyyy-MM-dd HH:mm:ss
     *
     * @param time Date类型时间
     * @return 时间字符串
     */
    @JvmOverloads
    @JvmStatic
    fun date2String(time: Date?, format: SimpleDateFormat = DEFAULT_SDF): String {
        return format.format(time)
    }
}