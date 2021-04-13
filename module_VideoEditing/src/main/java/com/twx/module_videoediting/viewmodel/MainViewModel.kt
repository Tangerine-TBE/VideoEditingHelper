package com.twx.module_videoediting.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.twx.module_base.base.BaseViewModel
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.livedata.VideoFileLiveData
import com.twx.module_videoediting.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.viewmodel
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/23 16:55:16
 * @class describe
 */
class MainViewModel:BaseViewModel() {

    val editAction by lazy {
        MutableLiveData(false)
    }

    val selectAllState by lazy {
        MutableLiveData(false)
    }

    fun getEditAction_(): Boolean = editAction.value ?: false

    fun setEditAction(action: Boolean) {
        editAction.value = action
    }


    fun setSelectAllState(state:Boolean){
        selectAllState.value=state
    }

    val selectItems by lazy {
        MutableLiveData<HashSet<MediaInformation>>()
    }



    fun setSelectItems(listBean: HashSet<MediaInformation>) {
        selectItems.value = listBean
    }


     val currentVideoList by lazy {
        MutableLiveData<MutableList<MediaInformation>>()
    }

    fun deleteMediaFile(mediaList:MutableList<MediaInformation>,deleteList:MutableList<MediaInformation>) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteList.forEach {
                FileUtils.deleteMedia(Uri.parse(it.uri))
                FileUtils.deleteFile(File(it.path))
            }

        }
        LogUtils.i("---deleteMediaFile-----${mediaList.size}------${deleteList.size}----------")
        if (mediaList.removeAll(deleteList)) {
            currentVideoList.value=mediaList
        }

    }


}