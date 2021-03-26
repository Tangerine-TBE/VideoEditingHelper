package com.twx.module_videoediting.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
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
                titleBarIcon.setColorFilter(getColor(R.styleable.TitleBar_barBackIconColor,Color.BLACK))
                titleBarIvAction.visibility=if (getBoolean(R.styleable.TitleBar_barHaveIvAction,false)) View.VISIBLE
                else View.GONE
                titleBarTvAction.visibility=if (getBoolean(R.styleable.TitleBar_barHaveTvAction,false)) View.VISIBLE
                else View.GONE
                titleBarTvAction.text=getString(R.styleable.TitleBar_barTvActionTitle)

                recycle()
            }
        }
        initEvent()
    }



    private fun initEvent() {

    }



    fun setThemeChange(){

    }

}