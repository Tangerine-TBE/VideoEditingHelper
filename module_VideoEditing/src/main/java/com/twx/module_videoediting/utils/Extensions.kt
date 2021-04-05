package com.twx.module_videoediting.utils

import android.app.Activity
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.twx.module_videoediting.ui.widget.TitleBar


fun TitleBar.setBarEventAction(activity: Activity?, block: () -> Unit){
    setOnBarActionListener(object : TitleBar.OnBarActionListener {
        override fun backAction() {
            activity?.finish()
        }

        override fun rightAction() {
            block()
        }

    })
}

fun formatVideoTime(timeTemp: Long): String {
    val second = timeTemp % 60
    val minuteTemp = timeTemp / 60
    return if (minuteTemp > 0) {
        val minute = minuteTemp % 60
        val hour = minuteTemp / 60
        if (hour > 0) {
            ((if (hour > 10) hour.toString() + "" else "0$hour") + ":" + (if (minute >= 10) minute.toString() + "" else "0$minute")
                    + ":" + if (second >= 10) second.toString() + "" else "0$second")
        } else {
            ("00:" + (if (minute >= 10) minute.toString() + "" else "0$minute") + ":"
                    + if (second >= 10) second.toString() + "" else "0$second")
        }
    } else {
        "00:00:" + if (second >= 10) second.toString() + "" else "0$second"
    }
}


fun formatDuration(durationMs: Long): String {
    val duration = durationMs / 1000
    val h = duration / 3600
    val m = (duration - h * 3600) / 60
    val s = duration - (h * 3600 + m * 60)
    return if (h == 0L) {
        DateTimeUtil.asTwoDigit(m) + ":" + DateTimeUtil.asTwoDigit(s)
    } else {
        DateTimeUtil.asTwoDigit(h) + ":" + DateTimeUtil.asTwoDigit(m) + ":" + DateTimeUtil.asTwoDigit(s)
    }
}


fun videoTimeInterval(duration: Long):Int{
    val time = duration / 1000/ 30
    return if (duration / 1000<20) {
            1000
    } else {
        if (time > 0) {
            time.toInt()*2000
        } else {
            2000
        }
    }



}

