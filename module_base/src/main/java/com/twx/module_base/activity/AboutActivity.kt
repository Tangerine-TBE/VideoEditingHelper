package com.twx.module_base.activity

import com.twx.module_base.R
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.databinding.ActivityAboutBinding
import com.twx.module_base.utils.PackageUtil
import com.twx.module_base.utils.setToolBar
import com.twx.module_base.utils.toolbarEvent

class AboutActivity : BaseViewActivity<ActivityAboutBinding>() {

    override fun getLayoutView(): Int=R.layout.activity_about


    override fun initView() {

        binding.apply {
            setToolBar(this@AboutActivity,"关于我们",aboutToolbar)
            version.text="${PackageUtil.packageCode(this@AboutActivity)}"
        }

    }

    override fun initEvent() {
        binding.aboutToolbar.toolbarEvent(this) {}
    }

}