package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityExportBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ExportViewModel

class ExportActivity : BaseVmViewActivity<ActivityExportBinding,ExportViewModel>() {


    override fun getViewModelClass(): Class<ExportViewModel> {
        return ExportViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_export


    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@ExportActivity, exportTitleBar, LayoutType.CONSTRAINTLAYOUT)

        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@ExportActivity, {
                    exportTitleBar.setThemeChange(it)
                    viewThemeColor(it, videoExportContainer)
                })

            }
        }

    }

    override fun initEvent() {
        binding.apply {
            exportTitleBar.setBarEventAction(this@ExportActivity) {

            }
        }
    }
}