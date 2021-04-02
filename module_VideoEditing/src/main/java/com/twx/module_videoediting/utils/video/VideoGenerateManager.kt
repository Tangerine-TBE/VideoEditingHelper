package com.twx.module_videoediting.utils.video

import android.graphics.BitmapFactory
import android.util.Log
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.basic.BaseGenerateKit
import com.tencent.qcloud.ugckit.module.editer.TailWaterMarkConfig
import com.tencent.qcloud.ugckit.module.editer.WaterMarkConfig
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.AlbumSaver
import com.tencent.qcloud.ugckit.utils.CoverUtil
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXGenerateResult
import com.tencent.ugc.TXVideoEditConstants.TXRect
import com.tencent.ugc.TXVideoEditer
import com.twx.module_videoediting.R

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.utils.video
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/1 18:37:27
 * @class describe
 */
object VideoGenerateManager:BaseGenerateKit(),IVideoGenerate, TXVideoEditer.TXVideoGenerateListener {
    private const val TAG = "VideoGenerateManager"
    private const val DURATION_TAILWATERMARK = 3
    private  var mCurrentState = 0
    private var mVideoOutputPath: String? = null
    private var mCoverPath: String? = null
    private var mVideoResolution = TXVideoEditConstants.VIDEO_COMPRESSED_720P
    private var mSaveToDCIM = false
    private var mCoverGenerate = false
    private var mWaterMark: WaterMarkConfig? = null
    private var mTailWaterMarkConfig: TailWaterMarkConfig? = null

    override fun setVideoResolution(resolution: Int) {
        mVideoResolution=resolution
    }

    override fun setCustomVideoBitrate(videoBitrate: Int) {
        VideoEditorsManager.getEditer()?.setVideoBitrate(videoBitrate)
    }

    override fun startGenerate() {
        mCurrentState = PlayState.STATE_GENERATE
        mVideoOutputPath = VideoPathUtil.generateVideoPath()
        Log.d(TAG, "startGenerate mVideoOutputPath:$mVideoOutputPath")

        val startTime = VideoEditorsManager.getCutterStartTime()
        val endTime = VideoEditorsManager.getCutterEndTime()

        val editer = VideoEditorsManager.getEditer()
        if (editer != null) {
            editer.setCutFromTime(startTime, endTime)
            editer.setVideoGenerateListener(this)
            if (mWaterMark != null) {
                editer.setWaterMark(mWaterMark!!.watermark, mWaterMark!!.rect)
            }
            if (mTailWaterMarkConfig != null) {
                editer.setTailWaterMark(mTailWaterMarkConfig!!.tailwatermark, mTailWaterMarkConfig!!.rect, mTailWaterMarkConfig!!.duration)
            }
            when (mVideoResolution) {
                TXVideoEditConstants.VIDEO_COMPRESSED_360P -> editer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_360P, mVideoOutputPath)
                TXVideoEditConstants.VIDEO_COMPRESSED_480P -> editer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_480P, mVideoOutputPath)
                TXVideoEditConstants.VIDEO_COMPRESSED_540P -> editer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_540P, mVideoOutputPath)
                else -> editer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, mVideoOutputPath)
            }
        }
    }

    override fun stopGenerate() {
        //FIXBUG:如果上一次如生成缩略图没有停止，先停止，在进行下一次生成
        val editer = VideoEditorsManager.getEditer()
        if (editer != null) {
            editer.cancel()
            editer.setVideoGenerateListener(null)
        }
        if (mCurrentState == PlayState.STATE_GENERATE) {
            ToastUtil.toastShortMessage(UGCKit.getAppContext().resources.getString(R.string.tc_video_editer_activity_cancel_video_generation))
            mCurrentState = PlayState.STATE_NONE
            if (mOnUpdateUIListener != null) {
                mOnUpdateUIListener.onUICancel()
            }
        }
    }

    override fun addTailWaterMark() {
        val info = VideoEditorsManager.getTXVideoInfo()
        if (info == null) {
            Log.e(TAG, "addTailWaterMark info is null")
            return
        }
        val tailWaterMarkBitmap = BitmapFactory.decodeResource(UGCKit.getAppContext().resources, R.drawable.tcloud_logo)
        val widthHeightRatio = tailWaterMarkBitmap.width / tailWaterMarkBitmap.height.toFloat()

        val rect = TXRect()
        // 归一化的片尾水印，这里设置了一个固定值，水印占屏幕宽度的0.25。
        // 归一化的片尾水印，这里设置了一个固定值，水印占屏幕宽度的0.25。
        rect.width = 0.25f
        // 后面根据实际图片的宽高比，计算出对应缩放后的图片的宽度：txRect.width * videoInfo.width 和高度：txRect.width * videoInfo.width / widthHeightRatio，然后计算出水印放中间时的左上角位置
        // 后面根据实际图片的宽高比，计算出对应缩放后的图片的宽度：txRect.width * videoInfo.width 和高度：txRect.width * videoInfo.width / widthHeightRatio，然后计算出水印放中间时的左上角位置
        rect.x = (info.width - rect.width * info.width) / (2f * info.width)
        rect.y = (info.height - rect.width * info.width / widthHeightRatio) / (2f * info.height)

        val editer = VideoEditorsManager.getEditer()
        editer?.setTailWaterMark(tailWaterMarkBitmap, rect, DURATION_TAILWATERMARK)
    }

    override fun getVideoOutputPath(): String? {
        return mVideoOutputPath
    }

    override fun getCoverPath(): String? {
        return mCoverPath
    }

    override fun saveVideoToDCIM(flag: Boolean) {
        mSaveToDCIM = flag
    }

    override fun setCoverGenerate(coverGenerate: Boolean) {
            mCoverGenerate = coverGenerate
    }

    override fun setWaterMark(waterMark: WaterMarkConfig) {
        mWaterMark = waterMark
    }

    override fun setTailWaterMark(tailWaterMarkConfig: TailWaterMarkConfig) {
        mTailWaterMarkConfig = tailWaterMarkConfig
    }

    override fun release() {
        // SDK释放资源
        val editer = VideoEditorsManager.getEditer()
        if (editer != null) {
            editer.setVideoGenerateListener(null)
            editer.release()
        }
        VideoEditorsManager.clear()
    }


    override fun onGenerateProgress(progress: Float) {
            mOnUpdateUIListener?.onUIProgress(progress)
    }

    override fun onGenerateComplete(result: TXGenerateResult) {
        mCurrentState = PlayState.STATE_NONE
        if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
            if (mCoverGenerate) {
                Log.d(TAG, "onGenerateComplete outputPath:$mVideoOutputPath")
                // 获取哪个视频的封面
                CoverUtil.getInstance().setInputPath(mVideoOutputPath)
                // 创建新的封面
                CoverUtil.getInstance().createThumbFile { coverPath ->
                    mCoverPath = coverPath
                    Log.d(TAG, "onGenerateComplete coverPath:$coverPath")
                    saveAndUpdate(result)
                    release()
                }
            } else {
                saveAndUpdate(result)
                release()
            }
        }
    }
    private fun saveAndUpdate(result: TXGenerateResult) {
        if (mSaveToDCIM) {
            val duration = VideoEditorsManager.getVideoDuration()
            AlbumSaver.getInstance(UGCKit.getAppContext()).setOutputProfile(mVideoOutputPath, duration, mCoverPath)
            AlbumSaver.getInstance(UGCKit.getAppContext()).saveVideoToDCIM()
        }
        // UI更新
        if (mOnUpdateUIListener != null) {
            mOnUpdateUIListener.onUIComplete(result.retCode, result.descMsg)
        }
    }


}