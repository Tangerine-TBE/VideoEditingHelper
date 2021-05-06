package com.twx.module_videoediting.ui.activity

import android.graphics.Bitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityJoinBinding
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinCutAdapter
import com.twx.module_videoediting.ui.widget.video.join.IVideoJoin
import com.twx.module_videoediting.ui.widget.video.join.JoinEditorManager
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.FileUtils
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.utils.video.PlayerManager

class JoinActivity : BaseViewActivity<ActivityJoinBinding>() {
    override fun getLayoutView(): Int = R.layout.activity_join

    private val mJoinCutAdapter by lazy {
        JoinCutAdapter()
    }

    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, joinContainer)
            setStatusBarDistance(this@JoinActivity, joinTitleBar, LayoutType.CONSTRAINTLAYOUT)
         //   lifecycle.addObserver(mVideoJoinContainer)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.let { it ->
                    if (it.joinList.size>0){
                        LogUtils.i("---JoinActivity---------${it.joinList}------")
                        it.joinList.forEach {
                           // JoinEditorManager.createEditor(it.path)
                        }

                        TXVideoEditer(this@JoinActivity).apply {
                            val videoFileInfo = TXVideoInfoReader.getInstance(this@JoinActivity).getVideoFileInfo(it.joinList[0].path)
                            setCutFromTime(0,videoFileInfo.duration)
                            getThumbnail(4,100,100,false,object :TXVideoEditer.TXThumbnailListener{
                                override fun onThumbnail(p0: Int, p1: Long, p2: Bitmap?) {
                                    LogUtils.i("---getThumbnail--------$p0}------")
                                }

                            })
                            mVideoJoinPlayerControl.initPlayerLayoutTest(this,videoFileInfo.duration)
                        }



                 //      mVideoJoinPlayerControl.initPlayerLayout(JoinEditorManager.getEditorList()[0])

                    }
                }
            }


            cutViewContainer.apply {
                layoutManager=LinearLayoutManager(context)
                adapter=mJoinCutAdapter
            }
        }


    }



    override fun initEvent() {
        binding.apply {
    /*        joinTitleBar.setBarEventAction(this@JoinActivity) {
               mVideoJoinContainer.startGenerateVideo()
                loadingPopup.showPopupView(joinContainer)
            }


            mVideoJoinContainer.setJoinListener(object : IVideoJoin.OnJoinListener {
                override fun onJoinProgress(progress: Float) {
                    LogUtils.i("**----onJoinProgress------------$progress---------------")
                    loadingPopup.setProgress((progress * 100).toInt())
                    MakeBackLiveData.setMakeFinishState(false)
                }

                override fun onJoinComplete(
                    result: TXVideoEditConstants.TXJoinerResult,
                    videoOutputPath: String
                ) {
                    loadingPopup.dismiss()
                    if (result.retCode == TXVideoEditConstants.JOIN_RESULT_OK) {
                        FileUtils.saveAlbum(this@JoinActivity,videoOutputPath)
                        ExportActivity.toExportPage(this@JoinActivity,true,videoOutputPath)
                    } else {
                        showToast("视频合失败！")
                    }
                    MakeBackLiveData.setMakeFinishState(true)
                }

            })

            loadingPopup.cancelMake {
                mVideoJoinContainer.stopGenerateVideo()
                MakeBackLiveData.setMakeFinishState(true)
            }*/

        }

    }

    override fun release() {
        super.release()
        JoinEditorManager.release()
    }


}