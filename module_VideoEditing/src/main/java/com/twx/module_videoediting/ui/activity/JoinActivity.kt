package com.twx.module_videoediting.ui.activity

import com.tencent.ugc.TXVideoEditConstants
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityJoinBinding
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.ui.widget.video.join.IVideoJoin
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.FileUtils
import com.twx.module_videoediting.utils.formatList
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.utils.video.PlayerManager

class JoinActivity : BaseViewActivity<ActivityJoinBinding>() {
    override fun getLayoutView(): Int = R.layout.activity_join

    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, joinContainer)
            setStatusBarDistance(this@JoinActivity, joinTitleBar, LayoutType.CONSTRAINTLAYOUT)
           lifecycle.addObserver(mVideoJoinContainer)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.let { it ->
                    if (it.joinList.size>0){
                        LogUtils.i("---JoinActivity---------${it.joinList}------")
                        val videoList = ArrayList<String>()
                        it.joinList.forEach {
                            videoList.add(it.path)
                        }
                        mVideoJoinContainer.setVideoSourceList(videoList){ finish() }
                    }
                }
            }
        }
    }



    override fun initEvent() {
        binding.apply {
            joinTitleBar.setBarEventAction(this@JoinActivity) {
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
            }

        }

    }

    override fun release() {
        super.release()

    }


}