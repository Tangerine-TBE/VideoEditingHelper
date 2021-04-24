package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityCropBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.*
import com.twx.module_videoediting.viewmodel.CropViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class CropActivity : BaseVmViewActivity<ActivityCropBinding,CropViewModel>() {

    private var mSrcFile = ""
    private var mVideoOutputPath = ""
    override fun getViewModelClass(): Class<CropViewModel> = CropViewModel::class.java
    override fun getLayoutView(): Int = R.layout.activity_crop
    override fun initView() {

        binding.apply {
            viewThemeColor(themeState,cropContainer)
            setStatusBarDistance(this@CropActivity, cropTitleBar, LayoutType.CONSTRAINTLAYOUT)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                mSrcFile=it
                mVideoCropContainer.setVideoPath(it)
            }
            lifecycle.addObserver(mVideoCropContainer)
        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {

            }
        }

    }


    private val callback by lazy {
        ffCallback(onComplete = {
            FileUtils.saveAlbum(this, mVideoOutputPath)
            ExportActivity.toExportPage(this, true, mVideoOutputPath)
        }, onProgress = {
            loadingPopup.setProgress(it)
        }, onCancel = {
            if (!TextUtils.isEmpty(mVideoOutputPath)) {
                FileUtils.deleteFile(File(mVideoOutputPath))
            }
        })
    }

    override fun initEvent() {
        binding.apply {
            cropTitleBar.setBarEventAction(this@CropActivity){

            }


            mVideoCropContainer.setCompleteCropAction {
                mScope.launch(Dispatchers.IO) {
                    mVideoOutputPath = VideoPathUtil.generateVideoPath()
                    FFmpegHelper.startCommand(FFmpegHelper.orientationVideo(mSrcFile, mVideoOutputPath), callback)
                }
                mVideoCropContainer.playerPause()
                loadingPopup.showPopupView(cropContainer)

            }

            loadingPopup.cancelMake {
                FFmpegHelper.exitCommand()
            }

        }
    }


    override fun release() {
        super.release()
        mJob.cancel()
        FFmpegHelper.exitCommand()
    }
}