package com.twx.module_videoediting.viewmodel

import androidx.lifecycle.MutableLiveData
import com.twx.module_base.base.BaseViewModel
import com.twx.module_videoediting.domain.ReadyJoinInfo

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.viewmodel
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/10 17:04:03
 * @class describe
 */
class ReadyJoinViewModel:BaseViewModel() {

    val readyList by lazy {
      MutableLiveData<MutableList<ReadyJoinInfo>>(arrayListOf())
    }



    fun setReadyList(list: MutableList<ReadyJoinInfo>){
        readyList.value=list
    }

}