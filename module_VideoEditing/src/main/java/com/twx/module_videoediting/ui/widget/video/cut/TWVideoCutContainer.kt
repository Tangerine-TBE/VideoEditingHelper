package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitImpl
import com.tencent.qcloud.ugckit.basic.JumpActivityMgr
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.module.PlayerManagerKit
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.VideoGenerateKit
import com.tencent.qcloud.ugckit.module.cut.IVideoCutKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.BackgroundTasks
import com.tencent.qcloud.ugckit.utils.DialogUtil
import com.tencent.qcloud.ugckit.utils.TelephonyUtil
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoCutContainerBinding
import com.twx.module_videoediting.ui.widget.video.BaseVideoUi
import com.twx.module_videoediting.utils.video.PlayerManager
import com.twx.module_videoediting.utils.formatVideoTime
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
) : BaseVideoUi(context, attrs, defStyleAttr), IVideoCut, PlayerManager.OnPreviewListener, PlayerManager.OnPlayStateListener{
    private val binding= DataBindingUtil.inflate<LayoutVideoCutContainerBinding>(LayoutInflater.from(context), R.layout.layout_video_cut_container, this, true)
    private var mDuration=""
    private val mVideoEditerSDK by lazy {
        VideoEditerSDK.getInstance()
    }

    init {
        mVideoEditerSDK.apply {
            releaseSDK()
            clear()
            initSDK()
            initEvent()
        }
    }

    override fun setVideoPath(videoPath: String?) {
        if (TextUtils.isEmpty(videoPath)) {
            ToastUtil.toastShortMessage(resources.getString(R.string.tc_video_cutter_activity_oncreate_an_unknown_error_occurred_the_path_cannot_be_empty))
            return
        }
        binding.apply {
            mVideoEditerSDK.setVideoPath(videoPath)
            // 初始化播放器界面[必须在setPictureList/setVideoPath设置数据源之后]
            mVideoPlayerView.initPlayerLayout()
            // 加载视频基本信息
            loadVideoInfo(videoPath)
        }
        PlayerManager.addOnPlayStateListener(this)
        PlayerManager.addOnPreviewListener(this)
    }

    override fun startPlay() {
        PlayerManager.startPlay()
    }

    override fun stopPlay() {
        PlayerManager.stopPlay()
        val editFlag = JumpActivityMgr.getInstance().editFlagFromCut
        if (editFlag) {
            ProcessKit.getInstance().stopProcess()
        } else {
            VideoGenerateKit.getInstance().stopGenerate()
        }
    }

    override fun pausePlay() {
        PlayerManager.pausePlay()
    }

    override fun resumePlay() {
        PlayerManager.resumePlay()
    }

    private fun initEvent() {
        binding.playerControl.apply {
            videoPlayAction.setOnClickListener {
                when (PlayerManager.getCurrentState()) {
                    PlayState.STATE_PLAY,PlayState.STATE_RESUME ->pausePlay()
                    PlayState.STATE_PAUSE->resumePlay()
                    PlayState.STATE_STOP ->startPlay()
                    PlayState.STATE_NONE->startPlay()
                }
            }
        }
    }

    override fun release() {
        TelephonyUtil.getInstance().uninitPhoneListener()
        PlayerManager.removeOnPreviewListener(this)
        PlayerManager.removeOnPlayStateListener(this)
    }


    private fun loadVideoInfo(videoPath: String?) {
        // 加载视频信息
        val info = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(videoPath)
        if (info == null) {
            DialogUtil.showDialog(UGCKitImpl.getAppContext(), resources.getString(R.string.tc_video_cutter_activity_video_main_handler_edit_failed), resources.getString(R.string.ugckit_does_not_support_android_version_below_4_3), null)
        } else {
            mVideoEditerSDK.txVideoInfo = info
            binding.mVideoCutLayout.apply {
                setVideoInfo(info)
                setOnRotateVideoListener { rotation -> VideoEditerSDK.getInstance().editer.setRenderRotation(rotation) }
            }
            loadThumbnail()
            //总时长
            mDuration=formatVideoTime(info.duration/1000)
        }
    }


    private fun loadThumbnail() {
        mVideoEditerSDK.let {
            binding.mVideoCutLayout.clearThumbnail()
            mScope.launch(Dispatchers.IO){
            // 初始化缩略图列表，裁剪缩略图时间间隔秒钟一张
                val interval = videoTimeInterval(it.txVideoInfo.duration)
                it.initThumbnailList({ index, timeMs, bitmap ->
                    mScope.launch(Dispatchers.Main) {
                        binding.mVideoCutLayout.addThumbnail(index, bitmap)
                    }
                           /* if (it.txVideoInfo != null) {
                                val size = (it.txVideoInfo.duration / interval).toInt()
                                if (index == size - 1) { // Note: index从0开始增长
                                    //  mComplete = true
                                }
                            }*/
                }, interval)
            }
        }
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
                    //    mProgressFragmentUtil.updateGenerateProgress((progress * 100).toInt())
                }

                override fun onUIComplete(retCode: Int, descMsg: String) {
                    //    mProgressFragmentUtil.dismissLoadingProgress()
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
                    // mProgressFragmentUtil.updateGenerateProgress((progress * 100).toInt())
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
        binding.playerControl.videoTime.text = "${formatVideoTime(time.toLong()/1000)}/$mDuration"
    }

    override fun onPreviewFinish() {
        // 循环播放
        // PlayerManager.getInstance().startPlay()
    }

    override fun onPlayState(state: Int) {
        binding.playerControl.apply {
            LogUtils.i("------onPlayState-------------$state-------------------")
            when(state){
                PlayState.STATE_PLAY,PlayState.STATE_RESUME ->{
                    videoPlayAction.setImageResource(R.mipmap.icon_player_stop)
                }
                PlayState.STATE_STOP, PlayState.STATE_PAUSE, PlayState.STATE_NONE -> {
                    videoPlayAction.setImageResource(R.mipmap.icon_player_start)
                }

            }
        }
    }

}