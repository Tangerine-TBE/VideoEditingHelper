package com.twx.module_base.widget.popup

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.twx.module_base.R
import com.twx.module_base.base.BasePopup
import com.twx.module_base.databinding.PopupLoadingWindowBinding


/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.popup
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/6 16:52:41
 * @class describe
 */
class LoadingPopup(activity: FragmentActivity?):BasePopup<PopupLoadingWindowBinding>(activity, R.layout.popup_loading_window,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT) {

    init {
        isFocusable = false
        isOutsideTouchable = false
    }



    fun setProgress(progress:Int){
        mView.loadingView.setProgress(progress)
    }

    fun setTitle(title:String){
        mView.loadingTitle.text=title
    }

    fun cancelMake(block:()->Unit){
        mView.cancelMake.setOnClickListener {
            block()
            dismiss()
        }
    }

}