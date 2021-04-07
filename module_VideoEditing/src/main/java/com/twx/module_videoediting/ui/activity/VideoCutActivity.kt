package com.twx.module_videoediting.ui.activity

import android.content.Intent
import com.tencent.liteav.demo.videoediter.TCVideoEditerActivity
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.toOtherActivity
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityVidelCutBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.widget.video.cut.IVideoCut
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class VideoCutActivity : BaseVmViewActivity<ActivityVidelCutBinding, VideoCutViewModel>() {

    private fun startEditActivity() {
        val intent = Intent(this, TCVideoEditerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val mOnCutListener by lazy {
        object: IVideoCut.OnCutListener{
            override fun onCutterCompleted(ugcKitResult: UGCKitResult?) {
                if (ugcKitResult?.errorCode == 0) {
                    startEditActivity()
                }
            }
            override fun onCutterCanceled() {

            }

            override fun onCutterProgress(progress: Float) {

            }

        }
    }

    override fun getViewModelClass(): Class<VideoCutViewModel> {
        return VideoCutViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_videl_cut

    override fun initView() {
        binding.apply {
            data=viewModel
            setStatusBarDistance(this@VideoCutActivity, cutTitleBar, LayoutType.CONSTRAINTLAYOUT)
            val videoPath = intent.getStringExtra(Constants.KEY_VIDEO_PATH)
            mTWVideoCutContainer.setVideoPath(videoPath)
            lifecycle.addObserver(mTWVideoCutContainer)
        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@VideoCutActivity, {
                    cutTitleBar.setThemeChange(it)
                    viewThemeColor(it, videoCutContainer)
                })

            }
        }
    }


    override fun initEvent() {
        binding.apply {
            cutTitleBar.setBarEventAction(this@VideoCutActivity) {
              //  mTWVideoCutContainer.startExportVideo()
                toOtherActivity<ExportActivity>(this@VideoCutActivity,true){}
            }
        }
    }



/*    override fun release() {
        binding.mTWVideoCutContainer.release()



    }*/
}

