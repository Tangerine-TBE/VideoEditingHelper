package com.twx.module_videoediting.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.tamsiree.rxkit.RxTimeTool
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.formatTime
import com.twx.module_videoediting.domain.MediaInformation
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/13 11:52:19
 * @class describe
 */
object FileUtils {
    private val contentResolver = BaseApplication.application.contentResolver
    /**
     * 获取视频文件
     * @return MutableList<MediaInformation>
     */
    fun getAllVideo(): MutableList<MediaInformation> {
        val videoList = ArrayList<MediaInformation>()
        val selection = MediaStore.Video.Media.DATA + " like ?"
        contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,selection, arrayOf("${VideoPathUtil.createPath()}"+"%"),  "${MediaStore.MediaColumns.DATE_ADDED} desc")?.apply {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val duration = getLong(getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)) // 时长
                val name = getString(getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))//名字
                val size = getLong(getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))//大小
                val path = getString(getColumnIndexOrThrow(MediaStore.Video.Media.DATA)) // 路径
                val bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null)//缩略图
                   LogUtils.i("---getAllVideo--${bitmap}--${id}---${name}---${size}---${duration}-----${path}---${uri}---")
                videoList.add(MediaInformation(id,name,
                        "${formatDuration(duration )}",
                        "${RxTimeTool.date2String(Date(File(path).lastModified()), SimpleDateFormat("yyyy-MM-dd"))}",
                        path,
                        uri.toString(),
                        bitmap))
            }
            close()
        }
        return videoList
    }

    /**
     * 删除文件
     * @param uri Uri
     * @return Int
     */
    fun deleteMedia(uri: Uri)= contentResolver.delete(uri,null,null)


    /**
     * 删除文件
     * @param file File
     * @return Boolean
     */
    fun deleteFile(file:File)=if (file.exists()) {
        file.delete()
    } else {
        false
    }
}