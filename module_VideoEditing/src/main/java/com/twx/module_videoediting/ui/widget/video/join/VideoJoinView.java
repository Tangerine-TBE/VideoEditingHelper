package com.twx.module_videoediting.ui.widget.video.join;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.ugckit.component.slider.RangeSlider;
import com.tencent.qcloud.ugckit.module.effect.utils.Edit;
import com.tencent.qcloud.ugckit.utils.DateTimeUtil;
import com.tencent.rtmp.TXLog;
import com.tencent.ugc.TXVideoEditConstants;
import com.twx.module_base.utils.SPUtil;
import com.twx.module_videoediting.R;
import com.twx.module_videoediting.ui.adapter.recycleview.video.cut.CutAdapter;
import com.twx.module_videoediting.utils.Constants;

import java.util.List;

/**
 * @author wujinming QQ:1245074510
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.cut
 * @class describe
 * @time 2021/5/6 11:14:08
 * @class describe
 */
public class VideoJoinView extends RelativeLayout implements RangeSlider.OnRangeChangeListener {

    @NonNull
    private String TAG = "VideoCutView";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RangeSlider mRangeSlider;
    private TextView mCutTime;
    private ImageView mDelete;

    /**
     * 控件最大时长16s
     */
    private long mViewMaxDuration;
    /**
     * 最终视频的起始时间
     */
    private long mVideoStartPos;
    /**
     * 最终视频的结束时间
     */
    private long mVideoEndPos;

    private CutAdapter mAdapter;

    private Edit.OnCutChangeListener mRangeChangeListener;

    public VideoJoinView(Context context) {
        super(context);

        init(context);
    }

    public VideoJoinView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public VideoJoinView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        boolean themeState = SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE);
        mContext = context;
        inflate(getContext(), R.layout.item_join_view, this);

        mRangeSlider = findViewById(R.id.range_slider);
        mCutTime = findViewById(R.id.cutTimes);
        mDelete = findViewById(R.id.mDeleteImage);

        mCutTime.setTextColor(themeState? Color.BLACK:Color.WHITE);
        mDelete.setColorFilter(themeState? Color.BLACK:Color.WHITE);

        mRangeSlider.setRangeChangeListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(manager);


        mAdapter = new CutAdapter();
        mRecyclerView.setAdapter(mAdapter);


        mDelete.setOnClickListener(v -> {
            if (mRangeChangeListener != null) {
                mRangeChangeListener.onDeleteItem();
            }
        });
    }


    /**
     * 设置裁剪Listener
     *
     * @param listener
     */
    public void setCutChangeListener(Edit.OnCutChangeListener listener) {
        mRangeChangeListener = listener;
    }

    public void setMediaFileInfo(@Nullable TXVideoEditConstants.TXVideoInfo videoInfo) {
        if (videoInfo == null) {
            return;
        }
        mViewMaxDuration =videoInfo.duration;
        mVideoStartPos = 0;
        mVideoEndPos = mViewMaxDuration;

        setCutTime(mVideoStartPos,mViewMaxDuration);

    }

    private void setCutTime(long startTime,long endTime){
        mCutTime.setText(DateTimeUtil.duration(startTime)+"/"+DateTimeUtil.duration(endTime));
    }


    public void addBitmapList(Bitmap bitmap,int index){
        mAdapter.setBitmap(bitmap,index);
    }


    public void addBitmapList(List<Bitmap> list) {
        mAdapter.setThumbnailList(list);
    }

    @Override
    public void onClick() {
        if (mRangeChangeListener != null) {
            mRangeChangeListener.onCutClick();
        }
    }

    @Override
    public void onKeyDown(int type) {
        if (mRangeChangeListener != null) {
            mRangeChangeListener.onCutChangeKeyDown();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdapter != null) {
            TXLog.i(TAG, "onDetachedFromWindow");
            mAdapter.clearAllBitmap();
        }
    }

    @Override
    public void onKeyUp(int type, int leftPinIndex, int rightPinIndex) {
        mVideoStartPos = (int) (mViewMaxDuration * leftPinIndex / 100); //ms
        mVideoEndPos = (int) (mViewMaxDuration * rightPinIndex / 100);
        setCutTime(mVideoStartPos,mVideoEndPos);
        onTimeChanged(type);
    }

    private void onTimeChanged(int touchType) {
        if (mRangeChangeListener != null) {

            mRangeChangeListener.onCutChangeKeyUp((int) mVideoStartPos, (int) mVideoEndPos, touchType);
        }
    }

}