package com.twx.module_videoediting.ui.activity

import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.twx.module_base.base.BaseVmViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReadyJoinBinding
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.livedata.ThemeChangeLiveData
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinAdapter
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import com.twx.module_videoediting.viewmodel.ReadyJoinViewModel
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener
import java.util.*
import kotlin.collections.ArrayList

class ReadyJoinActivity : BaseVmViewActivity<ActivityReadyJoinBinding, ReadyJoinViewModel>() {

    private val mJoinAdapter by lazy {
        JoinAdapter()
    }

    override fun getViewModelClass(): Class<ReadyJoinViewModel> {
        return ReadyJoinViewModel::class.java
    }

    override fun getLayoutView(): Int=R.layout.activity_ready_join



    override fun initView() {
        binding.apply {
            setStatusBarDistance(this@ReadyJoinActivity, readyJoinTitleBar, LayoutType.CONSTRAINTLAYOUT)
            joinContainer.apply {
                val divider: GridItemDecoration = GridItemDecoration.Builder(this@ReadyJoinActivity)
                    .setColorResource(R.color.color_divider)
                    .setShowLastLine(false)
                    .build()
                addItemDecoration(divider)

                layoutManager=LinearLayoutManager(this@ReadyJoinActivity)
                adapter=mJoinAdapter
                isLongPressDragEnabled=true
                isItemViewSwipeEnabled=true
            }
            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.let {
                    if (it.joinList.size>0){
                        mJoinAdapter.setItemList(it.joinList)
                    }
                }
            }
        }
    }


    override fun observerData() {
        binding.apply {
            viewModel.apply {
                ThemeChangeLiveData.observe(this@ReadyJoinActivity, {
                    readyJoinTitleBar.setThemeChange(it)
                    viewThemeColor(it, readyJoinContainer)
                })

            }
        }
    }

    override fun initEvent() {
        binding.apply {
            readyJoinTitleBar.setBarEventAction(this@ReadyJoinActivity){
                if (mJoinAdapter.getData().size >= 2) {
                    toOtherActivity<JoinActivity>(this@ReadyJoinActivity){
                        putExtra(Constants.KEY_VIDEO_PATH,Gson().toJson(ValueJoinList(mJoinAdapter.getData())))
                    }
                } else {
                    showToast("必须选择两个以上的视频文件")
                }
            }

            joinContainer.setOnItemMoveListener(object : OnItemMoveListener {
                override fun onItemMove(
                    srcHolder: RecyclerView.ViewHolder,
                    targetHolder: RecyclerView.ViewHolder
                ): Boolean {
                    // 不同的ViewType不能拖拽换位置。
                    if (srcHolder.itemViewType != targetHolder.itemViewType) return false

                    val fromPosition = srcHolder.adapterPosition
                    val toPosition = targetHolder.adapterPosition

                    Collections.swap(mJoinAdapter.getData(), fromPosition, toPosition)
                    mJoinAdapter.notifyItemMoved(fromPosition, toPosition)
                    return true // 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
                }

                override fun onItemDismiss(srcHolder: RecyclerView.ViewHolder) {
                    val position = srcHolder.adapterPosition
                    mJoinAdapter.getData().removeAt(position)
                    mJoinAdapter.notifyItemRemoved(position)
                }
            })

        }
    }

}