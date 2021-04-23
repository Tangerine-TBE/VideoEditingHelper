package com.twx.module_videoediting.ui.widget.video.crop

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.GridLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.showToast
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoCropViewBinding
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.video.crop.CropItemAdapter
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseUi
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.crop
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/23 14:27:07
 * @class describe
 */
class VideoCropContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseUi(context, attrs, defStyleAttr) {
    private val binding= DataBindingUtil.inflate<LayoutVideoCropViewBinding>(LayoutInflater.from(context), R.layout.layout_video_crop_view, this, true)

    private val mCropOneAdapter by lazy {
        CropItemAdapter()
    }


    private val mCropTwoAdapter by lazy {
        CropItemAdapter()
    }


    init {
        initView()
        initEvent()
    }

    private fun initView() {
        binding.apply {
            //初始好播放器
            GSYVideoOptionBuilder()
                .setIsTouchWiget(true)
                .setNeedLockFull(false)
                .setRotateWithSystem(false)
                .setAutoFullWithSize(false)
                .setRotateViewAuto(false)
                .setShowFullAnimation(false)
                .setLooping(true)
                .build(mCropPlayerView)

            cropOneContainer.apply {
                layoutManager=GridLayoutManager(context,4)
                mCropOneAdapter.setList(DataProvider.cropItemList.subList(0,4))
                adapter=mCropOneAdapter
            }

            cropTwoContainer.apply {
                layoutManager=GridLayoutManager(context,5)
                mCropTwoAdapter.setList(DataProvider.cropItemList.subList(3,8))
                adapter=mCropTwoAdapter
            }

        }
    }


    private fun initEvent() {
        binding.apply {
            mCropPlayerView.apply {
                setGSYStateUiListener {
                    LogUtils.i("-setGSYStateUiListener--------------$it------------------")
                    when(it){
                        GSYVideoView.CURRENT_STATE_ERROR -> {
                            errorSelectCore()
                        }
                    }
                }

                mCropOneAdapter.setOnItemClickListener { adapter, view, position ->
                    LogUtils.i("-mCropOneAdapter--------------${getRotationValue()}------------------")

                    when(position){
                        0-> setResolveTransform(true,getResolveTransform())
                        1-> setResolveTransform(false,getResolveTransform())
                        2->setRotation()
                        3->restoreVideoUi()
                    }
                }

            }
        }

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun playerResume(){
        binding.mCropPlayerView.onVideoResume()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun playerPause(){
        binding.mCropPlayerView.onVideoPause()
    }



    fun setVideoPath(path:String){
        binding.mCropPlayerView.apply {
            val videoFileInfo = TXVideoInfoReader.getInstance(context).getVideoFileInfo(path)
            LogUtils.i("-videoFileInfo--------------  ${   videoFileInfo.width} ${videoFileInfo.height}   ------------------")
            setUp(path,true,"")
            startPlayLogic()
        }
    }

    private var errorCount=0
    private fun errorSelectCore(){
            val type = when (sp.getInt(Constants.SP_CORE_TYPE)) {
                0 -> {
                    PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
                    1
                }
                1 -> {
                    PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
                    2
                }
                2 -> {
                    PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                    0
                }
                else->{
                    PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                    0
                }
            }
            sp.putInt(Constants.SP_CORE_TYPE,type)
            errorCount++
            errorCount = if (errorCount > 2) {
                showToast("暂时不支持此视频格式")
                0
            } else {
                binding.mCropPlayerView.startPlayLogic()
                0
            }
    }



    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding.mCropPlayerView.release()
        GSYVideoManager.releaseAllVideos()
    }

}