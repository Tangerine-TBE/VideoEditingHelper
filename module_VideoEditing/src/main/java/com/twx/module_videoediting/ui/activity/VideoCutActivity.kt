package com.twx.module_videoediting.ui.activity

import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.module.cut.IVideoCutKit
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityVidelCutBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class VideoCutActivity : BaseVmViewActivity<ActivityVidelCutBinding, VideoCutViewModel>() {

    private val mOnCutListener by lazy {
        object: IVideoCutKit.OnCutListener{
            override fun onCutterCompleted(ugcKitResult: UGCKitResult?) {

            }

            override fun onCutterCanceled() {

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
            cutTitleBar.setBarEventAction(this@VideoCutActivity) {}
        }
    }


    override fun onResume() {
        super.onResume()
        binding.apply {
            mTWVideoCutContainer.setOnCutListener(mOnCutListener)
            mTWVideoCutContainer.startPlay()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            mTWVideoCutContainer.stopPlay()
            //FIXBUG:生成进度条使用的同一个，不要在onStop中清空Listener，【onStop在下个界面onStart之后调用，会被清空】
            mTWVideoCutContainer.setOnCutListener(null)
        }
    }

    override fun release() {
        binding.mTWVideoCutContainer.release()
    }
}

