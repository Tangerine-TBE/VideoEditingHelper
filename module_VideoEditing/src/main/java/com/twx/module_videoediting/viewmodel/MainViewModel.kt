package com.twx.module_videoediting.viewmodel

import androidx.lifecycle.MutableLiveData
import com.twx.module_base.base.BaseViewModel

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.viewmodel
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/23 16:55:16
 * @class describe
 */
class MainViewModel:BaseViewModel() {

    val makeFinishState by lazy {
        MutableLiveData(true)
    }


    fun setMakeState(state:Boolean){
        makeFinishState.value=state
    }


    fun getMakeState()=makeFinishState.value?:true

}