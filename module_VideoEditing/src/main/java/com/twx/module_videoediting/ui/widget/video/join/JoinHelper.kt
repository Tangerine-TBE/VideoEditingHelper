package com.twx.module_videoediting.ui.widget.video.join

import android.graphics.Bitmap
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 16:30:46
 * @class describe
 */
class JoinHelper{

    private var mStartTime=0L
    private var mEndTime=0L
    private lateinit var mVideoInfo:TXVideoEditConstants.TXVideoInfo
    private var mBitmapList = ArrayList<Bitmap>()
    private lateinit var mEditor:TXVideoEditer


     fun  initData(path:String){
         mVideoInfo= TXVideoInfoReader.getInstance(BaseApplication.application).getVideoFileInfo(path)
         GlobalScope.launch {
             mEditor= TXVideoEditer(BaseApplication.application).apply {
                  setVideoPath(path)
                 setCutFromTime(0,mVideoInfo.duration)
                 LogUtils.i("JoinHelper--$path-----------${mVideoInfo.duration}----------")
             }
         }

   }


    fun setCutTime(startTime:Long,endTime:Long){
        mStartTime=startTime
        mEndTime=endTime
    }
    fun getCutTime()=mEndTime-mStartTime
    fun getStartTime()=mStartTime
    fun getEndTime()=mEndTime
    fun getEditor()=mEditor
    fun getVideoInfo(): TXVideoEditConstants.TXVideoInfo? =mVideoInfo
    fun getBitmapList()=
            mBitmapList
}