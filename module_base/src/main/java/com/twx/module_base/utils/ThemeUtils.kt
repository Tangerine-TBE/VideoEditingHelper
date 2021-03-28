package com.twx.module_base.utils

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.twx.module_base.R

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.utils
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/24 13:44:54
 * @class describe
 */

fun View.setHomeNavigationBgColor(state: Boolean){
    setBackgroundColor(ContextCompat.getColor(context, if (state)  R.color.white else R.color.color_home_bottom_false))
}


fun View.setBgDrawableStyle(state: Boolean,normal:Int,change:Int) {
    background=ContextCompat.getDrawable(context,if (state)
        change else normal)
}

fun View.setHomeThemeBgColor(state: Boolean){
    setBackgroundColor(ContextCompat.getColor(context, if (state)  R.color.color_home_bg else R.color.black))
}



fun viewThemeColor(state: Boolean=SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE), vararg view: View, normal:Int=R.color.white, change:Int=R.color.black){
    view.forEach {
        if (it is TextView){
            it.setTextColor(ContextCompat.getColor(it.context, if (state)  change else normal))
        }
        if (it is ImageView){
            it.setColorFilter(ContextCompat.getColor(it.context, if (state) change else normal))
        }

        if (it is ViewGroup){
            it.setBackgroundColor(ContextCompat.getColor(it.context, if (state) normal else change))
        }

    }
}

fun textViewThemeDrawable(state: Boolean=SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE),vararg view: TextView, normal:Int=R.mipmap.icon_login_logo_false, change:Int=R.mipmap.icon_login_logo_true){
    view.forEach {
        it.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(it.context, if (state)  change else normal), null, null);
    }
}







