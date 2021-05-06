package com.twx.module_videoediting.ui.activity

import android.content.Intent
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.tencent.qcloud.ugckit.basic.OnUpdateUIListener
import com.tencent.qcloud.ugckit.module.ProcessKit
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityExportBinding
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.video.cut.ExportAdapter
import com.twx.module_videoediting.ui.fragment.FileFragment
import com.twx.module_videoediting.ui.fragment.HomeFragment
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.setBarEventAction
import java.io.File

class ExportActivity : BaseViewActivity<ActivityExportBinding>(),
    OnUpdateUIListener {
    private val mExportAdapter by lazy {
        ExportAdapter()
    }
    private val mVideoEditorHelper by lazy {
        VideoEditerSDK.getInstance()
    }

    private val mProcessHelper by lazy {
        ProcessKit.getInstance()
    }


    companion object{
        fun toExportPage(activity:FragmentActivity?,isFinish: Boolean,path:String){
            toOtherActivity<ExportActivity>(activity,isFinish){
                putExtra(Constants.KEY_VIDEO_PATH,path)
            }
        }
    }



    override fun getLayoutView(): Int=R.layout.activity_export

    private var mVideoPath=""

    override fun initView() {
        binding.apply {
            loadingPopup.setTitle("视频预加载中...")

            viewThemeColor(themeState, videoExportContainer,exportHint)
            setStatusBarDistance(this@ExportActivity, exportTitleBar, LayoutType.LINEARLAYOUT)

            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                mVideoPath=it
                mVideoEditorHelper.apply {
                    releaseSDK()
                    clear()
                    initSDK()
                    setVideoPathInfo(it)
                }
                mVideoPlayerView.initPlayerLayout()
               lifecycle.addObserver(mVideoPlayerView)
             //   VideoFileLiveData.refreshData()
            }

            choseEditContainer.apply {
                layoutManager=GridLayoutManager(this@ExportActivity,4)
                mExportAdapter.setList(DataProvider.editList)
                adapter=mExportAdapter
            }

        }
    }



    private var openAction = 0
    override fun initEvent() {
        binding.apply {
            myVideo.setOnClickListener {
                MainViewActivity.toFileFragment(this@ExportActivity)
            }


            exportTitleBar.setBarEventAction(this@ExportActivity) {
                if (!TextUtils.isEmpty(mVideoPath)) {
                    IntentUtils.shareVideo(this@ExportActivity, File(mVideoPath),"分享视频")
                }
            }


            mExportAdapter.setOnItemClickListener { adapter, view, position ->
                openAction=position
                when(position){
                    0->  preVideo(true,HomeFragment.CUT_COUNT)
                    2->  preVideo(true)
                    3,5-> preVideo(false)
                    1-> openMediaSelect(4, 1, PictureConfig.MULTIPLE)
                    4->toOtherActivity<MusicActivity>(this@ExportActivity,true) {
                        putExtra(
                            Constants.KEY_VIDEO_PATH,
                            mVideoPath
                        )
                    }
                    6->toOtherActivity<SpeedActivity>(this@ExportActivity,true) {
                        putExtra(
                            Constants.KEY_VIDEO_PATH,
                            mVideoPath
                        )
                    }
                    7->toOtherActivity<CropActivity>(this@ExportActivity,true) {
                        putExtra(
                                Constants.KEY_VIDEO_PATH,
                                mVideoPath
                        )
                    }

                }
            }

            loadingPopup.cancelMake {
                cancelMake(true)
            }

        }
    }

    private fun openMediaSelect(
        maxSelectNum: Int = 1,
        minSelectNum: Int = 1,
        selectionMode: Int = PictureConfig.SINGLE
    ) {
        PictureSelector.create(this)
            .openGallery(PictureConfig.TYPE_VIDEO)
            .imageSpanCount(3)// 每行显示个数 int
            .maxSelectNum(maxSelectNum)
            .minSelectNum(minSelectNum)
            .videoMaxSecond(600)
            .selectionMode(selectionMode)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
            .isCamera(false)// 是否显示拍照按钮 true or false
            .isZoomAnim(false)
            .forResult(HomeFragment.ACTION_JOINT);//结果回调onActivityResult code
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PictureSelector.obtainMultipleResult(data)?.let { it ->
            if (it.size > 0) {
            val mediaInfo = ArrayList<MediaInformation>()
            val txVideoInfo = TXVideoInfoReader.getInstance(this).getVideoFileInfo(mVideoPath)
            mediaInfo.add(MediaInformation(path = mVideoPath, duration = txVideoInfo.duration))
            it.forEach {
                mediaInfo.add(MediaInformation(path = it.path, duration = it.duration))
                LogUtils.i("---ACTION_JOINT------${it.path}-----------------")
            }
            toOtherActivity<ReadyJoinActivity>(this,true) {
                putExtra(
                    Constants.KEY_VIDEO_PATH,
                    Gson().toJson(ValueJoinList(mediaInfo))
                )
            }
            }
        }
    }


    private fun preVideo(isSpecial: Boolean,count:Int=4){
        loadingPopup.showPopupView(binding.videoExportContainer)
        mProcessHelper.apply {
            stopProcess()
            setOnUpdateUIListener(this@ExportActivity)
            if (isSpecial) {
                startSpecialProcess(count)
            } else {
                startNormalProcess()
            }
        }
    }

    override fun onUIProgress(progress: Float) {
        loadingPopup.setProgress((progress * 100).toInt())
        MakeBackLiveData.setMakeFinishState(false)
    }

    override fun onUIComplete(retCode: Int, descMsg: String?) {
        loadingPopup.dismiss()
        toEditPage()
        MakeBackLiveData.setMakeFinishState(true)
    }

    override fun onUICancel() {
        MakeBackLiveData.setMakeFinishState(true)
    }

    private fun toEditPage() {
        when (openAction) {
            HomeFragment.ACTION_CUT -> toOtherActivity<CutActivity>(this,true) {}
            HomeFragment.ACTION_REVERSE -> toOtherActivity<ReverseActivity>(this,true) {}
            HomeFragment.ACTION_DIVISION -> toOtherActivity<DivisionActivity>(this,true) {}
            HomeFragment.ACTION_TAGS -> toOtherActivity<TagsActivity>(this,true) {}

        }
    }

    override fun release() {
        super.release()
        mProcessHelper.stopProcess()
    }

}