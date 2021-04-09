package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.util.TimeUtils.formatDuration
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitImpl
import com.tencent.qcloud.ugckit.basic.JumpActivityMgr
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.module.PlayerManagerKit
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.VideoGenerateKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.tencent.qcloud.ugckit.utils.DialogUtil
import com.tencent.qcloud.ugckit.utils.TelephonyUtil
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.showToast
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoCutContainerBinding
import com.twx.module_videoediting.domain.ThumbnailInfo
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoUi
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager
import com.twx.module_videoediting.utils.videoTimeInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 9:59:44
 * @class describe
 */
class TWVideoCutContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoUi(context, attrs, defStyleAttr), IVideoCut, PlayerManager.OnPreviewListener,
    PlayerManager.OnPlayStateListener, ICutView.VideoProgressSeekListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoCutContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_cut_container,
        this,
        true
    )
    private var mDuration = ""
    private var mCurrentTime = 0L

    init {
        initEvent()
    }

    /**
     * 事件监听
     */
     fun initEvent() {
        binding.apply {
            mCutViewLayout.setVideoProgressSeekListener(this@TWVideoCutContainer)

            // 播放动作
            playerControl.apply {
                videoPlayAction.setOnClickListener {
                    PlayerManager.playVideo(false)
                }
            }

            //设置剪辑起点终点动作
            cutControl.apply {
                beginAction.setOnClickListener {
                    if (mCurrentTime < mVideoEditorHelper.cutterEndTime) {
                        mVideoEditorHelper.cutterStartTime = mCurrentTime
                        showCutTime(mVideoEditorHelper)
                    } else {
                        showToast("剪辑起点不能大于剪辑终点")
                    }
                }

                endAction.setOnClickListener {
                    if (mCurrentTime > mVideoEditorHelper.cutterStartTime) {
                        mVideoEditorHelper.cutterEndTime = mCurrentTime
                        showCutTime(mVideoEditorHelper)
                    } else {
                        showToast("剪辑终点不能小于剪辑起点")
                    }
                }
            }
        }
    }


    override fun setVideoPath(videoPath: String?) {
        /* if (TextUtils.isEmpty(videoPath)) {
             ToastUtil.toastShortMessage(resources.getString(R.string.tc_video_cutter_activity_oncreate_an_unknown_error_occurred_the_path_cannot_be_empty))
             return
         }*/
        mVideoEditorHelper.let {
            binding.apply {
                // it.setVideoPath(videoPath)
                // 加载视频基本信息
                //   loadVideoInfo(videoPath)
                loadVideoInfo(videoPath)
                // 初始化播放器界面[必须在setPictureList/setVideoPath设置数据源之后]
                mVideoPlayerView.initPlayerLayout()

                it.resetDuration()

                //添加播放状态监听
                PlayerManager.addOnPlayStateListener(this@TWVideoCutContainer)
                PlayerManager.addOnPreviewListener(this@TWVideoCutContainer)
                //设置开始的剪辑时间
                //     it.setCutterStartTime(0L, it.txVideoInfo.duration)

                showCutTime(it)
            }
        }
    }


    //设置开始结束剪辑时间
    private fun LayoutVideoCutContainerBinding.showCutTime(it: VideoEditerSDK) {
        cutControl.apply {
            beginTime.text = formatDuration(it.cutterStartTime)
            endTime.text = formatDuration(it.cutterEndTime)
            timeInterval.text = formatDuration(it.geCutterDuration())
        }
    }

    /**
     * 设置视频信息
     * @param videoPath String?
     */
    private fun loadVideoInfo(videoPath: String?) {
        // 加载视频信息
        //  val info = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(videoPath)
        val info = mVideoEditorHelper.txVideoInfo
        if (info == null) {
            DialogUtil.showDialog(
                UGCKitImpl.getAppContext(),
                resources.getString(R.string.tc_video_cutter_activity_video_main_handler_edit_failed),
                resources.getString(R.string.ugckit_does_not_support_android_version_below_4_3),
                null
            )
        } else {
            //   mVideoEditer.txVideoInfo = info
            // 初始化缩略图列表，裁剪缩略图时间间隔秒钟一张
            val interval = videoTimeInterval(info.duration)
            binding.mCutViewLayout.setTotalDuration(info.duration)
            binding.mCutViewLayout.setAllThumbnailListWidth((info.duration / interval).toInt())
            loadThumbnail(interval)
            //总时长
            mDuration = formatDuration(info.duration)
        }
    }

    /**
     * 加载缩略图
     * @param interval Int
     */
    private fun loadThumbnail(interval: Int) {
        mVideoEditorHelper.let {
            binding.mCutViewLayout.setThumbnailList(it.allThumbnailList)

            /*   mScope.launch(Dispatchers.IO) {
                   it.initThumbnailList({ index, timeMs, bitmap ->
                       mScope.launch(Dispatchers.Main) {
                           LogUtils.i("--loadThumbnail---$index--$timeMs---------------------")
                           binding.mCutViewLayout.addThumbnail(index, ThumbnailInfo(timeMs,bitmap))
                       }
                   }, interval)
               }*/
        }
    }


    override fun release() {
        super.release()
        TelephonyUtil.getInstance().uninitPhoneListener()
        PlayerManager.removeOnPreviewListener(this)
        PlayerManager.removeOnPlayStateListener(this)
    }

    override fun startExportVideo() {
        PlayerManagerKit.getInstance().stopPlay()
        //如果图片没有加载完，先停止加载
        ProcessKit.getInstance().stopProcess()
        val editFlag = JumpActivityMgr.getInstance().editFlagFromCut
        if (editFlag) {
            ProcessKit.getInstance().startProcess()
        } else {
            VideoGenerateKit.getInstance().startGenerate()
        }
    }

    override fun stopExportVideo() {

    }


    override fun setOnCutListener(listener: IVideoCut.OnCutListener?) {
        if (listener == null) {
            ProcessKit.getInstance().setOnUpdateUIListener(null)
            VideoGenerateKit.getInstance().setOnUpdateUIListener(null)
            return
        }
        val editFlag = JumpActivityMgr.getInstance().editFlagFromCut
        // 设置生成的监听器，用来更新控件
        if (editFlag) {
            // 裁剪后进入编辑
            ProcessKit.getInstance().setOnUpdateUIListener(object : OnUpdateUIListener {
                override fun onUIProgress(progress: Float) {
                    // mProgressFragmentUtil.updateGenerateProgress((progress * 100).toInt())
                    LogUtils.i("-----setOnCutListener-------------${(progress * 100).toInt()}-------------")
                    listener.onCutterProgress(progress)
                }

                override fun onUIComplete(retCode: Int, descMsg: String) {
                    //  mProgressFragmentUtil.dismissLoadingProgress()
                    if (listener != null) {
                        val ugcKitResult = UGCKitResult()
                        ugcKitResult.errorCode = retCode
                        ugcKitResult.descMsg = descMsg
                        listener.onCutterCompleted(ugcKitResult)
                    }
                }

                override fun onUICancel() {
                    if (listener != null) {
                        listener.onCutterCanceled()
                    }
                }
            })
        } else {
            // 裁剪后输出视频
            VideoGenerateKit.getInstance().setOnUpdateUIListener(object : OnUpdateUIListener {
                override fun onUIProgress(progress: Float) {
                    //    mProgressFragmentUtil.updateGenerateProgress((progress * 100).toInt())
                    LogUtils.i("-----setOnCutListener-------------${(progress * 100).toInt()}-------------")
                }

                override fun onUIComplete(retCode: Int, descMsg: String) {
                    //    mProgressFragmentUtil.dismissLoadingProgress()
                    if (listener != null) {
                        val ugcKitResult = UGCKitResult()
                        ugcKitResult.errorCode = retCode
                        ugcKitResult.descMsg = descMsg
                        ugcKitResult.outputPath = VideoGenerateKit.getInstance().videoOutputPath
                        ugcKitResult.coverPath = VideoGenerateKit.getInstance().coverPath
                        listener.onCutterCompleted(ugcKitResult)
                    }
                }

                override fun onUICancel() {
                    if (listener != null) {
                        listener.onCutterCanceled()
                    }
                }
            })
        }
    }


    override fun setVideoEditFlag(flag: Boolean) {
        JumpActivityMgr.getInstance().editFlagFromCut = flag
    }

    override fun getVideoOutputPath(): String {
        return VideoGenerateKit.getInstance().videoOutputPath
    }

    override fun onPreviewProgress(time: Int) {
        LogUtils.i("----time--------onPreviewProgress-------$time------------")
        mCurrentTime = time.toLong()
        binding.playerControl.videoTime.text = "${formatDuration(time.toLong())}/$mDuration"

        val currentState = PlayerManager.getCurrentState()
        if (currentState == PlayState.STATE_PLAY || currentState == PlayState.STATE_RESUME) {
            binding.mCutViewLayout.setCurrentTime(time.toLong())
        }

    }

    override fun onPreviewFinish() {

    }

    override fun onPlayState(state: Int) {
        binding.playerControl.apply {
            LogUtils.i("------onPlayState-------------$state-------------------")
            when (state) {
                PlayState.STATE_PLAY, PlayState.STATE_RESUME -> {
                    videoPlayAction.setImageResource(R.mipmap.icon_player_stop)
                }
                PlayState.STATE_STOP, PlayState.STATE_PAUSE, PlayState.STATE_NONE -> {
                    videoPlayAction.setImageResource(R.mipmap.icon_player_start)
                }

            }
        }
    }

    override fun onVideoProgressSeek(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }

    override fun onVideoProgressSeekFinish(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }

}