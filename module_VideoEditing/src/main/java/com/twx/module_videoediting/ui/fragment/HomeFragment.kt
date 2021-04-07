package com.twx.module_videoediting.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.tencent.liteav.demo.videoediter.TCVideoPickerActivity
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.basic.UGCKitResult
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_base.base.BaseVmFragment
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.toOtherActivity
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.FragmentHomeBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.activity.ReverseActivity
import com.twx.module_videoediting.ui.activity.VideoCutActivity
import com.twx.module_videoediting.ui.adapter.recycleview.HomeBottomAdapter
import com.twx.module_videoediting.ui.popup.LoadingPopup
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.viewmodel.MainViewModel

/**
 * @name VideoEditingHelper
 * @class name：com.example.module_videoediting.ui.fragment
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/23 16:48:54
 * @class describe
 */
class HomeFragment : BaseVmFragment<FragmentHomeBinding, MainViewModel>(), OnUpdateUIListener {
    private val mHomeBottomAdapter by lazy {
        HomeBottomAdapter()
    }
    private val mProcessHelper by lazy {
        ProcessKit.getInstance()
    }
    private val mVideoEditorHelper by lazy {
        VideoEditerSDK.getInstance()
    }

    private val mLoadingPopup by lazy {
        LoadingPopup(activity).apply { setTitle("视频预加载中...") }
    }

    override fun getChildLayout(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }



    override fun initView() {
        binding.apply {
            bottomContainer.apply {
                layoutManager = LinearLayoutManager(activity)
                mHomeBottomAdapter.setList(DataProvider.bottomLis)
                adapter = mHomeBottomAdapter
            }
        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                val that = this@HomeFragment
                ThemeChangeLiveData.observe(that, {
                    mHomeBottomAdapter.setThemeChangeState(it)
                    viewThemeColor(it, homeTitle)
                })
            }

        }
    }

    companion object{
        const val ACTION_CUT=1
        const val ACTION_JOINT=2
        const val ACTION_DIVISION=3
        const val ACTION_TAGS=4
        const val ACTION_MUSIC=5
        const val ACTION_REVERSE=6
        const val ACTION_SPEED=7
        const val ACTION_SIZE=8
    }

    private var openAction=0

    override fun initEvent() {
        binding.apply {
            homeTop.apply {
                editAction.setOnClickListener {
                    openMediaSelect()
                    openAction=ACTION_CUT
                }

                divisionAction.setOnClickListener {
                   // toOtherActivity<VideoCutActivity>(activity) {}
                }

                jointAction.setOnClickListener {
                    toOtherActivity<TCVideoPickerActivity>(activity) {}
                }
            }
        }

        mHomeBottomAdapter.setOnItemClickListener { adapter, view, position ->
            when(position){
                1->{
                    openMediaSelect()
                    openAction=ACTION_REVERSE
                }
                2->{}
                0->{}
                3->{}
            }

        }



        mLoadingPopup.cancelMake {
           mProcessHelper.stopProcess()
            mLoadingPopup.dismiss()
            viewModel.setMakeState(true)
        }


    }

    private fun openMediaSelect() {
        PictureSelector.create(this@HomeFragment)
                .openGallery(PictureConfig.TYPE_VIDEO)
                .imageSpanCount(3)// 每行显示个数 int
                .maxSelectNum(1)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)
                .forResult(Constants.REQUEST_VIDEO_CUT);//结果回调onActivityResult code
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 结果回调
        PictureSelector.obtainMultipleResult(data)?.let {
            if (it.size>0){
                when(requestCode){
                    Constants.REQUEST_VIDEO_CUT -> {
                        preVideo(it[0].path)
                    }
                }
            }
        }
    }


    private fun preVideo(videoPath:String){
        mLoadingPopup.showPopupView(binding.bottomContainer)
        mVideoEditorHelper.apply {
            releaseSDK()
            clear()
            initSDK()
            setVideoPathInfo(videoPath)
        }
        mProcessHelper.apply {
            stopProcess()
            setOnUpdateUIListener(this@HomeFragment)
            startTwProcess()
        }
    }

    override fun onUIProgress(progress: Float) {
        mLoadingPopup.setProgress((progress*100).toInt())
        viewModel.setMakeState(false)
        LogUtils.i("-----setOnCutListener---${Thread.currentThread().name}----------${(progress * 100).toInt()}-------------")
    }

    override fun onUIComplete(retCode: Int, descMsg: String?) {
        val ugcKitResult = UGCKitResult()
        ugcKitResult.errorCode = retCode
        ugcKitResult.descMsg = descMsg
        mLoadingPopup.dismiss()
        toEditPage()
        viewModel.setMakeState(true)
    }

    override fun onUICancel() {
        viewModel.setMakeState(true)
    }


    private fun toEditPage(){
        when(openAction){
            ACTION_CUT-> toOtherActivity<VideoCutActivity>(activity){}
            ACTION_REVERSE->toOtherActivity<ReverseActivity>(activity){}
        }
    }


    override fun release() {
        mLoadingPopup.dismiss()
        mProcessHelper.stopProcess()
        mVideoEditorHelper.apply {
            releaseSDK()
            clear()
        }

    }


}