package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityExportBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.video.cut.ExportAdapter
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ExportViewModel
import java.io.File

class ExportActivity : BaseVmViewActivity<ActivityExportBinding,ExportViewModel>() {

    private val mExportAdapter by lazy {
        ExportAdapter()
    }

    private val mHelper by lazy {
        VideoEditerSDK.getInstance().apply {
            releaseSDK()
            clear()
            initSDK()
        }
    }


    companion object{
        fun toExportPage(activity:FragmentActivity?,isFinish: Boolean,path:String){
            toOtherActivity<ExportActivity>(activity,isFinish){
                putExtra(Constants.KEY_VIDEO_PATH,path)
            }
        }
    }

    override fun getViewModelClass(): Class<ExportViewModel> {
        return ExportViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_export

    private var mVideoPath=""

    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@ExportActivity, exportTitleBar, LayoutType.CONSTRAINTLAYOUT)

            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let {
                mVideoPath=it
                mHelper.setVideoPathInfo(it)
                mVideoPlayerView.initPlayerLayout()
               lifecycle.addObserver(mVideoPlayerView)
            }

            choseEditContainer.apply {
                layoutManager=GridLayoutManager(this@ExportActivity,4)
                mExportAdapter.setList(DataProvider.editList)
                adapter=mExportAdapter
            }

        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@ExportActivity, {
                    exportTitleBar.setThemeChange(it)
                    viewThemeColor(it, videoExportContainer,exportHint)
                })

            }
        }

    }

    override fun initEvent() {
        binding.apply {
            exportTitleBar.setBarEventAction(this@ExportActivity) {
                if (!TextUtils.isEmpty(mVideoPath)) {
                    IntentUtils.shareVideo(this@ExportActivity, File(mVideoPath),"分享视频")
                }
            }

            myVideoBt.setOnClickListener {

            }

        }
    }


}