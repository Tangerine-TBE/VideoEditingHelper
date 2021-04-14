package com.twx.module_videoediting.ui.activity

import android.content.Intent
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.tencent.qcloud.ugckit.module.record.MusicInfo
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityMusicBinding
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.MusicViewModel

class MusicActivity : BaseVmViewActivity<ActivityMusicBinding,MusicViewModel>(){
    override fun getViewModelClass(): Class<MusicViewModel> {
        return MusicViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_music

    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@MusicActivity, musicTitleBar, LayoutType.CONSTRAINTLAYOUT)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                mTWVideoMusicContainer.setVideoInfo(it)
            }
            lifecycle.addObserver(mTWVideoMusicContainer.getPlayerView())
        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@MusicActivity, {
                    musicTitleBar.setThemeChange(it)
                    viewThemeColor(it, musicContainer)
                })

            }
        }
    }

    override fun initEvent() {
        binding.apply {
            mTWVideoMusicContainer.setOnCutListener(outPutVideo(loadingPopup,this@MusicActivity))
            musicTitleBar.setBarEventAction(this@MusicActivity){
                mTWVideoMusicContainer.startExportVideo()
                loadingPopup.showPopupView(musicContainer)
            }

            mTWVideoMusicContainer.setSelectMusic {
                openMediaSelect()
            }

            loadingPopup.cancelMake {
                cancelMake(false)
            }

        }
    }

    private fun openMediaSelect() {
     /*   PictureSelector.create(this)
            .openGallery(PictureConfig.TYPE_AUDIO)
            .imageSpanCount(3)// 每行显示个数 int
            .maxSelectNum(1)
            .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
            .isCamera(false)// 是否显示拍照按钮 true or false
            .isZoomAnim(true)
            .forResult(Constants.REQUEST_VIDEO_CODE);//结果回调onActivityResult code*/

        toOtherResultActivity<SelectMediaActivity>(this,SelectMediaActivity.REQUEST_CODE){}

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 结果回调
        if (requestCode==SelectMediaActivity.REQUEST_CODE) {
            data?.getStringExtra(Constants.KEY_AUDIO_INFO)?.let {
                gsonHelper<MediaInformation>(it)?.let {
                    val musicInfo = MusicInfo().apply {
                        name = it.name
                        path = it.path
                        duration = it.duration
                    }
                    binding.mTWVideoMusicContainer.setMusicInfo(musicInfo)
                    binding.mTWVideoMusicContainer.showMusicEditor()
                }
            }

        }
    }

}