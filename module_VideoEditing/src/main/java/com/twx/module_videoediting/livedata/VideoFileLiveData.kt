package com.twx.module_videoediting.livedata

import com.twx.module_base.base.BaseLiveData
import com.twx.module_base.utils.lacksPermissions
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.livedata
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/13 16:48:48
 * @class describe
 */
object VideoFileLiveData:BaseLiveData<MutableList<MediaInformation>>() {


    fun getVideoInfo(){
        mScope.launch (Dispatchers.IO){
            if (lacksPermissions()) {
                postValue(FileUtils.getAllVideo())
            }
        }
    }



}