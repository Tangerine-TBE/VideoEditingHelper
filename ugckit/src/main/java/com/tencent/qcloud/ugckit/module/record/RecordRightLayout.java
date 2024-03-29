package com.tencent.qcloud.ugckit.module.record;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tencent.qcloud.ugckit.R;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordRightLayout;

public class RecordRightLayout extends RelativeLayout implements IRecordRightLayout,
        View.OnClickListener, AspectView.OnAspectListener {
    private static final String TAG = "RecordRightLayout";
    private Activity mActivity;
    // 音乐
    private ImageView mIvMusic;
    private TextView mTvMusic;
    private ImageView mIvMusicMask;
    private RelativeLayout mLayoutMusic;
    // 屏比，目前有三种（1:1；3:4；9:16）
    private AspectView mAspectView;
    // 美颜
    private ImageView mIvBeauty;
    private TextView mTvBeauty;
    private RelativeLayout mLayoutBeauty;
    // 音效
    private ImageView mIvSoundEffect;
    private TextView mTvSoundEffect;
    private ImageView mIvSoundEffectMask;
    private RelativeLayout mLayoutSoundEffect;
    private OnItemClickListener mOnItemClickListener;

    public RecordRightLayout(Context context) {
        super(context);
        initViews();
    }

    public RecordRightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public RecordRightLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mActivity = (Activity) getContext();
        inflate(mActivity, R.layout.record_right_layout, this);

        mLayoutMusic = (RelativeLayout) findViewById(R.id.layout_music);
        mIvMusic = (ImageView) findViewById(R.id.iv_music);
        mTvMusic = (TextView) findViewById(R.id.tv_music);
        mIvMusic.setOnClickListener(this);
        mIvMusicMask = (ImageView) findViewById(R.id.iv_music_mask);

        mAspectView = (AspectView) findViewById(R.id.aspect_view);
        mAspectView.setOnAspectListener(this);

        mLayoutBeauty = (RelativeLayout) findViewById(R.id.layout_beauty);
        mIvBeauty = (ImageView) findViewById(R.id.iv_beauty);
        mTvBeauty = (TextView) findViewById(R.id.tv_beauty);
        mIvBeauty.setOnClickListener(this);

        mLayoutSoundEffect = (RelativeLayout) findViewById(R.id.layout_sound_effect);
        mIvSoundEffect = (ImageView) findViewById(R.id.iv_sound_effect);
        mTvSoundEffect = (TextView) findViewById(R.id.tv_sound_effect);
        mIvSoundEffect.setOnClickListener(this);
        mIvSoundEffectMask = (ImageView) findViewById(R.id.iv_sound_effect_mask);
    }

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        if (id == R.id.iv_beauty) {
            mOnItemClickListener.onShowBeautyPanel();
        } else if (id == R.id.iv_music) {
            mOnItemClickListener.onShowMusicPanel();
        } else if (id == R.id.iv_sound_effect) {
            mOnItemClickListener.onShowSoundEffectPanel();
        }
    }

    /**
     * 切换了一种屏比
     *
     * @param currentAspect 当前屏比
     */
    @Override
    public void onAspectSelect(int currentAspect) {
        mOnItemClickListener.onAspectSelect(currentAspect);
    }

    /**
     * 设置"音乐"按钮是否可用
     *
     * @param enable {@code true} 可点击<br>
     *               {@code false} 不可点击
     */
    @Override
    public void setMusicIconEnable(boolean enable) {
        if (enable) {
            mIvMusicMask.setVisibility(View.GONE);
        } else {
            mIvMusicMask.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置"屏比"按钮是否可用
     *
     * @param enable {@code true} 可点击<br>
     *               {@code false} 不可点击
     */
    @Override
    public void setAspectIconEnable(boolean enable) {
        mAspectView.hideAspectSelectAnim();
        if (enable) {
            mAspectView.disableMask();
        } else {
            mAspectView.enableMask();
        }
    }

    /**
     * 设置"音效"按钮是否可用
     *
     * @param enable {@code true} 清除背景音后，音效Icon变为可点击<br>
     *               {@code false} 录制添加BGM后是录制不了人声的，而音效是针对人声有效的，此时开启音效遮罩层，音效Icon变为不可用
     */
    @Override
    public void setSoundEffectIconEnable(boolean enable) {
        if (enable) {
            mIvSoundEffectMask.setVisibility(View.INVISIBLE);
        } else {
            mIvSoundEffectMask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void disableRecordMusic() {
        mLayoutMusic.setVisibility(View.GONE);
    }

    public void disableRecordSoundEffect() {
        mLayoutSoundEffect.setVisibility(View.GONE);
    }

    public void disableAspect() {
        mAspectView.setVisibility(View.GONE);
    }

    public void disableBeauty() {
        mLayoutBeauty.setVisibility(View.GONE);
    }

    @Override
    public void setMusicIconResource(int resid) {
        mIvMusic.setImageResource(resid);
    }

    @Override
    public void setMusicTextSize(int size) {
        mTvMusic.setTextSize(size);
    }

    @Override
    public void setMusicTextColor(int color) {
        mTvMusic.setTextColor(getResources().getColor(color));
    }

    @Override
    public void setAspectTextSize(int size) {
        mAspectView.setTextSize(size);
    }

    @Override
    public void setAspectTextColor(int color) {
        mAspectView.setTextColor(color);
    }

    @Override
    public void setAspectIconList(int[] residList) {
        mAspectView.setIconList(residList);
    }

    @Override
    public void setBeautyIconResource(int resid) {
        mIvBeauty.setImageResource(resid);
    }

    @Override
    public void setBeautyTextSize(int size) {
        mTvBeauty.setTextSize(size);
    }

    @Override
    public void setBeautyTextColor(int color) {
        mTvBeauty.setTextColor(getResources().getColor(color));
    }

    @Override
    public void setSoundEffectIconResource(int resid) {
        mIvSoundEffect.setImageResource(resid);
    }

    @Override
    public void setSoundEffectTextSize(int size) {
        mTvSoundEffect.setTextSize(size);
    }

    @Override
    public void setSoundEffectTextColor(int color) {
        mTvSoundEffect.setTextColor(getResources().getColor(color));
    }

    public void setAspect(int aspectRatio) {
        mAspectView.setAspect(aspectRatio);
    }
}
