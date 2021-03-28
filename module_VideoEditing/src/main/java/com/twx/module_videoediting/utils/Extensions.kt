package com.twx.module_videoediting.utils

import android.app.Activity
import com.twx.module_videoediting.ui.widget.TitleBar


fun TitleBar.setBarEventAction(activity: Activity?,block:()->Unit){
    setOnBarActionListener(object :TitleBar.OnBarActionListener{
        override fun backAction() {
            activity?.finish()
        }

        override fun rightAction() {
            block()
        }

    })
}