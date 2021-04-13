package com.twx.module_videoediting.ui.popup


import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.twx.module_base.base.BasePopup
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.PopupRemindWindowBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.ui.adapter.recycleview.PopupSelectItemAdapter

/**
 * @name VidelPlayer
 * @class name：com.example.module_video.ui.widget.popup
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/9 18:07:08
 * @class describe
 */
class RemindDeletePopup(activity: FragmentActivity?): BasePopup<PopupRemindWindowBinding>(activity, R.layout.popup_remind_window, ViewGroup.LayoutParams.MATCH_PARENT) {
    private  val mSelectItemAdapter= PopupSelectItemAdapter()
    override fun initEvent() {
        mView.apply {

            cancel.setOnClickListener {
                dismiss()
            }

            sure.setOnClickListener {
                mListener?.sure()
                dismiss()
            }



        }
    }

    fun setContent(list:MutableList<MediaInformation>){
       mView.remindContent.apply {
            layoutManager=LinearLayoutManager(activity)
            adapter=mSelectItemAdapter
            mSelectItemAdapter.setList(list)
            mSelectItemAdapter.notifyDataSetChanged()
        }
    }

    fun setTitle(title:String){
        mView.remindTitle.text="$title"
    }




}