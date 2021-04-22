package com.twx.module_videoediting.utils

import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.qcloud.ugckit.utils.VideoUtil
import com.twx.module_base.utils.LogUtils
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import javax.security.auth.callback.Callback

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/19 10:14:50
 * @class describe
 */
object FFmpegHelper {

    private val helper=RxFFmpegInvoke.getInstance()


    fun startCommand(command:Array<String?>,callback:RxFFmpegInvoke.IFFmpegListener){
        helper.runCommand(command,callback)
    }


    fun exitCommand(){
        helper.exit()
    }

    fun clean(){
        helper.onClean()
    }


    fun changeVideoSpeed(speed:Float=1.0f,srcFile:String,targetFile:String): Array<String?>{
        var command = "ffmpeg -y -i %s -filter_complex [0:v]setpts=%s*PTS[v];[0:a]atempo=%s[a] -map [v] -map [a] %s"
        command = String.format(command, srcFile,(1/speed).toString(),if (speed>2.0) "2.0" else speed.toString(),targetFile)
        LogUtils.i("-FFmpegHelper---changeVideoSpeed------- $command")
        return command.split(" ").toTypedArray()
    }



    fun divisionVideo(beginTime:Long,endTime:Long,srcFile:String,targetFile:String): Array<String?>{
        LogUtils.i("--FFmpegHelper--divisionVideo------- $beginTime---------$endTime")
       // formatDuration(beginTime)
        val beginTimeStr =beginTime/1000f
        val endTimeStr =(endTime-beginTime)/1000f
        var command ="ffmpeg -ss %s -t %s -i %s -vcodec copy -acodec copy %s"
        command =  String.format(command,beginTimeStr.toString(),endTimeStr.toString(),srcFile,targetFile)
        LogUtils.i("--FFmpegHelper--divisionVideo------- $command")
        return command.split(" ").toTypedArray()
    }





}