package com.twx.module_videoediting.ui.activity

import android.graphics.Bitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.tencent.ugc.TXVideoEditer
import com.twx.module_base.base.BaseViewActivity
import com.twx.module_base.utils.*
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ActivityReadyJoinBinding
import com.twx.module_videoediting.domain.JoinData
import com.twx.module_videoediting.domain.ValueJoinList
import com.twx.module_videoediting.ui.adapter.recycleview.video.join.JoinCutAdapter
import com.twx.module_videoediting.ui.widget.video.join.JoinHelper
import com.twx.module_videoediting.utils.Constants
import com.twx.module_videoediting.utils.setBarEventAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ReadyJoinActivity : BaseViewActivity<ActivityReadyJoinBinding>() {
    private var count=0
    private val mJoinCutAdapter by lazy {
        JoinCutAdapter()
    }
   // private val editorList = ArrayList<JoinHelper>()
    private val pathList = ArrayList<String>()
    private val editorList = ArrayList<TXVideoEditer>()
    private val bitmapList =  ArrayList<MutableList<Bitmap>>()



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
                            pathList.add(it.path)
                        }
                        initPlayer(it[0].path)
                        mLoadingDialog.show()
                        getThumbnail()
                    }
                }
            }
        }
    }





    private fun ActivityReadyJoinBinding.initPlayer(path: String) {
        //初始好播放器
        GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setNeedLockFull(false)
                .setRotateWithSystem(false)
                .setAutoFullWithSize(false)
                .setRotateViewAuto(false)
                .setShowFullAnimation(false)
                .setLooping(true)
                .build(mJoinPlayerView)
           setVideoPath(path)
    }


    private fun setVideoPath(path: String){
        binding.mJoinPlayerView.apply {
            setUp(path, true, "")
            startPlayLogic()
        }
    }


    private fun  getThumbnail(){
        mScope.launch {
            if (pathList.size > count) {
                bitmapList.add(arrayListOf())
                TXVideoEditer(this@ReadyJoinActivity).apply {
                    setVideoPath(pathList[count])
                }.getThumbnail(6, 100, 100, true) { i, j, k ->
                    bitmapList[count].add(k)
                    if (i >= 5) {
                        count++
                        getThumbnail()
                    }
                        LogUtils.i("---getThumbnail----00----------$i---$j--$count-")
                    }
            } else {
               withContext(Dispatchers.Main){
                   mJoinCutAdapter.setJoinData(JoinData(bitmapList, pathList))
                   mLoadingDialog.dismiss()
                    LogUtils.i("---getThumbnail---end    end     end-")
                }
            }
        }
    }



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
                override fun selectClick(data: JoinData, position: Int,startTime:Long) {
                    binding.mJoinPlayerView.seekOnStart=startTime
                    setVideoPath(data.pathList[position])
                }
            })


        }
    }


    override fun onResume() {
        super.onResume()
        binding.mJoinPlayerView.onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mJoinPlayerView.onVideoPause()
    }



    override fun release() {
        super.release()
        mLoadingDialog.dismiss()
        binding.mJoinPlayerView.release()
        GSYVideoManager.releaseAllVideos()
    }

}