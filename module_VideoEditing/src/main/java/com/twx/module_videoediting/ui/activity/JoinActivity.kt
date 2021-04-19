package com.twx.module_videoediting.ui.activity

import com.tencent.qcloud.ugckit.utils.AlbumSaver
import com.tencent.qcloud.ugckit.utils.CoverUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityJoinBinding
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.widget.video.join.IVideoJoin
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.FileUtils
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.JoinViewModel

class JoinActivity : BaseVmViewActivity<ActivityJoinBinding, JoinViewModel>() {

    override fun getViewModelClass(): Class<JoinViewModel> {
        return JoinViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_join

    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@JoinActivity, joinTitleBar, LayoutType.CONSTRAINTLAYOUT)
            lifecycle.addObserver(mVideoJoinContainer)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.let { it ->
                    if (it.joinList.size>0){
                        LogUtils.i("---JoinActivity---------${it.joinList}------")
                        val  videoList = ArrayList<String>()
                        it.joinList.forEach {
                            videoList.add(it.path)
                        }
                        mVideoJoinContainer.setVideoSourceList(videoList){finish()}
                    }
                }
            }
        }

    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@JoinActivity, {
                    joinTitleBar.setThemeChange(it)
                    viewThemeColor(it, joinContainer)
                })

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


}