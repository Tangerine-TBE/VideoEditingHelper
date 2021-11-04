package com.twx.module_base.widget.popup

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.twx.module_base.R
import com.twx.module_base.activity.DealViewActivity
import com.twx.module_base.adapter.PermissionAdapter
import com.twx.module_base.base.BasePopup
import com.twx.module_base.databinding.PopupAgreementWindowBinding
import com.twx.module_base.domain.ItemBean
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.PackageUtil
import com.twx.module_base.utils.showToast
import com.twx.module_base.utils.toOtherActivity

/**
 * @name Wifi_Manager
 * @class name：com.example.wifi_manager.ui.popup
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/1/28 13:42:27
 * @class describe
 */
class AgreementPopup(activity: FragmentActivity):
    BasePopup<PopupAgreementWindowBinding>(activity, R.layout.popup_agreement_window,ViewGroup.LayoutParams.MATCH_PARENT) {

    val permissionList = arrayListOf(
        ItemBean( title = "储存", hint = "获取手机的音视频信息"),
    )

    private val mAppName=PackageUtil.getAppMetaData(activity, Constants.APP_NAME)

    private val textColor=ContextCompat.getColor(mView.root.context,R.color.theme_color)

    private val mPermissionAdapter by lazy {
        PermissionAdapter()
    }

    init {
        isFocusable=false
        isOutsideTouchable =false


        mView.apply {
            welcomeTitle.text="欢迎使用${mAppName}"
            permissionContainer.layoutManager = LinearLayoutManager(activity)
            permissionContainer.adapter = mPermissionAdapter
            mPermissionAdapter.setList(permissionList)


            val str = activity.resources.getString(R.string.user_agreement)
            val stringBuilder = SpannableStringBuilder(str)
            val span1 = TextViewSpan1()
            stringBuilder.setSpan(span1, 10, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val span2 = TextViewSpan2()
            stringBuilder.setSpan(span2, 19, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            descriptions.text=stringBuilder
            descriptions.movementMethod= LinkMovementMethod.getInstance()
        }

    }

    override fun initEvent() {
        mView.apply {
            btCancel?.setOnClickListener {
//                showToast("您需要同意后才能继续使${mAppName}提供的服务")
                dismiss()
                mListener?.cancel()
            }

            btSure?.setOnClickListener {
                    dismiss()
                    mListener?.sure()

            }

        }

    }

    inner class TextViewSpan1 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color =textColor
        }

        override fun onClick(widget: View) {
            //点击事件
            toOtherActivity<DealViewActivity>(activity,false){
                putExtra(Constants.SET_DEAL1,1)
            }
        }
    }

    inner  class TextViewSpan2 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color =textColor
        }

        override fun onClick(widget: View) {
            //点击事件
            toOtherActivity<DealViewActivity>(activity,false){
             putExtra(Constants.SET_DEAL1, 2)
            }
        }
    }


}