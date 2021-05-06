package com.twx.module_videoediting.ui.widget.video.join

import android.graphics.Bitmap
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseApplication

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 16:30:46
 * @class describe
 */
class JoinHelper(val path:String) {

    private var mStartTime=0L
    private var mEndTime=0L
    private var mVideoInfo=TXVideoInfoReader.getInstance(BaseApplication.application).getVideoFileInfo(path)
    private var mEditor:TXVideoEditer = TXVideoEditer(BaseApplication.application)


    init {
        mEditor.setCutFromTime(0,mVideoInfo.duration)
    }

    fun setCutTime(startTime:Long,endTime:Long){
        mStartTime=startTime
        mEndTime=endTime
    }
    fun getCutTime()=mEndTime-mStartTime
    fun getStartTime()=mStartTime
    fun getEndTime()=mEndTime
    fun getEditor()=mEditor
    fun getVideoInfo(): TXVideoEditConstants.TXVideoInfo =mVideoInfo
}