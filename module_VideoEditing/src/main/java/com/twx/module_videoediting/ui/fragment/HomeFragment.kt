package com.twx.module_videoediting.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.tencent.liteav.demo.videoediter.TCVideoPickerActivity
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_base.base.BaseVmFragment
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.toOtherActivity
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.FragmentHomeBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.HomeBottomAdapter
import com.twx.module_base.widget.popup.LoadingPopup
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.ui.activity.*
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
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
                mHomeBottomAdapter.setList(DataProvider.bottomList)
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

    companion object {
        const val ACTION_CUT = 0
        const val ACTION_JOINT = 1
        const val ACTION_DIVISION = 2
        const val ACTION_TAGS = 3
        const val ACTION_MUSIC = 4
        const val ACTION_REVERSE = 5
        const val ACTION_SPEED = 6
        const val ACTION_SIZE = 7
    }

    private var openAction = -1

    override fun initEvent() {
        binding.apply {
            homeTop.apply {
                editAction.setOnClickListener {
                    openMediaSelect(ACTION_CUT)

                }

                divisionAction.setOnClickListener {
                    openMediaSelect(ACTION_DIVISION)
                }

                jointAction.setOnClickListener {
                    openMediaSelect(ACTION_JOINT, 5, 2, PictureConfig.MULTIPLE)
                }

                tagsAction.setOnClickListener {
                    openMediaSelect(ACTION_TAGS)
                }

            }
        }

        mHomeBottomAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> openMediaSelect(ACTION_MUSIC)
                1 -> openMediaSelect(ACTION_REVERSE)
                2 -> openMediaSelect(ACTION_SPEED)
                3 -> {
                    toOtherActivity<TCVideoPickerActivity>(activity) {}
                }
            }

        }

        mLoadingPopup.cancelMake {
            cancelMake(true)
        }


    }

    private fun openMediaSelect(
        action: Int,
        maxSelectNum: Int = 1,
        minSelectNum: Int = 1,
        selectionMode: Int = PictureConfig.SINGLE
    ) {
        openAction = action
        PictureSelector.create(this@HomeFragment)
            .openGallery(PictureConfig.TYPE_VIDEO)
            .imageSpanCount(3)// 每行显示个数 int
            .maxSelectNum(maxSelectNum)
            .minSelectNum(minSelectNum)
            .videoMaxSecond(3600)
            .selectionMode(selectionMode)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
            .isCamera(false)// 是否显示拍照按钮 true or false
            .isZoomAnim(false)
            .forResult(action);//结果回调onActivityResult code
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 结果回调
        PictureSelector.obtainMultipleResult(data)?.let { it ->
            if (it.size > 0) {
                val path = it[0].path
                when (requestCode) {
                    ACTION_CUT, ACTION_REVERSE, ACTION_DIVISION, ACTION_TAGS-> preVideo(path)
                    ACTION_MUSIC -> toOtherActivity<MusicActivity>(activity) {
                        putExtra(
                            Constants.KEY_VIDEO_PATH,
                            path
                        )
                    }
                    ACTION_JOINT -> {
                        val mediaInfo = ArrayList<MediaInformation>()
                        it.forEach {
                            mediaInfo.add(MediaInformation(path = it.path, duration = it.duration))
                            LogUtils.i("---ACTION_JOINT------${it.path}-----------------")
                        }
                        toOtherActivity<ReadyJoinActivity>(activity) {
                            putExtra(
                                Constants.KEY_VIDEO_PATH,
                                Gson().toJson(ValueJoinList(mediaInfo))
                            )
                        }
                    }
                    ACTION_SPEED -> toOtherActivity<SpeedActivity>(activity) {
                        putExtra(
                            Constants.KEY_VIDEO_PATH,
                            path
                        )
                    }
                }
            }
        }
    }


    private fun preVideo(videoPath: String) {
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
        mLoadingPopup.setProgress((progress * 100).toInt())
        MakeBackLiveData.setMakeFinishState(false)
        LogUtils.i("-----setOnCutListener---${Thread.currentThread().name}----------${(progress * 100).toInt()}-------------")
    }

    override fun onUIComplete(retCode: Int, descMsg: String?) {
        mLoadingPopup.dismiss()
        toEditPage()
        MakeBackLiveData.setMakeFinishState(true)
    }

    override fun onUICancel() {
        MakeBackLiveData.setMakeFinishState(true)
    }


    private fun toEditPage() {
        when (openAction) {
            ACTION_CUT -> toOtherActivity<CutActivity>(activity) {}
            ACTION_REVERSE -> toOtherActivity<ReverseActivity>(activity) {}
            ACTION_DIVISION -> toOtherActivity<DivisionActivity>(activity) {}
            ACTION_TAGS -> toOtherActivity<TagsActivity>(activity) {}
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