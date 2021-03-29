package com.twx.module_videoediting.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.tencent.liteav.demo.videoediter.TCVideoPickerActivity
import com.twx.module_base.base.BaseVmFragment
import com.twx.module_base.utils.LogUtils
import com.twx.module_base.utils.toOtherActivity
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.FragmentHomeBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.activity.VideoCutActivity
import com.twx.module_videoediting.ui.adapter.recycleview.HomeBottomAdapter
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
class HomeFragment : BaseVmFragment<FragmentHomeBinding, MainViewModel>() {

    private val mHomeBottomAdapter by lazy {
        HomeBottomAdapter()
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


    override fun initEvent() {
        binding.apply {
            homeTop.apply {
                editAction.setOnClickListener {
                    openMediaSelect(Constants.REQUEST_VIDEO_CUT)
                }

                divisionAction.setOnClickListener {
                    toOtherActivity<VideoCutActivity>(activity) {}
                }

                jointAction.setOnClickListener {
                    toOtherActivity<TCVideoPickerActivity>(activity) {}
                }


            }

        }

    }

    private fun openMediaSelect(requestCode: Int) {
        PictureSelector.create(this@HomeFragment)
                .openGallery(PictureConfig.TYPE_VIDEO)
                .imageSpanCount(3)// 每行显示个数 int
                .maxSelectNum(1)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)
                .forResult(requestCode);//结果回调onActivityResult code
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 结果回调
        PictureSelector.obtainMultipleResult(data)?.let {
            if (it.size>0){
                when(requestCode){
                    Constants.REQUEST_VIDEO_CUT->{
                        toOtherActivity<VideoCutActivity>(activity) {
                            putExtra(Constants.KEY_VIDEO_PATH,it[0].path)
                        }
                    }
                }
            }
        }

    }

}