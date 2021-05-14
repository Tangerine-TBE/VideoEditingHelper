package com.twx.module_videoediting.utils

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import com.tencent.qcloud.ugckit.utils.AlbumSaver
import com.tencent.qcloud.ugckit.utils.CoverUtil
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.tools.RxTimeTool

import com.twx.module_videoediting.domain.MediaInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

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
                //   LogUtils.i("---getAllVideo--${bitmap}--${id}---${name}---${size}---${duration}-----${path}---${uri}---")
                videoList.add(MediaInformation(id,name,
                        duration,
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
     * 获取音频文件
     * @return MutableList<MediaInformation>
     */
    fun getAllAudio(): MutableList<MediaInformation> {
        val videoList = ArrayList<MediaInformation>()
        contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")?.apply {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val duration = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) // 时长
                val name = getString(getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))//名字
                val path = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) // 路径
                //   LogUtils.i("---getAllAudio---${id}---${name}-----${duration}---${date}-----${path}---${uri}---")
                videoList.add(MediaInformation(id, name, duration,
                    "${RxTimeTool.date2String(Date(File(path).lastModified()), SimpleDateFormat("yyyy-MM-dd"))}", uri.toString(), path,null))
            }
            close()
        }
        return videoList
    }

    /**
     * 获取音频文件
     * @return MutableList<MediaInformation>
     */
    fun getAllImage(): MutableList<MediaInformation> {
        val videoList = ArrayList<MediaInformation>()
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")?.apply {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val name = getString(getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))//名字
                val path = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) // 路径
                   LogUtils.i("---getAllAudio----${name}-------${path}        ---       ${uri}---")
                videoList.add(MediaInformation(id, name, 0L,
                    "${RxTimeTool.date2String(Date(File(path).lastModified()), SimpleDateFormat("yyyy-MM-dd"))}", uri.toString(), path,null))
            }
            close()
        }
        return videoList
    }


    /**
     * 重命名文件
     * @param uri Uri
     * @param name String
     * @param type MediaState
     * @return Int
     */
    fun reNameToMedia(uri:Uri,name:String,path:String):Int{
        return contentResolver.update(uri,contentValuesOf(MediaStore.Video.Media.DISPLAY_NAME to name,MediaStore.Video.Media.DATA to path), null, null)
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

    /**
     * 保持到相册
     * @param videoOutputPath String
     * @param coverImagePath String
     */
     fun saveAlbum(context: Context,videoOutputPath:String) {
        // 获取哪个视频的封面
        CoverUtil.getInstance().setInputPath(videoOutputPath)
        // 创建新的封面
        CoverUtil.getInstance().createThumbFile { coverPath ->
            val txVideoInfo = TXVideoInfoReader.getInstance(context).getVideoFileInfo(videoOutputPath)
            AlbumSaver.getInstance(context).setOutputProfile(videoOutputPath, txVideoInfo?.duration?:0L, coverPath)
            AlbumSaver.getInstance(context).saveVideoToDCIM()
        }
    }

    fun getVideoDuration(videoPath:String) =  MediaMetadataRetriever().apply {
        setDataSource(videoPath)
        }.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)



    suspend fun deleteDirectory(path: String){
        coroutineScope {
            val file = File(path)
            if (file.isDirectory) {
                val listFiles = file.listFiles()
                listFiles.forEach {
                    File(it.absolutePath).apply {
                        if (exists()) {
                            delete()
                        }
                    }
                   }
            }

        }
    }

}