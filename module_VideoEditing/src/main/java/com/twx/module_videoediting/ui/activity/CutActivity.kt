package com.twx.module_videoediting.ui.activity

import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityVidelCutBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class CutActivity : BaseVmViewActivity<ActivityVidelCutBinding, VideoCutViewModel>() {




    override fun getViewModelClass(): Class<VideoCutViewModel> {
        return VideoCutViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_videl_cut

    override fun initView() {
        binding.apply {
            data=viewModel
            setStatusBarDistance(this@CutActivity, cutTitleBar, LayoutType.CONSTRAINTLAYOUT)
            mTWVideoCutContainer.initPlayerLayout()
            lifecycle.addObserver(mTWVideoCutContainer.getPlayerView())
        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@CutActivity, {
                    cutTitleBar.setThemeChange(it)
                    viewThemeColor(it, videoCutContainer)
                })

            }
        }
    }


    override fun initEvent() {
        binding.apply {
            mTWVideoCutContainer.setOnCutListener(outPutVideo(loadingPopup,this@CutActivity))
            cutTitleBar.setBarEventAction(this@CutActivity) {
                mTWVideoCutContainer.startExportVideo()
                loadingPopup.showPopupView(videoCutContainer)
            }

            loadingPopup.cancelMake {
                cancelMake(false)
            }

        }
    }





}

