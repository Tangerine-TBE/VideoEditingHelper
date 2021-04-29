package com.twx.module_videoediting.ui.widget.video.division

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.division
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/21 17:30:43
 * @class describe
 */
class DivisionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val thumbnailList:MutableList<VideoEditerSDK.ThumbnailBitmapInfo> = ArrayList()
    private val mBitmapPaint= Paint()
    private val mShearBitmap= BitmapFactory.decodeResource(context.resources, R.mipmap.icon_division_shear)
    private var mWidth=0
    private var mHeight=0
    private var bitmapWidth = 0f
    private val srcThumbnail= Rect()
    private val dstThumbnail=Rect()
    private val dstShear=Rect()
    private val srcShear=Rect()
    private var rate=0f
    private var moveX=0f
    private var mShearWidth=0
    private var mShearHeight=0

    private var paddingDis=80



    init {
        mBitmapPaint.apply {
            isFilterBitmap=true
        }
        mShearWidth=mShearBitmap.width/7

        mShearHeight=mShearBitmap.height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth= MeasureSpec.getSize(widthMeasureSpec)
        mHeight= MeasureSpec.getSize(heightMeasureSpec)

        bitmapWidth=(mWidth-paddingDis)/4f


        setMeasuredDimension(mWidth,mHeight)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawThumbnail(canvas)
        drawShear(canvas)
    }

    private fun drawThumbnail(canvas: Canvas) {
       if (thumbnailList.size>0){
           for (i in 0 until thumbnailList.size){
              thumbnailList[i].bitmap?.let {
                  srcThumbnail.apply {
                      left=0
                      right=it.width
                      top=0
                      bottom=it.height
                  }

                  dstThumbnail.apply {
                      top=0
                      left= (bitmapWidth* i).toInt()+paddingDis/2
                      right= (bitmapWidth*(1+i)).toInt()+paddingDis/2
                      bottom= mHeight.toFloat().toInt()
                  }
                  canvas.drawBitmap(it,srcThumbnail,dstThumbnail,mBitmapPaint)
              }
           }
       }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        moveX = event.x
        LogUtils.i("---- event.x--------------${ event.x}------------")
        when (event.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN-> {
                updatePosition()
                mOnDivisionTimeListener?.divisionTime(((moveX/(mWidth-paddingDis))* mTotalTime).toLong())

            }
            MotionEvent.ACTION_UP->{
                updatePosition()
                mOnDivisionTimeListener?.continuePlay()
            }
        }
        invalidate()
        return true
    }

    private fun updatePosition() {
        if (moveX < 0) {
            moveX = 0f
        } else if (moveX > mWidth - paddingDis) {
            moveX = mWidth - paddingDis.toFloat()
        }
    }

    private fun drawShear(canvas: Canvas) {
        mShearBitmap.let {
            srcShear.apply {
                left=0
                right=it.width
                top=0
                bottom=it.height
            }

            dstShear.apply {
                left=(moveX-mShearWidth+paddingDis/2).toInt()
                right=(moveX+mShearWidth+paddingDis/2).toInt()
                top=0
                bottom=mHeight
            }
            canvas.drawBitmap(it,srcShear,dstShear,mBitmapPaint)
        }
    }



    private var mTotalTime=0L

    fun setVideoInfo(totalTime:Long,list:MutableList<VideoEditerSDK.ThumbnailBitmapInfo>){
        LogUtils.i("--setVideoInfo--------$totalTime---------------")
        mTotalTime=totalTime
        thumbnailList.clear()
        thumbnailList.addAll(list)
        invalidate()
    }


    interface OnDivisionTimeListener{
        fun divisionTime(time:Long)

        fun continuePlay();
    }

    private var mOnDivisionTimeListener:OnDivisionTimeListener?=null

    fun setOnSelectTimeListener(listener:OnDivisionTimeListener){
        mOnDivisionTimeListener=listener
    }

}