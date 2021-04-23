package com.twx.module_videoediting.ui.widget.video.crop

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
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
                    imageView.setImageResource(R.drawable.icon_exo_stop)
                }
                CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.icon_exo_start)
                }
                else -> {
                    imageView.setImageResource(R.drawable.icon_exo_start)
                }
            }
        }
    }

    private var mTransformSize = 0
    fun getResolveTransform() = mTransformSize
    fun setResolveTransform(isVertical: Boolean, transformSize: Int) {
        when (transformSize) {
            0 -> {
                mTransformSize = if (isVertical) {
                    2
                } else {
                    1
                }
            }
            1 -> mTransformSize = 0
            2 -> mTransformSize = 0
        }
        resolveTransform()
    }


    fun restoreVideoUi() {
        mTransformSize=0
        resolveTransform()
        mTextureView.rotation = 0f
        mTextureView.requestLayout()
    }



    enum class FlipState{
        NORMAL,VERTICAL,HORIZONTAL
    }



    /**
     * 处理镜像旋转
     * 注意，暂停时
     */
    private fun resolveTransform() {
        when (mTransformSize) {
            1 -> {
                val transform = Matrix()
                transform.setScale(-1f, 1f, (mTextureView.width / 2).toFloat(), 0f)
                mTextureView.setTransform(transform)



                mTextureView.invalidate()
            }
            2 -> {
                val transform = Matrix()
                transform.setScale(1f, -1f, 0f, (mTextureView.height / 2).toFloat())
                mTextureView.setTransform(transform)
                mTextureView.invalidate()
            }
            0 -> {
                val transform = Matrix()
                transform.setScale(1f, 1f, (mTextureView.width / 2).toFloat(), 0f)
                mTextureView.setTransform(transform)
                mTextureView.invalidate()
            }
        }
    }


    fun getRotationValue()=mTextureView.rotation

    fun setRotation() {
        if (mTextureView.rotation - mRotate == 270f) {
            mTextureView.rotation = mRotate.toFloat()
            mTextureView.requestLayout()
        } else {
            mTextureView.rotation = mTextureView.rotation + 90
            mTextureView.requestLayout()
        }
    }

    /**
     * 旋转逻辑
     */
    private fun resolveRotateUI() {
        mTextureView.rotation = mRotate.toFloat()
        mTextureView.requestLayout()
    }


}