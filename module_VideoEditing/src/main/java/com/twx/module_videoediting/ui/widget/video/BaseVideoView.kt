package com.twx.module_videoediting.ui.widget.video

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 10:23:56
 * @class describe
 */
open class BaseVideoUi@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

        protected val mJob= Job()
        protected var mScope= CoroutineScope(mJob)

}