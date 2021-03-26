package com.twx.module_videoediting.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityHomeBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.NavigationAdapter
import com.twx.module_videoediting.ui.fragment.FileFragment
import com.twx.module_videoediting.ui.fragment.HomeFragment
import com.twx.module_videoediting.ui.fragment.SetFragment
import com.twx.module_videoediting.viewmodel.MainViewModel

class MainViewActivity : BaseVmViewActivity<ActivityHomeBinding, MainViewModel>() {

    private val mHomeFragment by lazy {  HomeFragment() }
    private val mFileFragment by lazy {  FileFragment() }
    private val mSetFragment by lazy {  SetFragment() }
    private val mNavigationAdapter by lazy {
        NavigationAdapter()
    }



    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun getLayoutView(): Int =R.layout.activity_home


    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@MainViewActivity,pageContainer,LayoutType.CONSTRAINTLAYOUT)
            mBottomNavigationView.apply {
                layoutManager=GridLayoutManager(this@MainViewActivity,3)
                mNavigationAdapter.setList(DataProvider.navigationLis)
                adapter=mNavigationAdapter
            }
            showFragment(mHomeFragment)
        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                val that=this@MainViewActivity
                ThemeChangeLiveData.observe(that,{
                    mainContainer.setHomeThemeBgColor(it)
                    mBottomNavigationView.setHomeNavigationBgColor(it)
                    mNavigationAdapter.setThemeChangeState(it)
                })
            }

        }
    }

    override fun initEvent() {
        binding.apply {
            mNavigationAdapter.setOnItemClickListener { adapter, view, position ->
                mNavigationAdapter.setSelectPosition(position)
                when(position){
                    0->showFragment(mHomeFragment)
                    1->showFragment(mFileFragment)
                    2->showFragment(mSetFragment)
                }
            }

        }
    }


    private var oldFragment: Fragment?=null
    private fun showFragment(fragment: Fragment){
        if (oldFragment === fragment) {
            return
        }
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment) else add(R.id.pageContainer,fragment)

            oldFragment?.let {
                hide(it)
            }

            oldFragment=fragment
            commitAllowingStateLoss()
        }
    }


}