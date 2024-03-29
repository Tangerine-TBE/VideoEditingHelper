package com.twx.module_videoediting.ui.widget.video.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.twx.module_base.utils.LogUtils;
import com.twx.module_videoediting.R;


/**
 * Created by frank on 18/3/20.
 * <p>
 * 裁剪视频的View
 */

public class CropView extends View {
    float downX;
    float downY;
    float lastSlideX;
    float lastSlideY;
    boolean isLeft;
    boolean isRight;
    boolean isTop;
    boolean isBottom;
    boolean isMove;
    boolean isSlideLeft;
    boolean isSlideRight;
    boolean isSlideTop;
    boolean isSlideBottom;
    float rectLeft;
    float rectRight;
    float rectTop;
    float rectBottom;

    private int measuredWidth;
    private int measuredHeight;
    private Paint paint;
    private int dp3;
    private int cornerLength;
    private int dp1;

    private float aspect = -1;

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropView(Context context) {
        super(context);
        init();
    }

    private void init() {

        dp3 = (int) getResources().getDimension(R.dimen.dp3);
        dp1 = (int) getResources().getDimension(R.dimen.dp1);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                downX = event.getX();
                downY = event.getY();
                if (downX >= rectLeft && downX <= rectRight && downY >= rectTop && downY <= rectBottom) {
                    //判断手指的范围在左面还是右面
                    int w = (int) ((rectRight - rectLeft) / 3);
                    if (downX >= rectLeft && downX <= rectLeft + w) {
                        isLeft = true;
                    } else if (downX <= rectRight && downX >= rectRight - w) {
                        isRight = true;
                    }
                    //判断手指的范围在上面还是下面
                    int h = (int) ((rectBottom - rectTop) / 3);
                    if (downY >= rectTop && downY <= rectTop + h) {
                        isTop = true;
                    } else if (downY <= rectBottom && downY >= rectBottom - h) {
                        isBottom = true;
                    }
                    //如果手指范围没有在任何边界位置, 那么我们就认为用户是想拖拽框体
                    if (!isLeft && !isTop && !isRight && !isBottom) {
                        isMove = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //得到手指移动距离
                float slideX = moveX - downX + lastSlideX;
                float slideY = moveY - downY + lastSlideY;

                if (isMove) {//判断是否是拖拽模式
                    rectLeft += slideX;
                    rectRight += slideX;
                    rectTop += slideY;
                    rectBottom += slideY;
                    //同时改变left和right值, 达到左右移动的效果
                    if (rectLeft < 0 || rectRight > measuredWidth) {//判断x轴的移动边界
                        rectLeft -= slideX;
                        rectRight -= slideX;
                    }
                    //同时改变top和bottom值, 达到上下移动的效果
                    if (rectTop < 0 || rectBottom > measuredHeight) {//判断y轴的移动边界
                        rectTop -= slideY;
                        rectBottom -= slideY;
                    }
                    //实时触发onDraw()方法
                    invalidate();
                    downX = moveX;
                    downY = moveY;
                } else {
                    if (aspect != -1) {
                        if (isLeft && (isTop || isBottom)) {
                            if (!isSlideLeft && !isSlideTop && !isSlideBottom) {
                                float x = Math.abs(slideX);
                                float y = Math.abs(slideY);
                                if (x > y && x > 10) {
                                    isSlideLeft = true;
                                } else if (x < y && y > 10) {
                                    if (isTop) {
                                        isSlideTop = true;
                                    } else {
                                        isSlideBottom = true;
                                    }
                                }
                            }
                        } else if (isRight && (isTop || isBottom)) {
                            if (!isSlideRight && !isSlideTop && !isSlideBottom) {
                                float x = Math.abs(slideX);
                                float y = Math.abs(slideY);
                                if (x > y && x > 10) {
                                    isSlideRight = true;
                                } else if (x < y && y > 10) {
                                    if (isTop) {
                                        isSlideTop = true;
                                    } else {
                                        isSlideBottom = true;
                                    }
                                }
                            }
                        } else if (isLeft && !isSlideLeft) {
                            isSlideLeft = true;
                        } else if (isRight && !isSlideLeft) {
                            isSlideRight = true;
                        } else if (isTop && !isSlideTop) {
                            isSlideTop = true;
                        } else if (isBottom && !isSlideBottom) {
                            isSlideBottom = true;
                        }
                        if (isSlideLeft) {
                            rectLeft += slideX;
                            if (rectLeft < 0) rectLeft = 0;
                            float w = rectRight - rectLeft;
                            if (w < cornerLength * 2) {
                                w = cornerLength * 2;
                                rectLeft = rectRight - w;
                            }
                            float h = w / aspect;
                            if (h < cornerLength * 2) {
                                h = cornerLength * 2;
                                w = h * aspect;
                                rectLeft = rectRight - w;
                            }
                            if (isTop) {
                                rectBottom = rectTop + h;
                            } else if (isBottom) {
                                rectTop = rectBottom - h;
                            } else {
                                float rh = rectBottom - rectTop;
                                float t = (rh - h) / 2;
                                rectTop += t;
                                rectBottom -= t;
                            }
                            if (rectTop < 0) {
                                rectTop = 0;
                                rectBottom = h;
                                if (rectBottom > measuredHeight) {
                                    rectBottom = measuredHeight;
                                }
                                w = rectBottom * aspect;
                                rectLeft = rectRight - w;
                            } else if (rectBottom > measuredHeight) {
                                rectBottom = measuredHeight;
                                rectTop = measuredHeight - h;
                                if (rectTop < 0) {
                                    rectTop = 0;
                                }
                                w = (rectBottom - rectTop) * aspect;
                                rectLeft = rectRight - w;
                            }
                            invalidate();
                            downX = moveX;
                            downY = moveY;
                        } else if (isSlideRight) {
                            rectRight += slideX;
                            if (rectRight > measuredWidth)
                                rectRight = measuredWidth;
                            float w = rectRight - rectLeft;
                            if (w < cornerLength * 2) {
                                w = cornerLength * 2;
                                rectRight = rectLeft + w;
                            }
                            float h = w / aspect;
                            if (h < cornerLength * 2) {
                                h = cornerLength * 2;
                                w = h * aspect;
                                rectRight = rectLeft + w;
                            }

                            if (isTop) {
                                rectBottom = rectTop + h;
                            } else if (isBottom) {
                                rectTop = rectBottom - h;
                            } else {
                                float rh = rectBottom - rectTop;
                                float t = (rh - h) / 2;
                                rectTop += t;
                                rectBottom -= t;
                            }
                            if (rectTop < 0) {
                                rectTop = 0;
                                rectBottom = h;
                                if (rectBottom > measuredHeight) {
                                    rectBottom = measuredHeight;
                                }
                                w = rectBottom * aspect;
                                rectRight = rectLeft + w;
                            } else if (rectBottom > measuredHeight) {
                                rectBottom = measuredHeight;
                                rectTop = measuredHeight - h;
                                if (rectTop < 0) {
                                    rectTop = 0;
                                }
                                w = (rectBottom - rectTop) * aspect;
                                rectRight = rectLeft + w;
                            }
                            invalidate();
                            downX = moveX;
                            downY = moveY;
                        } else if (isSlideTop) {
                            rectTop += slideY;
                            if (rectTop < 0) rectTop = 0;
                            float h = rectBottom - rectTop;
                            if (h < cornerLength * 2) {
                                h = cornerLength * 2;
                                rectTop = rectBottom - h;
                            }
                            float w = h * aspect;
                            if (w < cornerLength * 2) {
                                w = cornerLength * 2;
                                h = w / aspect;
                                rectTop = rectBottom - h;
                            }

                            if (isLeft) {
                                rectRight = rectLeft + w;
                            } else if (isRight) {
                                rectLeft = rectRight - w;
                            } else {
                                float rw = rectRight - rectLeft;
                                float t = (rw - w) / 2;
                                rectLeft += t;
                                rectRight -= t;
                            }
                            if (rectLeft < 0) {
                                rectLeft = 0;
                                rectRight = w;
                                if (rectRight > measuredWidth) {
                                    rectRight = measuredWidth;
                                }
                                h = rectRight / aspect;
                                rectTop = rectBottom - h;
                            } else if (rectRight > measuredWidth) {
                                rectRight = measuredWidth;
                                rectLeft = measuredWidth - w;
                                if (rectLeft < 0) {
                                    rectLeft = 0;
                                    w = measuredWidth;
                                }
                                h = w / aspect;
                                rectTop = rectBottom - h;
                            }
                            invalidate();
                            downX = moveX;
                            downY = moveY;
                        } else if (isSlideBottom) {
                            rectBottom += slideY;
                            if (rectBottom > measuredHeight)
                                rectBottom = measuredHeight;
                            float h = rectBottom - rectTop;
                            if (h < cornerLength * 2) {
                                h = cornerLength * 2;
                                rectBottom = rectTop + h;
                            }
                            float w = h * aspect;
                            if (w < cornerLength * 2) {
                                w = cornerLength * 2;
                                h = w / aspect;
                                rectBottom = rectTop + h;
                            }

                            if (isLeft) {
                                rectRight = rectLeft + w;
                            } else if (isRight) {
                                rectLeft = rectRight - w;
                            } else {
                                float rw = rectRight - rectLeft;
                                float t = (rw - w) / 2;
                                rectLeft += t;
                                rectRight -= t;
                            }
                            if (rectLeft < 0) {
                                rectLeft = 0;
                                rectRight = w;
                                if (rectRight > measuredWidth) {
                                    rectRight = measuredWidth;
                                }
                                h = rectRight / aspect;
                                rectBottom = rectTop + h;
                            } else if (rectRight > measuredWidth) {
                                rectRight = measuredWidth;
                                rectLeft = measuredWidth - w;
                                if (rectLeft < 0) {
                                    rectLeft = 0;
                                    w = measuredWidth;
                                }
                                h = w / aspect;
                                rectBottom = rectTop + h;
                            }
                            invalidate();
                            downX = moveX;
                            downY = moveY;
                        }
                    } else {
                        if (mode == CropMode.MODE_INIT) {
                            if (isLeft) {
                                rectLeft += slideX;
                                if (rectLeft < 0) rectLeft = 0;
                                if (rectLeft > rectRight - cornerWidth)
                                    rectLeft = rectRight - cornerWidth;
                            } else if (isRight) {
                                rectRight += slideX;
                                if (rectRight > measuredWidth)
                                    rectRight = measuredWidth;
                                if (rectRight < rectLeft + cornerWidth)
                                    rectRight = rectLeft + cornerWidth;
                            }
                            //改变边框的高度, 如果两个都满足(比如手指在边角位置),那么就呈现一种缩放状态
                            if (isTop) {
                                rectTop += slideY;
                                if (rectTop < 0) rectTop = 0;
                                if (rectTop > rectBottom - cornerWidth)
                                    rectTop = rectBottom - cornerWidth;
                            } else if (isBottom) {
                                rectBottom += slideY;
                                if (rectBottom > measuredHeight)
                                    rectBottom = measuredHeight;
                                if (rectBottom < rectTop + cornerWidth)
                                    rectBottom = rectTop + cornerWidth;
                            }

                        } else {
                            boolean isMotionLeft = downX - moveX > 0;
                            boolean isVertical = measuredHeight > measuredWidth;
                            if (isLeft) {
                                rectLeft += slideX;
                                switch (mode) {
                                    //1:1模式------------------------------------
                                    case MODE_11:
                                        if (isMotionLeft) {
                                            rectBottom += Math.abs(slideX);
                                            //超出底部高
                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectLeft = rectRight - (rectBottom - rectTop);
                                            } else {
                                                //超出左边边界
                                                if (rectLeft < 0) {
                                                    rectLeft = 0;
                                                    rectBottom = rectRight + rectTop;
                                                }
                                            }

                                        } else {
                                            rectBottom -= Math.abs(slideX);
                                            //大于边框长度
                                            if (rectLeft > rectRight - cornerWidth) {
                                                rectLeft = rectRight - cornerWidth;
                                                rectBottom = (rectRight - rectLeft) + rectTop;
                                            }

                                        }
                                        break;
                                    //16:9模式------------------------------------
                                    case MODE_169:
                                        if (isMotionLeft) {
                                            rectBottom += Math.abs(slideX / 16 * 9);
                                            //超出底部高
                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectLeft = rectRight - ((rectBottom - rectTop) / 9 * 16);
                                            } else {
                                                //超出左边边界
                                                if (rectLeft < 0) {
                                                    rectLeft = 0;
                                                    rectBottom = (rectRight / 16) * 9 + rectTop;
                                                }
                                            }

                                        } else {
                                            rectBottom -= Math.abs(slideX / 16 * 9);
                                            int min = (measuredWidth / 16) * 6;
                                            if (rectLeft > rectRight - min) {
                                                rectLeft = rectRight - min;
                                                rectBottom = ((min / 16) * 9) + rectTop;
                                            }

                                        }
                                        break;
                                    //9:16模式------------------------------------
                                    case MODE_916:
                                        if (isMotionLeft) {
                                            rectBottom += Math.abs((slideX / 9) * 16);
                                            //超出底部高
                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectLeft = rectRight - (((rectBottom - rectTop) / 16) * 9);
                                            } else {
                                                //超出左边边界
                                                if (rectLeft < 0) {
                                                    rectLeft = 0;
                                                    rectBottom = (rectRight / 9) * 16 + rectTop;
                                                }
                                            }

                                        } else {
                                            rectBottom -= Math.abs((slideX / 9) * 16);
                                            int min = (measuredHeight / 16) * 5;
                                            if (rectLeft > rectRight - min) {
                                                rectLeft = rectRight - min;
                                                rectBottom = ((min / 9) * 16) + rectTop;
                                            }

                                        }
                                        break;
                                    //4:3模式------------------------------------
                                    case MODE_43:
                                        if (isMotionLeft) {
                                            rectBottom += Math.abs(slideX / 4 * 3);
                                            //超出底部高
                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectLeft = rectRight - ((rectBottom - rectTop) / 3 * 4);
                                            } else {
                                                //超出左边边界
                                                if (rectLeft < 0) {
                                                    rectLeft = 0;
                                                    rectBottom = (rectRight / 4) * 3 + rectTop;
                                                }
                                            }

                                        } else {
                                            rectBottom -= Math.abs(slideX / 4 * 3);
                                            int min = (measuredWidth / 4) * 1;
                                            if (rectLeft > rectRight - min) {
                                                rectLeft = rectRight - min;
                                                rectBottom = ((min / 4) * 3) + rectTop;
                                            }

                                        }
                                        break;
                                }
                            } else if (isRight) {
                                rectRight += slideX;
                                switch (mode) {
                                    case MODE_11:
                                        if (isMotionLeft) {
                                            LogUtils.i("isRight----方向左--------------------------   " + rectLeft);
                                            rectBottom -= Math.abs(slideX);
                                            if (rectRight < rectLeft + cornerWidth) {
                                                rectRight = rectLeft + cornerWidth;
                                                rectBottom = (rectRight - rectLeft) + rectTop;
                                            }

                                        } else {
                                            LogUtils.i("isRight----方向右--------------------------   " + rectLeft);
                                            rectBottom += Math.abs(slideX);
                                            //大于边界
                                            if (rectRight > measuredWidth) {
                                                rectRight = measuredWidth;
                                                rectBottom = rectTop + (rectRight - rectLeft);
                                            } else {
                                                //大于底部高
                                                if (rectBottom > measuredHeight) {
                                                    rectBottom = measuredHeight;
                                                    rectRight = (rectBottom - rectTop) + rectLeft;
                                                }
                                            }
                                        }
                                        break;
                                    //16:9模式------------------------------------
                                    case MODE_169:
                                        if (isMotionLeft) {
                                            LogUtils.i("isRight-169---方向左--------------------------   " + rectLeft);
                                            rectBottom -= Math.abs(slideX / 16 * 9);
                                            int min = (measuredWidth / 16) * 6;
                                            if (rectRight <= rectLeft + min) {
                                                rectRight = rectLeft + min;
                                                rectBottom = ((min / 16) * 9) + rectTop;
                                            }

                                        } else {
                                            LogUtils.i("isRight-169---方向右--------------------------   " + rectLeft);
                                            rectBottom += Math.abs(slideX / 16 * 9);

                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectRight = rectLeft + (((rectBottom - rectTop) / 9) * 16);
                                            } else {
                                                if (rectRight > measuredWidth) {
                                                    rectRight = measuredWidth;
                                                    rectBottom = ((rectRight - rectLeft) / 16) * 9 + rectTop;
                                                }
                                            }
                                        }
                                        break;
                                    //9:16模式------------------------------------
                                    case MODE_916:
                                        if (isMotionLeft) {
                                            LogUtils.i("isRight-169---方向左--------------------------   " + rectLeft);
                                            rectBottom -= Math.abs((slideX / 9) * 16);
                                            int min = (measuredHeight / 16) * 5;
                                            if (rectRight <= rectLeft + min) {
                                                rectRight = rectLeft + min;
                                                rectBottom = ((min / 9) * 16) + rectTop;
                                            }

                                        } else {
                                            LogUtils.i("isRight-169---方向右--------------------------   " + rectLeft);
                                            rectBottom += Math.abs((slideX / 9) * 16);
                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectRight = rectLeft + (((rectBottom - rectTop) / 16) * 9);
                                            } else {
                                                if (rectRight > measuredWidth) {
                                                    rectRight = measuredWidth;
                                                    rectBottom = ((rectRight - rectLeft) / 9) * 16 + rectTop;
                                                }
                                            }
                                        }
                                        break;
                                    //4:3模式------------------------------------
                                    case MODE_43:
                                        if (isMotionLeft) {
                                            LogUtils.i("isRight-169---方向左--------------------------   " + rectLeft);
                                            rectBottom -= Math.abs(slideX / 4 * 3);
                                            int min = (measuredWidth / 4) * 1;
                                            if (rectRight <= rectLeft + min) {
                                                rectRight = rectLeft + min;
                                                rectBottom = ((min / 4) * 3) + rectTop;
                                            }

                                        } else {
                                            LogUtils.i("isRight-169---方向右--------------------------   " + rectLeft);
                                            rectBottom += Math.abs(slideX / 4 * 3);

                                            if (rectBottom >= measuredHeight) {
                                                rectBottom = measuredHeight;
                                                rectRight = rectLeft + (((rectBottom - rectTop) / 3) * 4);
                                            } else {
                                                if (rectRight > measuredWidth) {
                                                    rectRight = measuredWidth;
                                                    rectBottom = ((rectRight - rectLeft) / 4) * 3 + rectTop;
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                        }

                        invalidate();
                        downX = moveX;
                        downY = moveY;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isLeft = false;
                isRight = false;
                isTop = false;
                isBottom = false;
                isMove = false;
                isSlideLeft = false;
                isSlideRight = false;
                isSlideTop = false;
                isSlideBottom = false;
                break;
        }
        return true;
    }

    /**
     * 得到裁剪区域的margin值
     */
    public float[] getCutArr() {

        float[] arr = new float[4];
        arr[0] = rectLeft;
        arr[1] = rectTop;
        arr[2] = rectRight;
        arr[3] = rectBottom;
        return arr;
    }

    public RectF getRectValue() {
        return new RectF(rectLeft, rectTop, rectRight, rectBottom);
    }


    public int getRectWidth() {
        return (int) (measuredWidth);
    }

    public int getRectHeight() {
        return (int) (measuredHeight);
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (measuredWidth == 0) {
            initParams();
        }
    }

    private int cornerWidth;

    private void initParams() {

        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        if (aspect == -1) {
            cornerLength = measuredWidth / 6;
            cornerWidth = cornerLength * 2;
            if (isSpecial) {

            } else {
                rectRight = measuredWidth;
                rectLeft = 0;
                rectTop = 0;
                rectBottom = measuredHeight;
            }

            LogUtils.i("---mCropView------------------------" + measuredWidth + "---------" + measuredHeight);
        } else {
            float vh = measuredWidth * 1.0f / measuredHeight;
            if (aspect > 1) {
                cornerLength = measuredWidth / 6;
            } else {
                cornerLength = measuredHeight / 6;
            }
            if (aspect > vh) {
                rectLeft = 0;
                rectRight = measuredWidth;
                float h = measuredWidth / aspect;
                rectTop = (measuredHeight - h) / 2;
                rectBottom = rectTop + h;
            } else {
                rectTop = 0;
                rectBottom = measuredHeight;
                float w = measuredHeight * aspect;
                rectLeft = (measuredWidth - w) / 2;
                rectRight = rectLeft + w;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setStrokeWidth(dp1);
        //绘制裁剪区域的矩形, 传入margin值来确定大小
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
        //绘制四条分割线和四个角
        drawLine(canvas, rectLeft, rectTop, rectRight, rectBottom);
    }


    private boolean isSpecial;


    public enum CropMode {
        MODE_INIT, MODE_11, MODE_169, MODE_916, MODE_43
    }

    private CropMode mode = CropMode.MODE_INIT;


    private int maxWidth;
    private int maxHeight;


    public void setRectValue(CropView.CropMode mode, boolean specialState, int left, int top, int right, int bottom) {
        this.mode = mode;
        isSpecial = specialState;
        rectLeft = left;
        rectTop = top;
        rectRight = right;
        rectBottom = bottom;
        maxWidth = right - left;
        maxHeight = bottom - top;
        invalidate();
    }


    /**
     * 绘制四条分割线和四个角
     */
    private void drawLine(Canvas canvas, float left, float top, float right, float bottom) {

        paint.setStrokeWidth(1);
        //绘制四条分割线
        float startX = (right - left) / 3 + left;
        float startY = top;
        float stopX = (right - left) / 3 + left;
        float stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = (right - left) / 3 * 2 + left;
        startY = top;
        stopX = (right - left) / 3 * 2 + left;
        stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = left;
        startY = (bottom - top) / 3 + top;
        stopX = right;
        stopY = (bottom - top) / 3 + top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = left;
        startY = (bottom - top) / 3 * 2 + top;
        stopX = right;
        stopY = (bottom - top) / 3 * 2 + top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        paint.setStrokeWidth(dp3);
        //绘制四个角
        startX = left - dp3 / 2;
        startY = top;
        stopX = left + cornerLength;
        stopY = top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = left;
        startY = top;
        stopX = left;
        stopY = top + cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = right + dp3 / 2;
        startY = top;
        stopX = right - cornerLength;
        stopY = top;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = right;
        startY = top;
        stopX = right;
        stopY = top + cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = left;
        startY = bottom;
        stopX = left;
        stopY = bottom - cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = left - dp3 / 2;
        startY = bottom;
        stopX = left + cornerLength;
        stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        startX = right + dp3 / 2;
        startY = bottom;
        stopX = right - cornerLength;
        stopY = bottom;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        startX = right;
        startY = bottom;
        stopX = right;
        stopY = bottom - cornerLength;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}