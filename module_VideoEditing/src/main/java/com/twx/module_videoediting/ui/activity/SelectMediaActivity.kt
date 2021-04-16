package com.twx.module_videoediting.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivitySelectMediaBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.adapter.recycleview.video.music.AudioAdapter
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.SelectMediaViewModel

class SelectMediaActivity : BaseVmViewActivity<ActivitySelectMediaBinding, SelectMediaViewModel>() {

    companion object {
        const val REQUEST_CODE = 10000
    }

    private val mAudioAdapter by lazy {
        AudioAdapter()
    }

    override fun getViewModelClass(): Class<SelectMediaViewModel> {
        return SelectMediaViewModel::class.java
    }

    override fun getLayoutView(): Int = R.layout.activity_select_media

    override fun initView() {
        viewModel.getAudioData()
        binding.apply {
            setStatusBarDistance(this@SelectMediaActivity, mediaTitleBar, LayoutType.CONSTRAINTLAYOUT)
            audioContainer.apply {
                val divider: GridItemDecoration = GridItemDecoration.Builder(this@SelectMediaActivity)
                        .setColorResource(R.color.color_divider)
                        .setShowLastLine(false)
                        .build()
                addItemDecoration(divider)
                layoutManager = LinearLayoutManager(context)
                adapter = mAudioAdapter
            }
        }
    }

    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@SelectMediaActivity, {
                    mediaTitleBar.setThemeChange(it)
                    viewThemeColor(it, mediaContainer)
                })

                audioList.observe(this@SelectMediaActivity, {
                    mAudioAdapter.setList(it)
                })

            }
        }
    }

    override fun initEvent() {
        binding.mediaTitleBar.setBarEventAction(this){}

        mAudioAdapter.setOnItemClickListener { adapter, view, position ->
            val item = mAudioAdapter.getItem(position)
            setResult(REQUEST_CODE, intent.putExtra(com.twx.module_videoediting.utils.Constants.KEY_AUDIO_INFO, Gson().toJson(item)))
            finish()
        }

    }

}