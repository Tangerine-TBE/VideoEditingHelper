package com.twx.module_videoediting.ui.widget.video.cut

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tencent.qcloud.ugckit.UGCKitImpl
import com.tencent.qcloud.ugckit.basic.JumpActivityMgr
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.component.slider.RangeSlider
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.VideoGenerateKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.qcloud.ugckit.module.effect.utils.Edit
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.DialogUtil
import com.tencent.qcloud.ugckit.utils.TelephonyUtil
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.showToast
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoCutContainerBinding
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseVideoEditUi
import com.twx.module_videoediting.utils.formatDuration
import com.twx.module_videoediting.utils.video.PlayerManager
import com.twx.module_videoediting.utils.videoTimeInterval

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/29 9:59:44
 * @class describe
 */
class TWVideoEditCutContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseVideoEditUi(context, attrs, defStyleAttr), IVideoCut, ICutView.VideoProgressSeekListener, PlayerManager.OnPreviewListener, Edit.OnCutChangeListener {
    private val binding = DataBindingUtil.inflate<LayoutVideoCutContainerBinding>(
        LayoutInflater.from(context),
        R.layout.layout_video_cut_container,
        this,
        true
    )

    private var currentTime=0L

    init {
        PlayerManager.addOnPreviewListener(this)
        initEvent()
    }

    /**
     * 事件监听
     */
    fun initEvent() {

        binding.apply {
            cutControl.apply {
                mCutViewLayout.setVideoProgressSeekListener(this@TWVideoEditCutContainer)
                viewThemeColor(themeState,beginTime,endTime,timeInterval)

                //设置剪辑起点终点动作
                beginAction.setOnClickListener {
                    if (mVideoPlayerView.getCurrentTime() < mVideoEditorHelper.cutterEndTime) {
                        mVideoEditorHelper.cutterStartTime = mVideoPlayerView.getCurrentTime()
                        showCutTime()
                    } else {
                        showToast("剪辑起点不能大于剪辑终点")
                    }
                }

                endAction.setOnClickListener {
                    if (mVideoPlayerView.getCurrentTime() > mVideoEditorHelper.cutterStartTime) {
                        mVideoEditorHelper.cutterEndTime = mVideoPlayerView.getCurrentTime()
                        showCutTime()
                    } else {
                        showToast("剪辑终点不能小于剪辑起点")
                    }
                }


            }
        }
    }

    override fun initPlayerLayout() {
        mVideoEditorHelper.let {
            binding.apply {
                loadVideoInfo()
                // 初始化播放器界面[必须在setPictureList/setVideoPath设置数据源之后]
                mVideoPlayerView.initPlayerLayout()
                it.resetDuration()

                showCutTime()

                sliderView()
            }
        }
    }

    private fun LayoutVideoCutContainerBinding.sliderView() {
        mVideoPlayerView.previewProgressAction {
            val currentState = PlayerManager.getCurrentState()
            if (currentState == PlayState.STATE_PLAY || currentState == PlayState.STATE_RESUME) {
                binding.cutControl.mCutViewLayout.setCurrentTime(it.toLong())
            }
        }
    }

    //设置开始结束剪辑时间
    private fun LayoutVideoCutContainerBinding.showCutTime() {

        cutControl.apply {
            beginTime.text = formatDuration(mVideoEditorHelper.cutterStartTime)
            endTime.text = formatDuration(mVideoEditorHelper.cutterEndTime)
            timeInterval.text = formatDuration(mVideoEditorHelper.geCutterDuration())
        }
    }

    /**
     * 设置视频信息
     * @param videoPath String?
     */
    private fun loadVideoInfo() {
        // 加载视频信息
        val info = mVideoEditorHelper.txVideoInfo
        if (info == null) {
            DialogUtil.showDialog(
                UGCKitImpl.getAppContext(),
                resources.getString(R.string.tc_video_cutter_activity_video_main_handler_edit_failed),
                resources.getString(R.string.ugckit_does_not_support_android_version_below_4_3),
                null
            )
        } else {
            // 初始化缩略图列表，裁剪缩略图
            val interval = videoTimeInterval(info.duration)
            binding.cutControl.mCutViewLayout.apply {
              /*  setTotalDuration(info.duration)
                setAllThumbnailListWidth((info.duration / interval).toInt())
                setThumbnailList(mVideoEditorHelper.allThumbnailList)*/
            }

            binding.cutControl.videoCutLayout.apply {
                setCutChangeListener(this@TWVideoEditCutContainer)
              //  setCount((info.duration / interval).toInt())
                setMediaFileInfo(mVideoEditorHelper.txVideoInfo)
                val list = ArrayList<Bitmap>()
                mVideoEditorHelper.allThumbnailList.forEach {
                    list.add(it.bitmap)
                }
                addBitmapList(list)
            }

        }
    }

    fun getPlayerView() = binding.mVideoPlayerView

    override fun release() {
        super.release()
        TelephonyUtil.getInstance().uninitPhoneListener()
    }



    override fun onVideoProgressSeek(currentTimeMs: Long) {
        PlayerManager.previewAtTime(currentTimeMs)
    }

    override fun onVideoProgressSeekFinish(currentTimeMs: Long) {
        currentTime=currentTimeMs
        PlayerManager.previewAtTime(currentTimeMs)

        LogUtils.i("--onVideoProgressSeekFinish--------------${currentTimeMs}---------------")
    }

    override fun onPreviewProgress(time: Int) {
        currentTime=time.toLong()
        LogUtils.i("--TWVideoEditCutContainer--------------${time}---------------")
    }

    override fun onPreviewFinish() {
        PlayerManager.restartPlay()
    }

    override fun onCutClick() {

    }

    override fun onCutChangeKeyDown() {
        LogUtils.i("--onCutChangeKeyDown------------------")
    }

    override fun onCutChangeKeyUp(startTime: Long, endTime: Long, type: Int) {
        if (type == RangeSlider.TYPE_LEFT) {
            PlayerManager.previewAtTime(startTime)
        } else {
            PlayerManager.previewAtTime(endTime)
        }
        LogUtils.i("--onCutChangeKeyUp--------------${startTime}----${endTime}-----------")
        mVideoEditorHelper.setCutterStartTime(startTime,endTime)
        binding.showCutTime()
    }


}