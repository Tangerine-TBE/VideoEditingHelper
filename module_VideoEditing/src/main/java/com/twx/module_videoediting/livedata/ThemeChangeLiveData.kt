package com.twx.module_videoediting.livedata

import com.twx.module_base.base.BaseLiveData
import com.twx.module_videoediting.utils.Constants

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.livedata
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 13:39:22
 * @class describe
 */
object ThemeChangeLiveData:BaseLiveData<Boolean>() {


    fun setThemeType(){
        val state = !sp.getBoolean(Constants.SP_THEME_STATE, false)
        value=state
        sp.putBoolean(Constants.SP_THEME_STATE,state)
    }

    override fun onActive() {
        super.onActive()
        value=sp.getBoolean(Constants.SP_THEME_STATE, false)
    }


}