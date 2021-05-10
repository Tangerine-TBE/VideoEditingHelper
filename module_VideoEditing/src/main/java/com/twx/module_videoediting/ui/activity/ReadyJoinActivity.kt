package com.twx.module_videoediting.ui.activity

import android.graphics.Bitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReadyJoinBinding
import com.twx.module_videoediting.domain.ReadyJoinInfo
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinCutAdapter
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ReadyJoinViewModel
import com.yanzhenjie.recyclerview.*
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ReadyJoinActivity : BaseVmViewActivity<ActivityReadyJoinBinding,ReadyJoinViewModel>(), TXVideoEditer.TXVideoGenerateListener {
    private var count=0
    private val mJoinCutAdapter by lazy {
        JoinCutAdapter()
    }

    private val joinVideoList = ArrayList<ReadyJoinInfo>()
    private val pathList = ArrayList<String>()
    override fun getLayoutView(): Int = R.layout.activity_ready_join
    override fun getViewModelClass(): Class<ReadyJoinViewModel>  = ReadyJoinViewModel::class.java
    override fun initView() {
        binding.apply {
            data=viewModel
            viewThemeColor(themeState, readyJoinContainer)
            setStatusBarDistance(this@ReadyJoinActivity, readyJoinTitleBar, LayoutType.CONSTRAINTLAYOUT)
            cutViewContainer.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mJoinCutAdapter
                isLongPressDragEnabled=true
                isItemViewSwipeEnabled=false

            }

            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.joinList?.let { it ->
                    if (it.size > 0) {
                        it.forEach {
                            pathList.add(it.path)
                        }
                        initPlayer(it[0].path)
                        mLoadingDialog.show()
                        getThumbnail()
                    }
                }
            }
        }
    }

    private val mEditerList = ArrayList<TXVideoEditer>()

    private fun ActivityReadyJoinBinding.initPlayer(path: String) {
        //初始好播放器
        GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setNeedLockFull(false)
                .setRotateWithSystem(false)
                .setAutoFullWithSize(false)
                .setRotateViewAuto(false)
                .setShowFullAnimation(false)
                .setLooping(true)
                .build(mJoinPlayerView)
           setVideoPath(path)
    }


    private fun  getThumbnail(){
        mScope.launch {
            if (pathList.size > count) {
                val bitmapList = ArrayList<Bitmap>()
                val videoPath=pathList[count]
                TXVideoEditer(this@ReadyJoinActivity).apply {
                    setVideoPath(videoPath)
                }.getThumbnail(6, 100, 100, true) { index, times, bitmap ->
                    bitmapList.add(bitmap)
                    if (index >= 5) {
                        joinVideoList.add(ReadyJoinInfo(bitmapList,videoPath,0L,getTotalTime(videoPath)))
                        count++
                        getThumbnail()
                    }
                        LogUtils.i("---getThumbnail----00----------$index---$times--$count-")
                    }
            } else {
               withContext(Dispatchers.Main){
                   mJoinCutAdapter.setReadyJoinList(joinVideoList)
                    viewModel.setReadyList(joinVideoList)
                   mLoadingDialog.dismiss()
                    LogUtils.i("---getThumbnail---end    end     end-")
                }
            }
        }
    }


    private fun getTotalTime(path: String)= TXVideoInfoReader.getInstance(this).getVideoFileInfo(path).duration

    override fun initEvent() {
        binding.apply {
            readyJoinTitleBar.setBarEventAction(this@ReadyJoinActivity) {
                mJoinCutAdapter.getReadyJoinList()?.let {
                    if (it.size >= 2) {
                        loadingPopup.showPopupView(mJoinPlayerView)
                        for (index in 0 until it.size){
                            mEditerList.add(TXVideoEditer(this@ReadyJoinActivity))
                            startGenerateVideo(it, index)
                        }
                    } else {
                        showToast("必须选择两个以上的视频文件")
                    }

                }

            }

            cutViewContainer.setOnItemMoveListener(object : OnItemMoveListener {
                override fun onItemMove(
                        srcHolder: RecyclerView.ViewHolder,
                        targetHolder: RecyclerView.ViewHolder
                ): Boolean {
                    // 不同的ViewType不能拖拽换位置。
                    if (srcHolder.itemViewType != targetHolder.itemViewType) return false

                    val fromPosition = srcHolder.adapterPosition
                    val toPosition = targetHolder.adapterPosition

                    Collections.swap(mJoinCutAdapter.getReadyJoinList(), fromPosition, toPosition)
                    mJoinCutAdapter.notifyItemMoved(fromPosition, toPosition)
                    return true // 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
                }

                override fun onItemDismiss(srcHolder: RecyclerView.ViewHolder) {
                    val position = srcHolder.adapterPosition
                    mJoinCutAdapter.getReadyJoinList().removeAt(position)
                    mJoinCutAdapter.notifyItemRemoved(position)
                }

            })



            mJoinCutAdapter.setOnListener(object : JoinCutAdapter.OnListener {
                override fun selectClick(data: ReadyJoinInfo, position: Int, type: Int) {
                    binding.mJoinPlayerView.seekOnStart = data.cutStartTime
                    setVideoPath(data.videoPath)
                    LogUtils.i("---selectClick---${data.videoPath}--------${data.cutStartTime}---${data.cutEndTime}---------")
                }

                override fun click(data: ReadyJoinInfo) {
                    setVideoPath(data.videoPath)
                    LogUtils.i("---click----${data.videoPath}-------${data.cutStartTime}---${data.cutEndTime}---------")
                }

                override fun deleteItem(list: MutableList<ReadyJoinInfo>) {
                    viewModel.setReadyList(list)
                }
            })

            addVideo.setOnClickListener {
                MainViewActivity.toAddJoinActivity(this@ReadyJoinActivity,Gson().toJson(mJoinCutAdapter.getReadyJoinList()))
            }

            loadingPopup.cancelMake {
                stopGenerateVideo()
            }

        }
    }

    private fun startGenerateVideo(it: ArrayList<ReadyJoinInfo>, index: Int) {

        mEditerList[index].apply {
            val readyJoinInfo = it[index]
            setVideoPath(readyJoinInfo.videoPath)
            setCutFromTime(readyJoinInfo.cutStartTime, readyJoinInfo.cutEndTime)
            setVideoGenerateListener(this@ReadyJoinActivity)
            generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, VideoPathUtil.generateJoinVideoPath(index))
        }
    }

    private fun stopGenerateVideo(){
        mEditerList.forEach {
            it.cancel()
            it.setVideoGenerateListener(null)
        }
        MakeBackLiveData.setMakeFinishState(true)
    }


    private fun setVideoPath(path: String){
        binding.mJoinPlayerView.apply {
            setUp(path, true, "")
            startPlayLogic()
        }
    }


    override fun onResume() {
        super.onResume()
        binding.mJoinPlayerView.onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mJoinPlayerView.onVideoPause()
    }



    override fun release() {
        super.release()
        mLoadingDialog.dismiss()
        binding.mJoinPlayerView.release()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onGenerateProgress(progress: Float) {

        loadingPopup.setProgress((progress*100).toInt())
        MakeBackLiveData.setMakeFinishState(false)
        LogUtils.i("-onGenerateProgress------${Thread.currentThread()}---$progress---------------")
    }

    override fun onGenerateComplete(result: TXVideoEditConstants.TXGenerateResult) {

        if (result?.retCode == 0) {

        }
        MakeBackLiveData.setMakeFinishState(true)
        loadingPopup.dismiss()

    }


}