package com.twx.module_videoediting.ui.widget.video.speed

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.utils.SizeUtils
import com.twx.module_videoediting.R

class SpeedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mScalePaint = Paint()
    private val mCirclePaint = Paint()
    private val mTextPaint = Paint()

    private var mWidth = 0
    private var mHeight = 0
    private var mScaleHeight = 0f
    private var interval = 0f
    private val minValue = 50f
    private var maxValue = 0f
    private var radius = minValue / 3 * 2
    private var moveX = minValue
    private var initValue=0f


    init {
        mScalePaint.apply {
            color = ContextCompat.getColor(context, R.color.theme_color)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }


        mCirclePaint.apply {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = 5f
            isAntiAlias = true
        }


        mTextPaint.apply {
            color =if (SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE)) Color.BLACK else Color.WHITE
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
            textSize = SizeUtils.sp2px(context,14f).toFloat()
            isAntiAlias = true
        }

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        interval = mWidth / 8f
        mScaleHeight = mHeight / 3 * 2f
        maxValue=interval*7+minValue

        initValue=interval+minValue


        moveX=initValue

        setMeasuredDimension(mWidth, mHeight)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawScale(canvas)
        drawCircle(canvas, moveX)
    }


    private fun drawScale(canvas: Canvas) {
        canvas.drawLine(
            minValue,
            mScaleHeight / 2,
            maxValue,
            mScaleHeight / 2,
            mScalePaint
        )
        for (i in 0 until 8) {
            canvas.drawLine(
                i * interval + minValue,
                0f,
                i * interval + minValue,
                mScaleHeight,
                mScalePaint
            )

            canvas.drawText("${(i+1)*0.5f}", i * interval + minValue, mHeight.toFloat(), mTextPaint)
        }
    }


    private fun drawCircle(canvas: Canvas, moveX: Float) {
        canvas.drawCircle(moveX, mScaleHeight / 2, radius, mCirclePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                calculateMoveX(event.x)
                mOnSpeedListener?.selectSpeed(calculateSpeed())
                Log.d("SpeedView","---calculateSpeed-------$moveX----------")
            }
        }

        return true
    }

    private fun calculateMoveX(x: Float) {
        moveX = when {
            x < radius -> {
                minValue
            }
            x > maxValue -> {
                maxValue
            }
            else -> {
                x
            }
        }
        invalidate()

    }


    private  fun calculateSpeed() = when {
        moveX <= minValue -> {
            0.5f
        }
        moveX >= maxValue -> {
            4.0f
        }
        else -> {
            ((moveX- minValue)/interval)*0.5f+0.5f
        }
    }




    private var mOnSpeedListener:OnSpeedListener?=null

    fun setOnSpeedListener(listener:OnSpeedListener){
        mOnSpeedListener=listener
    }


    interface OnSpeedListener{

        fun selectSpeed(speed:Float)
    }

}