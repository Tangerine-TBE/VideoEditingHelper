package com.twx.module_videoediting.ui.widget.video.cut;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.qcloud.ugckit.component.slider.RangeSlider;
import com.tencent.qcloud.ugckit.module.effect.time.TCVideoEditerAdapter;
import com.tencent.qcloud.ugckit.module.effect.utils.Edit;
import com.tencent.rtmp.TXLog;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.twx.module_base.base.BaseApplication;
import com.twx.module_base.utils.LogUtils;
import com.twx.module_videoediting.R;
import com.twx.module_videoediting.ui.adapter.recycleview.video.cut.CutAdapter;

import java.util.List;

/**
 * @author wujinming QQ:1245074510
 * @name VideoEditingHelper
 * @class name：com.twx.module_videoediting.ui.widget.video.cut
 * @class describe
 * @time 2021/5/6 11:14:08
 * @class describe
 */
public class VideoCutView extends RelativeLayout implements com.tencent.qcloud.ugckit.component.slider.RangeSlider.OnRangeChangeListener {

    @NonNull
    private String TAG = "VideoCutView";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RangeSlider mRangeSlider;

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

    public VideoCutView(Context context) {
        super(context);

        init(context);
    }

    public VideoCutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public VideoCutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        inflate(getContext(), R.layout.item_edit_view, this);

        mRangeSlider = (RangeSlider) findViewById(R.id.range_slider);
        mRangeSlider.setRangeChangeListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(manager);


        mAdapter = new CutAdapter();
        mRecyclerView.setAdapter(mAdapter);

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
    }

    public void addBitmapList(Bitmap bitmap,int index){
        mAdapter.setBitmap(bitmap,index);
    }


    public void addBitmapList(List<Bitmap> list) {
        mAdapter.setThumbnailList(list);
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

        onTimeChanged(type);
    }

    private void onTimeChanged(int touchType) {
        if (mRangeChangeListener != null) {
            mRangeChangeListener.onCutChangeKeyUp((int) mVideoStartPos, (int) mVideoEndPos, touchType);
        }
    }


    public void  setVideoEditor(TXVideoEditer videoEditor){
                videoEditor.getThumbnail(6, 100, 100, true, (index, times, bitmap) -> {
                    LogUtils.i(times+"setVideoEditor--------------------"+index);
                    BaseApplication.mHandler.post(() -> {
                        mAdapter.setBitmap(bitmap,index);
                    });
                });
    }
}