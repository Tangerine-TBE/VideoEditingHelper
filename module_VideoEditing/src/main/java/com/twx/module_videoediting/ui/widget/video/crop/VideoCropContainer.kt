package com.twx.module_videoediting.ui.widget.video.crop

import android.content.Context
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginRight
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
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoCropViewBinding
import com.twx.module_videoediting.domain.CropConfig
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
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : BaseUi(context, attrs, defStyleAttr) {
    private val binding = DataBindingUtil.inflate<LayoutVideoCropViewBinding>(
            LayoutInflater.from(context),
            R.layout.layout_video_crop_view,
            this,
            true
    )
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
                    .setIsTouchWiget(false)
                    .setNeedLockFull(false)
                    .setRotateWithSystem(false)
                    .setAutoFullWithSize(false)
                    .setRotateViewAuto(false)
                    .setShowFullAnimation(false)
                    .setLooping(true)
                    .build(mCropPlayerView)

            cropOneContainer.apply {
                layoutManager = GridLayoutManager(context, 4)
                mCropOneAdapter.setList(DataProvider.cropItemList.subList(0, 4))
                adapter = mCropOneAdapter
            }

            cropTwoContainer.apply {
                layoutManager = GridLayoutManager(context, 5)
                mCropTwoAdapter.setList(DataProvider.cropItemList.subList(4, 9))
                adapter = mCropTwoAdapter
            }



        }
    }

    private var srcWidth=0
    private var srcHeight=0
    private var mRealHeight=0
    private var mRealWidth=0

    private fun initEvent() {
        binding.apply {
            mCropPlayerView.apply {
                setGSYStateUiListener {

                    when (it) {
                        GSYVideoView.CURRENT_STATE_ERROR -> {
                            errorSelectCore()
                        }
                        GSYVideoView.CURRENT_STATE_PLAYING -> {

                        }
                    }
                }

                mCropOneAdapter.setOnItemClickListener { adapter, view, position ->
                    mCropViewContainer.goneView()
                    mCropTwoAdapter.setModelPosition(false,-1)
                    when (position) {
                        0 -> setResolveTransform(true, getResolveTransform())
                        1 -> setResolveTransform(false, getResolveTransform())
                        2 -> {
                            if (!getRotateState()) {
                                getTextureView {
                                    srcWidth=it.width
                                    srcHeight=it.height
                                }
                            }
                            setRotation()
                        }
                        3 -> restoreVideoUi()
                    }
                }

                mCropTwoAdapter.setOnItemClickListener { adapter, view, position ->
                    restoreVideoUi()
                    getTextureView {
                        mRealHeight=if (getRotateState()) srcHeight else it.height
                        mRealWidth= if (getRotateState()) srcWidth  else it.width
                        LogUtils.i("--mCropTwoAdapter-srcWidth  ${srcWidth}    it.width ${it.width}    ----$mRealWidth--------------$mRealHeight-----------------")
                        val layoutParams = mCropViewContainer.layoutParams
                        layoutParams.height =mRealHeight
                        layoutParams.width =mRealWidth
                        mCropViewContainer.layoutParams = layoutParams
                        mCropViewContainer.showView()
                        setSizeType(position, mRealWidth, mRealHeight)

                    }
                }


                completeCrop.setOnClickListener {
                    if (!TextUtils.isEmpty(mSrcFile)) {
                        val videoFileInfo = TXVideoInfoReader.getInstance(context).getVideoFileInfo(mSrcFile)
                        val widthRate = videoFileInfo.width / mRealWidth.toFloat()
                        val heightRate = videoFileInfo.height / mRealHeight.toFloat()
                        LogUtils.i("-outInfo****----原宽：${ videoFileInfo.width}  原高：${videoFileInfo.height}     现宽：${mRealWidth}   现高：${mRealHeight} -----------------")
                        completeAction(CropConfig(getResolveTransform(), getRotationValue().toInt(), if (mCropViewContainer.isVisible) mCropView.rectValue else RectF(),widthRate,heightRate))
                    }
                }

            }


        }

    }




    private fun LayoutVideoCropViewBinding.setSizeType(position: Int, realWidth: Int, realHeight: Int) {
        var cropWidth: Int
        var cropHeight: Int
        var marginLeft: Int=0
        var marginTop: Int=0
        mCropTwoAdapter.setModelPosition(true,position)
        when (position) {
            //原比例
            0 -> mCropView.setRectValue(true,0, 0, realWidth, realHeight)
            //1:1
            1 -> {
                when {
                    realWidth > realHeight -> {
                        cropWidth = realWidth / 2
                        marginLeft = (realWidth - cropWidth) / 2
                        mCropView.setRectValue(true, marginLeft, 0, cropWidth + marginLeft, realHeight)
                    }
                    realWidth < realHeight -> {
                        cropHeight = realHeight / 2
                        marginTop = (realHeight - cropHeight) / 2
                        mCropView.setRectValue(true, 0, marginTop, realWidth, cropHeight + marginTop)
                    }
                    else -> {
                        mCropView.setRectValue(true, 0, 0, realWidth, realHeight)
                    }
                }

            }
            //16:9
            2 -> {
                    cropHeight=realWidth/16*9
                    marginTop= (realHeight - cropHeight)/2
                    if (cropHeight+marginTop>realHeight){
                        cropHeight=realHeight
                        marginTop=0
                    }

                    mCropView.setRectValue(true, 0, marginTop, realWidth, cropHeight+marginTop)
            }
            //9:16
            3 -> {
                    cropWidth=realHeight/16*9
                    marginLeft=(realWidth-cropWidth)/2
                    mCropView.setRectValue(true, marginLeft, 0, cropWidth + marginLeft, realHeight)
            }
            //4:3
            4 -> {
                when {
                    realWidth > realHeight -> {
                        var cropWidth = realHeight / 3 * 4
                        val marginLeft= (realWidth - cropWidth)/2
                        mCropView.setRectValue(true, marginLeft, 0, cropWidth+marginLeft, realHeight)
                    }
                    realWidth<realHeight -> {
                        var cropHeight= realWidth/ 3 * 4
                        val marginTop= (realHeight - cropHeight)/2
                        mCropView.setRectValue(true, 0, marginTop, realWidth, cropHeight+marginTop)
                    }
                    else -> {
                        var cropHeight= realWidth/ 4 * 3
                        val marginTop= (realHeight - cropHeight)/2
                        mCropView.setRectValue(true, 0, marginTop, realWidth, cropHeight+marginTop)
                    }
                }
            }
        }
    }




    private var completeAction: (CropConfig) -> Unit = {}
    fun setCompleteCropAction(action: (CropConfig) -> Unit) {
        completeAction = action
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun playerPause() {
        binding.mCropPlayerView.onVideoPause()
    }

/*
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun playerResume() {
        binding.mCropPlayerView.onVideoResume()
    }
*/




    private var mSrcFile=""
    fun setVideoPath(path: String) {
        binding.mCropPlayerView.apply {
            mSrcFile=path
            setUp(path, true, "")
            startPlayLogic()
        }
    }


    private var errorCount = 0
    private fun errorSelectCore() {
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
            else -> {
                PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                0
            }
        }
        sp.putInt(Constants.SP_CORE_TYPE, type)
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