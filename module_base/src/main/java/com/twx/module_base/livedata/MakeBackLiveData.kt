package com.twx.module_base.livedata

import com.twx.module_base.base.BaseLiveData

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.livedata
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/13 11:11:54
 * @class describe
 */
object MakeBackLiveData:BaseLiveData<Boolean>() {

    fun setMakeState(state:Boolean){
        value=state
    }

    fun getMakeState()=value?:true


}