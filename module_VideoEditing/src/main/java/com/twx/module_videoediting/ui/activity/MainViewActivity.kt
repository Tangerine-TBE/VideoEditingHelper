package com.twx.module_videoediting.ui.activity

import android.content.Intent
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityHomeBinding
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.domain.ReadyJoinInfo
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.NavigationAdapter
import com.twx.module_videoediting.ui.fragment.FileFragment
import com.twx.module_videoediting.ui.fragment.HomeFragment
import com.twx.module_videoediting.ui.fragment.SetFragment
import com.twx.module_videoediting.ui.popup.ExitPoPupWindow
import com.twx.module_videoediting.utils.Constants

import com.twx.module_videoediting.viewmodel.MainViewModel

class MainViewActivity : BaseVmViewActivity<ActivityHomeBinding, MainViewModel>() {

    companion object{
        fun toFileFragment(activity: FragmentActivity?){
               toOtherActivity<MainViewActivity>(activity,true){
                                    putExtra(Constants.KEY_FRAGMENT_ID,1)
                                }
        }

        fun toAddJoinActivity(activity: FragmentActivity?){
            toOtherActivity<MainViewActivity>(activity,true){
                putExtra(Constants.KEY_FRAGMENT_ID,2)
            }
        }



  }

    private val mExitPoPupWindow by lazy {
        ExitPoPupWindow(this)
    }
    private val mHomeFragment by lazy {  HomeFragment() }
    private val mFileFragment by lazy {  FileFragment() }
    private val mSetFragment by lazy {  SetFragment() }
    private val mNavigationAdapter by lazy {
        NavigationAdapter()
    }
    private val mVideoList by lazy {
        ArrayList<MediaInformation>()
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun getLayoutView(): Int =R.layout.activity_home


    override fun initView() {
        binding.apply {
            sp. putBoolean(com.twx.module_base.utils.Constants.IS_FIRST, false)
            setStatusBarDistance(this@MainViewActivity,pageContainer,LayoutType.CONSTRAINTLAYOUT)
            mBottomNavigationView.apply {
                layoutManager=GridLayoutManager(this@MainViewActivity,3)
                mNavigationAdapter.setList(DataProvider.navigationList)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        super.onKeyDown(keyCode, event)
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (viewModel.getEditAction_()) {
                viewModel.setEditAction(false)
                return false
            }
            if (MakeBackLiveData.getMakeState()) {
                mExitPoPupWindow.showPopupView(binding.mainContainer)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }




    override fun onResume() {
        super.onResume()
        intent.apply {
            val id = getIntExtra(Constants.KEY_FRAGMENT_ID, -1)
            LogUtils.i("----onResume-----------   $id  ---------------")
            when(id) {
                1->{
                    mNavigationAdapter.setSelectPosition(1)
                    showFragment(mFileFragment)
                }
                2 -> {
                    val videoStr = sp.getString(Constants.SP_VIDEO_LIST)
                    Gson().fromJson<List<ReadyJoinInfo>>(videoStr, object : TypeToken<List<ReadyJoinInfo>>() {}.type)?.let { it ->
                        mVideoList.clear()
                        if (it.isNotEmpty()) {
                            it.forEach {
                                mVideoList.add(MediaInformation(path = it.videoPath))
                            }
                            openMediaSelect(maxSelectNum = 5 - it.size)
                        } else {
                            openMediaSelect(minSelectNum = 2, maxSelectNum = 5 - it.size)
                        }
                    }

                }
            }
            putExtra(Constants.KEY_FRAGMENT_ID, -1)
        }
    }




    private fun openMediaSelect(
            action: Int=0,
            maxSelectNum: Int = 1,
            minSelectNum: Int = 1,
            selectionMode: Int = PictureConfig.MULTIPLE,
    ) {
        PictureSelector.create(this)
                .openGallery(PictureConfig.TYPE_VIDEO)
                .imageSpanCount(3)// 每行显示个数 int
                .maxSelectNum(maxSelectNum)
                .minSelectNum(minSelectNum)
                .previewVideo(false)
                .previewImage(false)
                .videoMaxSecond(600)
                .selectionMode(selectionMode)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(false)
                .forResult(action);//结果回调onActivityResult code
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==0) {
            PictureSelector.obtainMultipleResult(data)?.let { it ->
               if (it.size>0){
                   it.forEach {
                       mVideoList.add(MediaInformation(path = it .path))
                   }
                   toOtherActivity<ReadyJoinActivity>(this) {
                       putExtra(
                               Constants.KEY_VIDEO_PATH,
                               Gson().toJson(ValueJoinList(mVideoList))
                       )
                   }
               }
            }
        }
    }


    override fun release() {
        mExitPoPupWindow.dismiss()
    }
}