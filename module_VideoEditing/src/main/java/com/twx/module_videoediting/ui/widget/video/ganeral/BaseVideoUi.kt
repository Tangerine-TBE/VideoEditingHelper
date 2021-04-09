package com.twx.module_videoediting.ui.widget.video.ganeral

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_videoediting.utils.video.PlayerManager
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
open class BaseVideoUi @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),LifecycleObserver {
    protected val mVideoEditorHelper by lazy {
        VideoEditerSDK.getInstance()

    }

    protected val mJob = Job()
    protected var mScope = CoroutineScope(mJob)



    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startPlay() {
        PlayerManager.startPlay()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopPlay() {
        PlayerManager.stopPlay()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun release(){
        mVideoEditorHelper.releaseSDK()
        mVideoEditorHelper.clear()
    }


}