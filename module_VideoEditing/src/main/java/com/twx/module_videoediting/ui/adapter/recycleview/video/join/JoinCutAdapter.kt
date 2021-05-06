package com.twx.module_videoediting.ui.adapter.recycleview.video.join

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.ugc.TXVideoEditer
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.ItemJoinCutContainerBinding
import com.twx.module_videoediting.domain.VideoEditorInfo
import com.twx.module_videoediting.ui.widget.video.join.JoinEditorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.join
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 16:25:52
 * @class describe
 */
class JoinCutAdapter : RecyclerView.Adapter<JoinCutAdapter.MyHolder>() {


    class MyHolder(itemView: View,val itemBinding:ItemJoinCutContainerBinding) : RecyclerView.ViewHolder(itemView) {
        fun setItemData(videoEditorInfo: VideoEditorInfo, position: Int) {
            itemBinding?.apply {
                LogUtils.i("----MyHolder----------${   videoEditorInfo.joinHelper.getVideoInfo().duration}----------------")

                videoEditorInfo.joinHelper.apply {
                    cutSliderView.setMediaFileInfo(getVideoInfo())
                        getEditor()?.getThumbnail(4, IVideoCutLayout.DEFAULT_THUMBNAIL_WIDTH, IVideoCutLayout.DEFAULT_THUMBNAIL_HEIGHT, false) { index, times, bitmap ->
                            LogUtils.i("----MyHolder------${index}----${times}----------------")
                            GlobalScope.launch(Dispatchers.Main) {
                                cutSliderView.addBitmapList(bitmap, index)
                            }
                        }


                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBinding = DataBindingUtil.inflate<ItemJoinCutContainerBinding>(LayoutInflater.from(parent.context), R.layout.item_join_cut_container, parent, false)
        return MyHolder(itemBinding.root,itemBinding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setItemData(JoinEditorManager.getEditorList()[position],position)
    }

    override fun getItemCount(): Int = JoinEditorManager.getEditorList().size


}