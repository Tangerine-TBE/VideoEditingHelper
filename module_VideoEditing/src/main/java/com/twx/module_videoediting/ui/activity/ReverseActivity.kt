package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReverseBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ReverseViewModel

class ReverseActivity : BaseVmViewActivity<ActivityReverseBinding, ReverseViewModel>() {

    override fun getViewModelClass(): Class<ReverseViewModel> {
        return ReverseViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_reverse


    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@ReverseActivity, reverseTitleBar, LayoutType.CONSTRAINTLAYOUT)
            lifecycle.addObserver(mTWVideoReverseContainer)
        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@ReverseActivity, {
                    reverseTitleBar.setThemeChange(it)
                    viewThemeColor(it, reverseContainer)
                })

            }
        }

    }

    override fun initEvent() {
        binding.apply {
            reverseTitleBar.setBarEventAction(this@ReverseActivity){}
        }

    }

}