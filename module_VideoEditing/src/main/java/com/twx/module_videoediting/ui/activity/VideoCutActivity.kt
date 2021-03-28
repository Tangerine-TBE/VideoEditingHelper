package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityVidelCutBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.VideoCutViewModel

class VideoCutActivity : BaseVmViewActivity<ActivityVidelCutBinding,VideoCutViewModel>() {

    override fun getViewModelClass(): Class<VideoCutViewModel> {
        return VideoCutViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_videl_cut

    override fun initView() {
        binding.apply {
            data=viewModel
            setStatusBarDistance(this@VideoCutActivity,cutTitleBar, LayoutType.CONSTRAINTLAYOUT)

        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@VideoCutActivity,{
                    cutTitleBar.setThemeChange(it)
                    viewThemeColor(it,videoCutContainer)
                })


            }
        }
    }


    override fun initEvent() {
        binding.apply {
            cutTitleBar.setBarEventAction(this@VideoCutActivity) {}
        }
    }

}