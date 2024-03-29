package com.tencent.qcloud.ugckit.module.effect.bgm.view;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.tencent.qcloud.ugckit.utils.DateTimeUtil;
import com.tencent.qcloud.ugckit.R;
import com.tencent.qcloud.ugckit.component.slider.RangeSlider;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;

public class TCEditMusicPannel extends RelativeLayout implements IEditMusicPannel, SeekBar.OnSeekBarChangeListener, RangeSlider.OnRangeChangeListener, View.OnClickListener {
    private Context mContext;
    private SeekBar mMicVolumeSeekBar;
    private SeekBar mBGMVolumeSeekBar;
    private int mMicVolume = 100;
    private int mBGMVolume = 100;
    private MusicChangeListener mMusicChangeListener;
    private RangeSlider mRangeSlider;
    private long mBgmDuration;
    private TextView mTvStartTime;
    private TextView mTvMicVolume;
    private TextView mTvMusicName;
    private ImageView mIvReplace;
    private ImageView mIvDelete;

    public TCEditMusicPannel(Context context) {
        super(context);
        init(context);
    }

    public TCEditMusicPannel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TCEditMusicPannel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_edit_music, this);
        mMicVolumeSeekBar = (SeekBar) findViewById(R.id.seekbar_mic_volume);
        mBGMVolumeSeekBar = (SeekBar) findViewById(R.id.seekbar_bgm_volume);
        mMicVolumeSeekBar.setOnSeekBarChangeListener(this);
        mBGMVolumeSeekBar.setOnSeekBarChangeListener(this);

        mTvMicVolume = (TextView) findViewById(R.id.tv_mic_volume);

        mRangeSlider = (RangeSlider) findViewById(R.id.bgm_range_slider);
        mRangeSlider.setRangeChangeListener(this);

        mIvReplace = (ImageView) findViewById(R.id.btn_bgm_replace);
        mIvDelete = (ImageView) findViewById(R.id.btn_bgm_delete);

        mIvReplace.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);

        mTvStartTime = (TextView) findViewById(R.id.tv_bgm_start_time);
        mTvStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position), "00:00"));

        mTvMusicName = (TextView) findViewById(R.id.tx_music_name);
        mTvMusicName.setText("");
        mTvMusicName.setSelected(true);
    }

    @Override
    public void setMusicInfo(MusicInfo musicInfo) {
        if (!TextUtils.isEmpty(musicInfo.name)) {
            mTvMusicName.setText(musicInfo.name);
        }
        if (mMicVolumeSeekBar != null && musicInfo.videoVolume != -1) {
            mMicVolumeSeekBar.setProgress((int) (musicInfo.videoVolume * 100));
        }

        if (mBGMVolumeSeekBar != null && musicInfo.bgmVolume != -1) {
            mBGMVolumeSeekBar.setProgress((int) (musicInfo.bgmVolume * 100));
        }
        mBgmDuration = musicInfo.duration;
        setCutRange(musicInfo.startTime, musicInfo.endTime);
    }

    @Override
    public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seekbar_mic_volume) {
            mMicVolume = progress;
            if (mMusicChangeListener != null) {
                mMusicChangeListener.onMicVolumeChanged(mMicVolume / (float) 100);
            }
        } else if (seekBar.getId() == R.id.seekbar_bgm_volume) {
            mBGMVolume = progress;
            if (mMusicChangeListener != null) {
                mMusicChangeListener.onMusicVolumChanged(mBGMVolume / (float) 100);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void setOnMusicChangeListener(MusicChangeListener volumeChangeListener) {
        mMusicChangeListener = volumeChangeListener;
    }

    @Override
    public void onClick() {

    }

    /******** RangeSlider callback start *********/
    @Override
    public void onKeyDown(int type) {

    }

    @Override
    public void onKeyUp(int type, int leftPinIndex, int rightPinIndex) {
        long leftTime = mBgmDuration * leftPinIndex / 100; //ms
        long rightTime = mBgmDuration * rightPinIndex / 100;

        mTvStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position),
                DateTimeUtil.millsecondToMinuteSecond((int) leftTime)));

        if (mMusicChangeListener != null) {
            mMusicChangeListener.onMusicTimeChanged(leftTime, rightTime);
        }

        mTvStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position), DateTimeUtil.millsecondToMinuteSecond((int) leftTime)));
    }

    /******** RangeSlider callback end *********/
    @Override
    public void onClick(@NonNull View v) {
        int i = v.getId();
        if (i == R.id.btn_bgm_replace) {
            if (mMusicChangeListener != null) {
                mMusicChangeListener.onMusicReplace();
            }

        } else if (i == R.id.btn_bgm_delete) {
            if (mMusicChangeListener != null) {
                mMusicChangeListener.onMusicDelete();
            }
        }
    }

    private void setCutRange(long startTime, long endTime) {
        if (startTime == -1 || endTime == -1)
            return;
        if (mRangeSlider != null && mBgmDuration != 0) {
            int left = (int) (startTime * 100 / mBgmDuration);
            int right = (int) (endTime * 100 / mBgmDuration);
            mRangeSlider.setCutRange(left, right);
        }
        if (mTvStartTime != null) {
            mTvStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position), DateTimeUtil.millsecondToMinuteSecond((int) startTime)));
        }
    }
}
