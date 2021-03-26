package com.twx.module_videoediting.ui.fragment

import com.twx.module_base.base.BaseVmFragment
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.FragmentFileBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.viewmodel.MainViewModel

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.fragment
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/23 16:48:54
 * @class describe
 */
class FileFragment:BaseVmFragment<FragmentFileBinding,MainViewModel>(){
    override fun getChildLayout(): Int= R.layout.fragment_file



    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                val that = this@FileFragment
             ThemeChangeLiveData.observe(that,{
                 viewThemeColor(it,fileTitle)
             })


            }
        }
    }
}