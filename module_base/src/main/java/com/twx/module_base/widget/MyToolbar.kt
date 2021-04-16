package com.twx.module_base.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.twx.module_base.R
import com.twx.module_base.databinding.LayoutToolbarNewBinding

/**
 * @name WeatherOne
 * @class name：com.nanjing.tqlhl.ui.custom.mj15day
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2020/10/20 11:43
 * @class describe
 */
class MyToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding= DataBindingUtil.inflate<LayoutToolbarNewBinding>(LayoutInflater.from(context),R.layout.layout_toolbar_new,this,true)




    init {
        context.obtainStyledAttributes(attrs, R.styleable.MyToolbar).apply {
            mTitle = getString(R.styleable.MyToolbar_toolbarTitle) ?: "顶部栏"
            mRightTitle = getString(R.styleable.MyToolbar_rightTitle)
            mTitleColor = getColor(R.styleable.MyToolbar_titleColors, Color.WHITE)
            mBarBgColor = getColor(R.styleable.MyToolbar_barBgColor, Color.WHITE)
            mRightTitleColor = getColor(R.styleable.MyToolbar_rightTitleColor, Color.WHITE)
            mLeftIcon = getResourceId(R.styleable.MyToolbar_backIconStyle, R.drawable.icon_bar_white_back)
            mRightIcon = getResourceId(R.styleable.MyToolbar_rightIconStyle, -1)
            mRightTwoIcon = getResourceId(R.styleable.MyToolbar_rightIconTwoStyle, -1)
            isHaveAdd = getBoolean(R.styleable.MyToolbar_has_right_icon, false)
            isHaveRight = getBoolean(R.styleable.MyToolbar_hasRightTitle, false)
            isHaveRightTwo = getBoolean(R.styleable.MyToolbar_has_right_two_icon, false)
            isHaveBack = getBoolean(R.styleable.MyToolbar_has_right_icon, true)
            recycle()
        }
        initView()
        initEvent()
    }

    private var mTitle: String = ""
    private var mRightTitle: String? = null
    private var mTitleColor: Int = Color.BLACK
    private var mBarBgColor: Int = Color.WHITE
    private var mLeftIcon: Int? = null
    private var mRightIcon: Int? = null
    private var mRightTwoIcon: Int? = null
    private var isHaveAdd: Boolean? = null
    private var isHaveBack: Boolean? = null
    private var isHaveRightTwo: Boolean? = null
    private var isHaveRight: Boolean? = null
    private var mRightTitleColor: Int? = null



    private fun initView() {
        binding.apply {
        mTitle.let {
            tvToolbarTitle.text=it

        }

            tvToolbarTitle.setTextColor(mTitleColor)

        rlBar.setBackgroundColor(mBarBgColor)

        mLeftIcon?.let {
            if (it != -1) {
                ivBarBack.setImageResource(it)
            }
        }

        mRightIcon?.let {
            if (it != -1) {
                ivBarAdd.setImageResource(it)
            }
        }

        mRightTwoIcon?.let {
            if (it != -1) {
                ivBarRight.setImageResource(it)
            }
        }



        if (isHaveAdd!!) {
            ivBarAdd.visibility = View.VISIBLE
        } else {
            ivBarAdd.visibility = View.GONE
        }

        if (isHaveRightTwo!!) {
            ivBarRight.visibility = View.VISIBLE
        } else {
            ivBarRight.visibility = View.GONE
        }


        if (isHaveBack!!) {
            ivBarBack.visibility = View.VISIBLE
        } else {
            ivBarBack.visibility = View.GONE
        }

        mRightTitle?.let {
            tvBarRight.text = it
        }
        mRightTitleColor?.let {
            tvBarRight.setTextColor(it)
        }

        if (isHaveRight!!) {
            tvBarRight.visibility = View.VISIBLE
        } else {
            tvBarRight.visibility = View.GONE
        }
        }

    }

    private fun initEvent() {
        binding.apply {
            ivBarBack.setOnClickListener {
                mOnBackClickListener?.onBack()
            }

            ivBarAdd.setOnClickListener {
                mOnBackClickListener?.onRightTo()
            }

            tvBarRight.setOnClickListener {
                mOnBackClickListener?.onRightTo()
            }

            ivBarRight.setOnClickListener {
                mOnBackClickListener?.onRightTwoTo()
            }
        }

    }

    private var mOnBackClickListener: OnBackClickListener? = null
    fun setOnBackClickListener(listener: OnBackClickListener?) {
        this.mOnBackClickListener = listener
    }

    fun setTitle(title: String) {
        mTitle = title
        initView()
    }


    interface OnBackClickListener {
        fun onBack()

        fun onRightTo()

        fun onRightTwoTo()
    }


}


