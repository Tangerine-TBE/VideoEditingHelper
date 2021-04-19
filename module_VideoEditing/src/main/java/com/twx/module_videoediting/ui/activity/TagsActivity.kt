package com.twx.module_videoediting.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityTagsBinding
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.TagsViewModel

class TagsActivity : BaseVmViewActivity<ActivityTagsBinding,TagsViewModel>() {

    override fun getViewModelClass(): Class<TagsViewModel> {
        return TagsViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_tags


    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@TagsActivity, tagsTitleBar, LayoutType.CONSTRAINTLAYOUT)
            mVideoTagsContainer.initPlayerLayout()
            lifecycle.addObserver(mVideoTagsContainer.getPlayerView())


        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@TagsActivity, {
                    tagsTitleBar.setThemeChange(it)
                    viewThemeColor(it, tagsContainer)
                })


            }
        }
    }

    override fun initEvent() {
        binding.apply {
            tagsTitleBar.setBarEventAction(this@TagsActivity) {}
        }
    }
}