package com.twx.module_videoediting.ui.fragment


import androidx.recyclerview.widget.LinearLayoutManager
import com.twx.module_base.activity.AboutActivity
import com.twx.module_base.activity.DealViewActivity
import com.twx.module_base.base.BaseVmFragment
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.FragmentSetBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.SetAdapter

import com.twx.module_videoediting.viewmodel.MainViewModel

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.fragment
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/23 16:48:54
 * @class describe
 */
class SetFragment : BaseVmFragment<FragmentSetBinding, MainViewModel>() {
    private val mSetAdapter by lazy {
        SetAdapter()
    }
    override fun getChildLayout(): Int = R.layout.fragment_set
    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun initView() {
        binding.apply {
            setContainer.apply {
                layoutManager = LinearLayoutManager(activity)
                mSetAdapter.setList(DataProvider.setList)
                adapter=mSetAdapter
            }

        }
    }

    override fun observerData() {
        binding.apply {
            val that=this@SetFragment
            ThemeChangeLiveData.observe(that,{
                mSetAdapter.setThemeChangeState(it)
                viewThemeColor(it,setTitle,loginTip)
                textViewThemeDrawable(it,loginTip)
            })
        }

    }

    override fun initEvent() {
        binding.apply {
            mSetAdapter.setOnItemClickListener { adapter, view, position ->
                when (position) {
                    0 -> ThemeChangeLiveData.setThemeType()
                    1-> toAppShop(activity)
                    2-> toOtherActivity<AboutActivity>(activity){}
                    3->toOtherActivity<DealViewActivity>(activity){
                        putExtra(Constants.SET_DEAL1,1)
                    }
                    4->toOtherActivity<DealViewActivity>(activity){
                        putExtra(Constants.SET_DEAL1,2)
                    }
                }
            }

        }
    }

}