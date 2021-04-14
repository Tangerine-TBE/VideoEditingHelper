package com.twx.module_videoediting.ui.popup
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.twx.module_base.base.BasePopup
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.PopupItemSelectBinding
import com.twx.module_videoediting.domain.ItemBean
import com.twx.module_videoediting.domain.MediaInformation
import com.twx.module_videoediting.ui.adapter.recycleview.PopupDeleteItemAdapter
import com.twx.module_videoediting.ui.adapter.recycleview.PopupSelectItemAdapter

/**
 * @name VidelPlayer
 * @class name：com.example.module_video.ui.widget.popup
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/8 16:59:32
 * @class describe
 */
class ItemSelectPopup(activity:FragmentActivity?): BasePopup<PopupItemSelectBinding>(activity, R.layout.popup_item_select,ViewGroup.LayoutParams.MATCH_PARENT) {
    private var mPopupDeleteItemAdapter: PopupSelectItemAdapter = PopupSelectItemAdapter()


    fun setTitleNormal(media:MediaInformation,list:List<ItemBean>){
        mView.itemSelectContainer.apply {
            adapter=mPopupDeleteItemAdapter
            layoutManager=LinearLayoutManager(activity)
            mPopupDeleteItemAdapter.setList(list)
        }
        mView.selectTitle.text="${media.name}"

    }



    fun setItemAction(vararg block: ()->Unit){
        mPopupDeleteItemAdapter?.setOnItemClickListener { adapter, view, position ->
            block[position]()
            dismiss()
        }
    }



}