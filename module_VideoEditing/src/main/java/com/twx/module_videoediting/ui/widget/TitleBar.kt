package com.twx.module_videoediting.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.twx.module_base.utils.viewThemeColor
import com.twx.module_videoediting.R
import com.twx.module_videoediting.databinding.LayoutTitleBarContainerBinding


/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/3/26 13:57:03
 * @class describe
 */
class TitleBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val binding=DataBindingUtil.inflate<LayoutTitleBarContainerBinding>(LayoutInflater.from(context),
        R.layout.layout_title_bar_container,this,true)

    init {
        binding.apply {
            context.obtainStyledAttributes(attrs,R.styleable.TitleBar).apply {
                titleBarTitle.text=getString(R.styleable.TitleBar_barTitle)?:""
                titleBarIvAction.visibility=if (getBoolean(R.styleable.TitleBar_barHaveIvAction,false)) View.VISIBLE
                else View.GONE
                titleBarTvAction.visibility=if (getBoolean(R.styleable.TitleBar_barHaveTvAction,false)) View.VISIBLE
                else View.GONE
                titleBarTvAction.text=getString(R.styleable.TitleBar_barTvActionTitle)

                val resourceId = getResourceId(R.styleable.TitleBar_barIvActionIcon, -1)
                if (resourceId!=-1)  titleBarIvAction.setImageResource(resourceId)



                recycle()
            }
        }
        initEvent()
    }



    private fun initEvent() {
        binding.apply {
            titleBarIcon.setOnClickListener {
                mListener?.backAction()
            }


            titleBarTvAction.setOnClickListener {
                mListener?.rightAction()
            }


            titleBarIvAction.setOnClickListener {
                mListener?.rightAction()
            }
        }
    }


    fun setThemeChange(state: Boolean) {
        binding.apply {
            viewThemeColor(state,titleBarIcon,titleBarTitle)
        }
    }

    private var mListener:OnBarActionListener?=null
    fun setOnBarActionListener(listener:OnBarActionListener){
        mListener=listener
    }


    interface OnBarActionListener{
        fun backAction()

        fun rightAction()
    }

}

