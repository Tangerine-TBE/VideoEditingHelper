package com.twx.module_videoediting.ui.activity

import android.content.Intent
import com.tencent.qcloud.ugckit.module.record.MusicInfo
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityMusicBinding
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction

class MusicActivity : BaseViewActivity<ActivityMusicBinding>(){
    override fun getLayoutView(): Int=R.layout.activity_music
    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, musicContainer)
            setStatusBarDistance(this@MusicActivity, musicTitleBar, LayoutType.CONSTRAINTLAYOUT)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                mTWVideoMusicContainer.setVideoInfo(it)
            }
            lifecycle.addObserver(mTWVideoMusicContainer.getPlayerView())
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