package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityDivisionBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class DivisionActivity : BaseVmViewActivity<ActivityDivisionBinding,VideoCutViewModel>() {

    override fun getLayoutView(): Int = R.layout.activity_division
    override fun getViewModelClass(): Class<VideoCutViewModel> {
        return VideoCutViewModel::class.java
    }

    override fun initView() {
        binding.apply {
            setStatusBarDistance(
                this@DivisionActivity,
                divisionTitleBar,
                LayoutType.CONSTRAINTLAYOUT
            )
            val videoPath = intent.getStringExtra(Constants.KEY_VIDEO_PATH)
            mTWVideoCutContainer.setVideoPath(videoPath)
            lifecycle.addObserver(mTWVideoCutContainer.getPlayerView())
        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@DivisionActivity, {
                    divisionTitleBar.setThemeChange(it)
                    viewThemeColor(it, divisionContainer)
                })

            }
        }

    }

    override fun initEvent() {
        binding.divisionTitleBar.setBarEventAction(this){

        }
    }

}