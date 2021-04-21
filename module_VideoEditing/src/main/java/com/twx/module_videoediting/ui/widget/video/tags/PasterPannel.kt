package com.twx.module_videoediting.ui.widget.video.tags

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutVideoPasterContainerBinding
import com.twx.module_videoediting.repository.DataProvider
import com.twx.module_videoediting.ui.adapter.recycleview.video.tags.PasterAdapter
import com.twx.module_videoediting.ui.adapter.recycleview.video.tags.TagsTypeAdapter
import com.twx.module_videoediting.ui.widget.video.ganeral.BaseUi

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.tags
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/20 10:17:20
 * @class describe
 */
class PasterPannel @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : BaseUi(context, attrs, defStyleAttr) {

    companion object{
        const val STATIC_TAGS_FRUITS=0
        const val STATIC_TAGS_HALLOWEEN = 1
        const val STATIC_TAGS_TRANSPORT=2
        const val STATIC_TAGS_CHRIST=3
        const val STATIC_TAGS_ANIMAL=4
    }

    private val binding = DataBindingUtil.inflate<LayoutVideoPasterContainerBinding>(
            LayoutInflater.from(context),
            R.layout.layout_video_paster_container,
            this,
            true
    )
    private val mTagsTypeAdapter by lazy {
        TagsTypeAdapter()
    }

    private val mPasterAdapter by lazy {
        PasterAdapter()
    }


    init {
        binding.apply {
            viewThemeColor(themeState,pasterSelectContainer)
            pasterTypeContainer.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                mTagsTypeAdapter.setList(DataProvider.tagsTypeList)
                adapter=mTagsTypeAdapter
            }

            pasterItemContainer.apply {
                layoutManager=GridLayoutManager(context,4)
                adapter=mPasterAdapter
            }
            switchPasterList(0)

            initEvent()
        }

    }


    private fun initEvent() {
        mTagsTypeAdapter.setOnItemClickListener { adapter, view, position ->
            switchPasterList(position)
        }

        binding.dismissAction.setOnClickListener {
            dismissAction()
        }

    }

   private var dismissAction:()->Unit={}

    fun setDismissAction(action:()->Unit){
        dismissAction=action
    }


    //切换贴纸类型
    private fun switchPasterList(position:Int){
        mTagsTypeAdapter.setPosition(position)
        val pasterList = when (position) {
            STATIC_TAGS_FRUITS -> DataProvider.tagsFruitsList
            STATIC_TAGS_HALLOWEEN -> DataProvider.tagsHalloweenList
            STATIC_TAGS_CHRIST -> DataProvider.tagsChristList
            STATIC_TAGS_ANIMAL -> DataProvider.tagsAnimalList
            STATIC_TAGS_TRANSPORT -> DataProvider.tagsTransportList
            else->DataProvider.tagsFruitsList
        }
        mPasterAdapter.setList(pasterList)
    }


    fun getPasterAdapter()=mPasterAdapter

}