package com.twx.module_videoediting.ui.adapter.recycleview.video.cut

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.module_base.utils.SizeUtils
import com.twx.module_base.utils.tools.RxDeviceTool
import com.twx.module_videoediting.ui.fragment.HomeFragment


/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.adapter.recycleview.video.cut
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/5/6 13:46:25
 * @class describe
 */
class CutAdapter:RecyclerView.Adapter<CutAdapter.MyHolder>(){
    private val dataList: MutableList<Bitmap> = ArrayList()

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setItemData(thumbnailBitmapInfo: Bitmap, position: Int) {
            Glide.with(itemView.context).load(thumbnailBitmapInfo).into(itemView as ImageView)
        }
    }

    override fun getItemCount(): Int=dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val screenWidth = (RxDeviceTool.getScreenWidth(parent.context)-SizeUtils.dip2px(parent.context,50f))/HomeFragment.CUT_COUNT
        val view = ImageView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(screenWidth,  ViewGroup.LayoutParams.MATCH_PARENT)
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setItemData(dataList[position],position)
    }

    fun setBitmap(bitmap: Bitmap,index:Int){
        dataList.add(bitmap)
        notifyItemInserted(index)
    }


    fun setThumbnailList(list: MutableList<Bitmap>){
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearAllBitmap() {
        dataList.clear()
    }


}