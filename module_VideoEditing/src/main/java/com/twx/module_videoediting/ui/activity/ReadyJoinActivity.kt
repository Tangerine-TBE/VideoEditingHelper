package com.twx.module_videoediting.ui.activity

import android.graphics.Bitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReadyJoinBinding
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.domain.VideoEditorInfo
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinAdapter
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinCutAdapter
import com.twx.module_videoediting.ui.widget.video.join.JoinEditorManager
import com.twx.module_videoediting.ui.widget.video.join.JoinHelper
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.FileUtils
import com.twx.module_videoediting.utils.setBarEventAction
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ReadyJoinActivity : BaseViewActivity<ActivityReadyJoinBinding>() {

    private val mJoinCutAdapter by lazy {
        JoinCutAdapter()
    }


    override fun getLayoutView(): Int = R.layout.activity_ready_join
    override fun initView() {
        binding.apply {
            viewThemeColor(themeState, readyJoinContainer)
            setStatusBarDistance(this@ReadyJoinActivity, readyJoinTitleBar, LayoutType.CONSTRAINTLAYOUT)
            cutViewContainer.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mJoinCutAdapter
            }

            intent.getStringExtra(Constants.KEY_VIDEO_PATH)?.let { it ->
                gsonHelper<ValueJoinList>(it)?.joinList?.let { it ->
                    if (it.size > 0) {
                        it.forEach {
                            JoinEditorManager.createEditor(it.path)
                        }
                        mVideoJoinPlayerControl.initPlayerLayout(JoinEditorManager.getEditorList()[1].joinHelper)

                        getThumbnail()
                    }
                }
            }
        }
    }

    private fun  getThumbnail(){
        mScope.launch {
            if (JoinEditorManager.getEditorList().size > count) {
                JoinEditorManager.getEditorList()[count].joinHelper.getEditor()
                    .getThumbnail(6, 100, 100, true) { i, j, k ->
                        if (i == 6 - 1) {
                            JoinEditorManager.getEditorList()[count].joinHelper.getBitmapList()
                                .add(k)
                            count++
                            getThumbnail()
                        }
                        LogUtils.i("---getThumbnail----00----------$i---$j--$count-")
                    }
            } else {
                mScope.launch (Dispatchers.Main){
                    mJoinCutAdapter.setVideoEditorInfo(JoinEditorManager.getEditorList())
                    LogUtils.i("---getThumbnail---end    end     end-")
                }
            }
        }
    }

    private var count=0

    override fun initEvent() {
        binding.apply {
            readyJoinTitleBar.setBarEventAction(this@ReadyJoinActivity) {
                /*   if (mJoinAdapter.getData().size >= 2) {
                       toOtherActivity<JoinActivity>(this@ReadyJoinActivity){
                           putExtra(Constants.KEY_VIDEO_PATH,Gson().toJson(ValueJoinList(mJoinAdapter.getData())))
                       }
                   } else {
                       showToast("必须选择两个以上的视频文件")
                   }*/
            }

/*            joinContainer.setOnItemMoveListener(object : OnItemMoveListener {
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
            })*/

            mJoinCutAdapter.setOnListener(object : JoinCutAdapter.OnListener {
                override fun selectClick(videoEditorInfo: VideoEditorInfo) {
                    mVideoJoinPlayerControl.initPlayerLayout(videoEditorInfo.joinHelper)
                }
            })


        }
    }

    override fun release() {
        super.release()
        JoinEditorManager.release()
    }

}