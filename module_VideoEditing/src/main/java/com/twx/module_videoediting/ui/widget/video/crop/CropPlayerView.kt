package com.twx.module_videoediting.ui.widget.video.crop

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.twx.module_base.utils.LogUtils
import com.twx.module_videoediting.R

/**
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.crop
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/4/23 14:30:03
 * @class describe
 */
class CropPlayerView : StandardGSYVideoPlayer {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}



    override fun getLayoutId(): Int = R.layout.layout_crop_player_view
    override fun updateStartImage() {
        mStartButton.visibility = View.VISIBLE
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.mipmap.icon_player_stop)
                }
                CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.mipmap.icon_player_start)
                }
                else -> {
                    imageView.setImageResource(R.mipmap.icon_player_start)
                }
            }
        }
    }

    companion object{
        const val TRANSFORM_NORMAL=0
        const val TRANSFORM_VERTICAL=2
        const val TRANSFORM_HORIZONTAL=1
    }


    private var mTransformSize =TRANSFORM_NORMAL
    fun getResolveTransform() = mTransformSize
    fun setResolveTransform(isVertical: Boolean, transformSize: Int) {
        when (transformSize) {
            TRANSFORM_NORMAL -> {
                mTransformSize = if (isVertical) {
                    TRANSFORM_VERTICAL
                } else {
                    TRANSFORM_HORIZONTAL
                }
            }
            TRANSFORM_HORIZONTAL -> mTransformSize = TRANSFORM_NORMAL
            TRANSFORM_VERTICAL -> mTransformSize = TRANSFORM_NORMAL
        }
        resolveTransform()
    }





    fun getTextureView(block:(View)->Unit){
        mTextureView?.let {
            block(it.showView)
        }
    }


    /**
     * 处理镜像旋转
     * 注意，暂停时
     */
    private fun resolveTransform() {
        mTextureView?.let {
            when (mTransformSize) {
                1 -> {
                    val transform = Matrix()
                    transform.setScale(-1f, 1f, (it.width / 2).toFloat(), 0f)
                    it.setTransform(transform)
                    it.invalidate()
                }
                2 -> {
                    val transform = Matrix()
                    transform.setScale(1f, -1f, 0f, (it.height / 2).toFloat())
                    it.setTransform(transform)
                    it.invalidate()
                }
                0 -> {
                    val transform = Matrix()
                    transform.setScale(1f, 1f, (mTextureView.width / 2).toFloat(), 0f)
                    it.setTransform(transform)
                    it.invalidate()
                }
            }
        }

    }

    private var isRotate=false
    private var mRotateValue=mTextureView?.rotation?:0f
    fun getRotationValue()=mRotateValue
    fun getRotateState()=isRotate

    fun setRotation() {
        mTextureView?.let {
            isRotate=true
            LogUtils.i("----setRotation---begin--${mTextureView.rotation}--------------")
            if (mTextureView.rotation - mRotate == 270f) {
                mTextureView.rotation = mRotate.toFloat()
                mTextureView.requestLayout()
            } else {
                mTextureView.rotation = mTextureView.rotation + 90
                mTextureView.requestLayout()
            }
            mRotateValue= mTextureView.rotation
            LogUtils.i("----setRotation--end---${mTextureView.rotation}--------------")
        }

    }


    fun restoreVideoUi() {
        mTransformSize=0
        mRotateValue=0f
        mTextureView?.rotation = 0f
        resolveTransform()
        mTextureView?.requestLayout()
    }

}