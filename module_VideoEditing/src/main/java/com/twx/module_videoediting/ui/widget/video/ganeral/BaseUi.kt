package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.ganeral
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/12 17:33:54
 * @class describe
 */
open class BaseUi @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    protected val mJob = Job()
    protected var mScope = CoroutineScope(mJob)


}