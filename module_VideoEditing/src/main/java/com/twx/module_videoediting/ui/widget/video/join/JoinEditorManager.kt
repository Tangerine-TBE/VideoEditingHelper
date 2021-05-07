package com.twx.module_videoediting.ui.widget.video.join

import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.domain.VideoEditorInfo

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 15:49:38
 * @class describe
 */
object JoinEditorManager {

    private val editorList = ArrayList<VideoEditorInfo>()

    fun getEditorList() = editorList



    fun createEditor(path:String){
        editorList.add(VideoEditorInfo(JoinHelper(path)))
    }


    fun removeEditor(videoEditorInfo: VideoEditorInfo){
        editorList.remove(videoEditorInfo)
    }


    fun release(){
        editorList.forEach {
            it.joinHelper.getEditor()?.release()
        }
        editorList.clear()
    }
}