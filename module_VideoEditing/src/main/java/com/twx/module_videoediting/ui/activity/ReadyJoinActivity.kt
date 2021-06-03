package com.twx.module_videoediting.ui.activity

import android.graphics.Bitmap
import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import com.tencent.qcloud.ugckit.utils.DialogUtil
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.tencent.ugc.TXVideoJoiner
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReadyJoinBinding
import com.twx.module_videoediting.domain.ReadyJoinInfo
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinCutAdapter
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.FileUtils
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ReadyJoinViewModel
import com.yanzhenjie.recyclerview.*
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ReadyJoinActivity : BaseVmViewActivity<ActivityReadyJoinBinding,ReadyJoinViewModel>(), TXVideoEditer.TXVideoGenerateListener, TXVideoJoiner.TXVideoJoinerListener {
    private var count=0
    private var cutCount=0
    private var mJoinVideoPath=""
    private val mEditerList = ArrayList<TXVideoEditer>()
    private val mJoinCutAdapter by lazy { JoinCutAdapter() }
    private val joinVideoList = ArrayList<ReadyJoinInfo>()
    private val pathList = ArrayList<String>()
    private val outputVideoPath = ArrayList<String>()
    private val mJoinHelper by lazy {
        TXVideoJoiner(this)
    }
    private val mExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build()
    }

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
            }

            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.joinList?.let { it ->
                    if (it.size > 0) {
                        it.forEach {
                            pathList.add(it.path)
                        }
                        playVideo(it[0].path)
                        mLoadingDialog.show()
                        getThumbnail()


                    }
                }
            }

        }
    }

    /**
     * 播放视频
     * @param path String
     */
    private fun playVideo(path: String){
        binding.mJoinPlayerView.player = mExoPlayer.apply {
            repeatMode= Player.REPEAT_MODE_ALL
            setMediaItem(MediaItem.fromUri(Uri.parse(path)))
            prepare()
            play()
        }
    }



    /**
     * 获取缩略图
     */
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




    override fun initEvent() {
        binding.apply {
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
                    playVideo(data.videoPath)
                    mExoPlayer.seekTo(data.cutStartTime)
                    LogUtils.i("---selectClick---${data.videoPath}--------${data.cutStartTime}---${data.cutEndTime}---------")
                }

                override fun click(data: ReadyJoinInfo) {
                    playVideo(data.videoPath)
                    LogUtils.i("---click----${data.videoPath}-------${data.cutStartTime}---${data.cutEndTime}---------")
                }

                override fun deleteItem(list: MutableList<ReadyJoinInfo>) {
                    viewModel.setReadyList(list)
                }
            })

            addVideo.setOnClickListener {
                MainViewActivity.toAddJoinActivity(this@ReadyJoinActivity)
                sp.putString(Constants.SP_VIDEO_LIST,Gson().toJson(mJoinCutAdapter.getReadyJoinList()))
            }


            loadingPopup.cancelMake {
                stopGenerateVideo()
            }

            readyJoinTitleBar.setBarEventAction(this@ReadyJoinActivity) {
                mExoPlayer.pause()
                mJoinCutAdapter.getReadyJoinList()?.let {
                    if (it.size >= 2) {
                        intiGenerateParams()
                        loadingPopup.showPopupView(mJoinPlayerView)
                        for (index in 0 until it.size){
                            mEditerList.add(TXVideoEditer(this@ReadyJoinActivity))
                        }
                        startGenerateVideo()

                    } else {
                        showToast("必须选择两个以上的视频文件")
                    }

                }

            }
        }
    }


    /**
     * 生成视频初始化
     */
    private fun intiGenerateParams(){
        viewModel.deleteDirectory()
        cutCount=0
        mEditerList.clear()
    }


    /**
     * 开始裁剪
     */
    private fun startGenerateVideo() {
        mEditerList.let { it ->
            if (it.size>cutCount){
                loadingPopup.setTitle("正在剪辑第${cutCount+1}个视频...")
               it[cutCount]?.apply {
                   mJoinCutAdapter.getReadyJoinList()?.let {
                       val readyJoinInfo = it[cutCount]
                       setVideoPath(readyJoinInfo.videoPath)
                       setCutFromTime(readyJoinInfo.cutStartTime, readyJoinInfo.cutEndTime)
                       setVideoGenerateListener(this@ReadyJoinActivity)
                       val generateJoinVideoPath = VideoPathUtil.generateJoinVideoPath(cutCount)
                       generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, generateJoinVideoPath)
                       outputVideoPath.add(generateJoinVideoPath)
                   }

               }
           }
        }
    }

    /**
     * 停止裁剪
     */
    private fun stopGenerateVideo(){
        mEditerList.forEach {
            it.cancel()
            it.setVideoGenerateListener(null)
        }
        MakeBackLiveData.setMakeFinishState(true)
    }

    private fun getTotalTime(path: String)= TXVideoInfoReader.getInstance(this).getVideoFileInfo(path).duration



    override fun onResume() {
        super.onResume()
        mExoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
       mExoPlayer.pause()
    }



    override fun release() {
        super.release()
        stopGenerateVideo()
        mLoadingDialog.dismiss()
        mExoPlayer.release()
    }

    /**
     * 裁剪进度
     * @param progress Float
     */
    override fun onGenerateProgress(progress: Float) {
        loadingPopup.setProgress((progress*100).toInt())
        MakeBackLiveData.setMakeFinishState(false)
        LogUtils.i("-onGenerateProgress------${Thread.currentThread()}---$progress---------------")
    }

    /**
     * 裁剪结果
     * @param result TXGenerateResult
     */
    override fun onGenerateComplete(result: TXVideoEditConstants.TXGenerateResult) {
        val retCode = result?.retCode
        LogUtils.i("-onGenerateComplete--$cutCount----${Thread.currentThread()}---${result.retCode}---------------")
        if (retCode == 0) {
            cutCount++
            startGenerateVideo()
            if (cutCount <= mJoinCutAdapter.getReadyJoinList().size - 1) {
                loadingPopup.setProgress(0)
            } else {

                generateJoinVideo()
            }
        }else{
            MakeBackLiveData.setMakeFinishState(true)
            loadingPopup.dismiss()
            showToast("裁剪失败${retCode}")
        }
    }


    /**
     * 合成视频
     */
    private fun generateJoinVideo() {
        when(mJoinHelper.setVideoPathList(outputVideoPath)){
            TXVideoEditConstants.ERR_UNSUPPORT_VIDEO_FORMAT->{
                DialogUtil.showDialog(this, "视频合成失败", "本机型暂不支持此视频格式"){
                    restoreBack()
                }
            }
            TXVideoEditConstants.ERR_UNSUPPORT_AUDIO_FORMAT->{
                DialogUtil.showDialog(this, "视频合成失败", "暂不支持非单双声道的视频格式"){
                    restoreBack()
                }
            }
            else->{
                mJoinHelper.let {
                    loadingPopup.setProgress(0)
                    loadingPopup.setTitle("正在合成视频...")
                    it.setVideoJoinerListener(this@ReadyJoinActivity)
                    mJoinVideoPath= VideoPathUtil.generateVideoPath()
                    it.joinVideo(
                            TXVideoEditConstants.VIDEO_COMPRESSED_720P,
                            mJoinVideoPath
                    )
                }
            }
        }
    }

    /**
     * 合成进度
     * @param progress Float
     */
    override fun onJoinProgress(progress: Float) {
        LogUtils.i("-----onJoinProgress-----------$progress-------------")
        loadingPopup.setProgress((progress*100).toInt())
        MakeBackLiveData.setMakeFinishState(false)
    }

    /**
     * 合成结果
     * @param result TXJoinerResult
     */
    override fun onJoinComplete(result: TXVideoEditConstants.TXJoinerResult?) {
        if (result?.retCode == TXVideoEditConstants.JOIN_RESULT_OK) {
            FileUtils.saveAlbum(this,mJoinVideoPath)
            ExportActivity.toExportPage(this,true,mJoinVideoPath)
        } else {
            showToast("视频合失败！")
        }
        viewModel.deleteDirectory()
        restoreBack()
    }

    private fun restoreBack(){
        MakeBackLiveData.setMakeFinishState(true)
        loadingPopup.dismiss()
    }


}