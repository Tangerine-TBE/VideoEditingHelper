package com.twx.module_videoediting.ui.activity

import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.utils.LayoutType
import com.twx.module_base.utils.setStatusBarDistance
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityTagsBinding
import com.twx.module_videoediting.utils.cancelMake
import com.twx.module_videoediting.utils.outPutVideo
import com.twx.module_videoediting.utils.setBarEventAction

class TagsActivity : BaseViewActivity<ActivityTagsBinding>() {

    override fun getLayoutView(): Int=R.layout.activity_tags

    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, tagsContainer)
            setStatusBarDistance(this@TagsActivity, tagsTitleBar, LayoutType.CONSTRAINTLAYOUT)
            mVideoTagsContainer.initPlayerLayout()
            lifecycle.addObserver(mVideoTagsContainer.getPlayerView())
        }
    }


    override fun initEvent() {
        binding.apply {
            mVideoTagsContainer.setOnCutListener(outPutVideo(loadingPopup,this@TagsActivity))
            tagsTitleBar.setBarEventAction(this@TagsActivity){
                mVideoTagsContainer.startExportVideo()
                loadingPopup.showPopupView(tagsContainer)
            }

            loadingPopup.cancelMake {
                cancelMake(false)
            }

        }
    }


}