package com.twx.module_videoediting.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.base
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/7 15:57:12
 * @class describe
 */
open class BaseView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr),LifecycleObserver {

    protected val mJob = Job()
    protected var mScope = CoroutineScope(mJob)

}