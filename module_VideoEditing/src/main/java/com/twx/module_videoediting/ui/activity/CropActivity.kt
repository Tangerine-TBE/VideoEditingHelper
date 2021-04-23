package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityCropBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.CropViewModel

class CropActivity : BaseVmViewActivity<ActivityCropBinding,CropViewModel>() {


    override fun getViewModelClass(): Class<CropViewModel> = CropViewModel::class.java
    override fun getLayoutView(): Int = R.layout.activity_crop

    override fun initView() {

        binding.apply {
            viewThemeColor(themeState,cropContainer)
            setStatusBarDistance(this@CropActivity, cropTitleBar, LayoutType.CONSTRAINTLAYOUT)
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
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


    override fun initEvent() {
        binding.apply {
            cropTitleBar.setBarEventAction(this@CropActivity){

            }
        }

    }
}