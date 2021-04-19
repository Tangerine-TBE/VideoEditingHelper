package com.twx.module_base.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.graphics.withTranslation
import com.twx.module_base.base.BaseView
import com.twx.module_base.utils.SizeUtils


/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/6 16:57:00
 * @class describe
 */
class LoadingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr) {
    private val mBgPaint = Paint()
    private val mFgPaint = Paint()
    private val mTextPaint = Paint()
    private val mPaintWidth = 8f

    init {

        mTextPaint.apply {
            color = Color.BLACK
            textSize = SizeUtils.sp2px(context, 13f).toFloat()
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        mBgPaint.apply {
            color = Color.parseColor("#F1F5FC")
            strokeWidth = mPaintWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        mFgPaint.apply {
            color = Color.parseColor("#4060FF")
            strokeWidth = mPaintWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }

    }

    private var mWidth = 0
    private var mHeight = 0
    private var mRadius = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mRadius = mWidth / 2f - mPaintWidth / 2
        setMeasuredDimension(mWidth, mWidth)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.withTranslation(mWidth / 2f, mWidth / 2f) {
            drawCircle(0f, 0f, mRadius, mBgPaint)

            mFgPaint.color = Color.parseColor("#FF9C50")
            drawArc(-mRadius, -mRadius, mRadius, mRadius, mCurrentAngle+45f, 180f, false, mFgPaint)
            mFgPaint.color = Color.parseColor("#99FF40")
            drawArc(-mRadius, -mRadius, mRadius, mRadius, mCurrentAngle+55f, 180f, false, mFgPaint)
            mFgPaint.color = Color.parseColor("#40FFFF")
            drawArc(-mRadius, -mRadius, mRadius, mRadius, mCurrentAngle+70f, 180f, false, mFgPaint)
            mFgPaint.color = Color.parseColor("#4060FF")
            drawArc(-mRadius, -mRadius, mRadius, mRadius, mCurrentAngle+90f, 180f, false, mFgPaint)

            val fontMetrics: Paint.FontMetrics = Paint.FontMetrics()
            mTextPaint.getFontMetrics(fontMetrics)
            val offset = (fontMetrics.descent + fontMetrics.ascent) / 2
            canvas.drawText("$mProgress%", 0f, 0f - offset, mTextPaint)


        }
    }


    private var mCurrentAngle=0f
    private var mValueAnimator:ValueAnimator?=null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            interpolator = LinearInterpolator()
            repeatCount=ValueAnimator.INFINITE
            addUpdateListener {
                mCurrentAngle = it.animatedValue as Float
                duration = 500
                invalidate()
            }
        }
        mValueAnimator?.start()
    }


    private var mProgress = 0
    fun setProgress(progress: Int) {
        mProgress = if (progress >= 100) {
            99
        } else {
            progress
        }

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mValueAnimator?.cancel()
    }

}