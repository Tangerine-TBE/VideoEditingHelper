package com.twx.module_videoediting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.twx.module_base.base.BaseViewModel
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.viewmodel
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/14 14:35:13
 * @class describe
 */
class SelectMediaViewModel:BaseViewModel() {

    val audioList by lazy {
        MutableLiveData<MutableList<MediaInformation>>()
    }



    fun getAudioData(){
        viewModelScope.launch(Dispatchers.IO) {
            audioList.postValue(FileUtils.getAllAudio())
        }
    }

}