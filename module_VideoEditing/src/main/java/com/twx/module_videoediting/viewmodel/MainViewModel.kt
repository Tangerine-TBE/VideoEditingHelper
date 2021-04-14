package com.twx.module_videoediting.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.twx.module_base.base.BaseViewModel
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.domain.ValueReName
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


    val renameState by lazy {
        MutableLiveData<ValueReName>()
    }

    fun setSelectItems(listBean: HashSet<MediaInformation>) {
        selectItems.value = listBean
    }


     val currentVideoList by lazy {
        MutableLiveData<MutableList<MediaInformation>>()
    }

    fun deleteMediaFile(mediaList:MutableList<MediaInformation>,deleteList:MutableList<MediaInformation>) {
        val list = ArrayList<MediaInformation>()
        list.addAll(deleteList)
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                FileUtils.deleteMedia(Uri.parse(it.uri))
                FileUtils.deleteFile(File(it.path))
            }
        }
        if (mediaList.removeAll(deleteList)) {
            currentVideoList.value=mediaList
        }
        LogUtils.i("---deleteMediaFile-----${mediaList.size}------${deleteList.size}----------")
    }


    fun deleteMediaFile(uri: Uri,path:String){
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val deleteMedia = FileUtils.deleteMedia(uri)
                val deleteFile = FileUtils.deleteFile(File(path))
                LogUtils.i("------deleteMedia--------$deleteMedia----------------------")
            }
        }catch (e:Exception){

        }

    }


    fun reNameToMediaFile(name:String,msg:MediaInformation,position: Int){
        viewModelScope.launch {
            var destPath: String
            val updateState = withContext(Dispatchers.IO) {
                LogUtils.i("-reNameToMediaFile---  ${ msg.name}  ------------ ${ msg.path} --------------")
                val typeIndex = msg.path.lastIndexOf(".")
                val pathIndex = msg.path.lastIndexOf("/")

                val realType = msg.path.substring(typeIndex)
                val realPath = msg.path.substring(0, pathIndex)

                destPath= "${realPath}/${name}${realType}"
                File(msg.path).renameTo(File(destPath))
            }
            withContext(Dispatchers.IO){
                if (updateState) {
                    renameState.postValue(ValueReName(FileUtils.reNameToMedia(Uri.parse(msg.uri), name,destPath)>0,name,msg,position))
                }
            }
        }
    }



}