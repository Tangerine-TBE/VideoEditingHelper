package com.tencent.qcloud.ugckit.module.effect;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitImpl;
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout;
import com.tencent.qcloud.ugckit.module.effect.utils.DraftEditer;
import com.tencent.qcloud.ugckit.module.effect.utils.EffectEditer;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;
import com.twx.module_base.extensions.ExtensionsKt;
import com.twx.module_base.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 由于SDK提供的TXVideoEditer为非单例模式
 * 当您需要在多个Activity\Fragment 之间对同一个Video进行编辑的时候，可以在上层将其包装为一个单例
 * <p>
 * 需要注意：
 * 完成一次视频编辑后，请务必调用{@link VideoEditerSDK#clear()}晴空相关的一些配置
 */
public class VideoEditerSDK {
    private static final String TAG = "VideoEditerKit";
    private static VideoEditerSDK INSTANCE;
    @Nullable
    private TXVideoEditer mTXVideoEditer;

    /**
     * 缩略图相关
     */
    private List<ThumbnailBitmapInfo> mThumbnailList;               // 将已经加在好的Bitmap缓存起来

    /**
     * 预览相关
     * <p>
     * 由于SDK没有提供多个Listener的预览进度的回调，所以在上层包装一下
     */
    private final List<TXVideoPreviewListenerWrapper> mPreviewWrapperList;
    private boolean mIsReverse;
    private long mCutterDuration;                                   // 裁剪的总时长
    private long mCutterStartTime;                                  // 裁剪开始的时间
    private long mCutterEndTime;                                    // 裁剪结束的时间

    public int getThumbnailCount() {
        return mThumbnailCount;
    }

    private int mThumbnailCount;//缩略图个数
    private TXVideoEditConstants.TXVideoInfo mTXVideoInfo;
    private String mVideoPath;
    private boolean mPublishFlag;

    public static VideoEditerSDK getInstance() {
        if (INSTANCE == null) {
            synchronized (VideoEditerSDK.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VideoEditerSDK();
                }
            }
        }
        return INSTANCE;
    }

    private VideoEditerSDK() {
        mThumbnailList = new ArrayList<>();
        mPreviewWrapperList = new ArrayList<>();
        mIsReverse = false;
    }

    public void setTXVideoInfo(TXVideoEditConstants.TXVideoInfo info) {
        Log.d(TAG, "setTXVideoInfo info:" + info);
        mTXVideoInfo = info;

    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
        mTXVideoEditer.setVideoPath(mVideoPath);
    }

    public String getVideoPath(){
        return mVideoPath;
    }


    public void setVideoPathInfo(String videoPathInfo){
        mVideoPath=videoPathInfo;
        mTXVideoEditer.setVideoPath(mVideoPath);
        mTXVideoInfo = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(mVideoPath);
        setCutterStartTime(0,mTXVideoInfo.duration);
        mThumbnailCount= (int) (mTXVideoInfo.duration/ExtensionsKt.videoTimeInterval(mTXVideoInfo.duration));

    }







    /**
     * 获取视频的信息
     * FIXBUG：不能判断是否为空，如果更换频路径，
     *
     * @return
     */
    public TXVideoEditConstants.TXVideoInfo getTXVideoInfo() {
        mTXVideoInfo = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(mVideoPath);
        if (mTXVideoInfo != null) {
            Log.d(TAG, "setTXVideoInfo duration:" + mTXVideoInfo.duration);
        }
        return mTXVideoInfo;
    }

    /**
     * 释放全部资源
     */
    public void clear() {
        if (mTXVideoEditer != null) {
            mTXVideoEditer.setTXVideoPreviewListener(null);
            mTXVideoEditer = null;
        }

        mCutterDuration = 0;
        mCutterStartTime = 0;
        mCutterEndTime = 0;
        mThumbnailCount=0;

        mThumbnailList.clear();
        DraftEditer.getInstance().clear();
        EffectEditer.getInstance().clear();

        synchronized (mPreviewWrapperList) {
            mPreviewWrapperList.clear();
        }
        mIsReverse = false;
    }


    /**
     * 释放编辑者
     */
    public void releaseSDK() {
        if (mTXVideoEditer != null) {
            mTXVideoEditer.release();
        }
    }

    /**
     * 设置裁剪后的时长
     *
     * @param newVideoDuration
     */
    public void setCutterDuration(long newVideoDuration) {
        mCutterDuration = newVideoDuration;
    }

    /**
     * 获取裁剪后的时长
     *
     * @return
     */
    public long geCutterDuration() {
        return mCutterDuration;
    }


    public void setCutterStartTime(long startTime, long endTime) {
        mCutterStartTime = startTime;
        mCutterEndTime = endTime;
        mCutterDuration = endTime - startTime;
    }

    /**
     * 设置剪辑开始的时间
     *
     * @return
     */
    public void setCutterStartTime(long cutterStartTime) {
        mCutterStartTime = cutterStartTime;
        mCutterDuration = mCutterEndTime - mCutterStartTime;
    }

    /**
     * 获设置辑结束的时间
     *
     * @return
     */
    public void setCutterEndTime(long cutterEndTime) {
        mCutterEndTime = cutterEndTime;
        mCutterDuration = mCutterEndTime - mCutterStartTime;
    }

    /**
     * 获取剪辑开始的时间
     *
     * @return
     */
    public long getCutterStartTime() {
        return mCutterStartTime;
    }

    /**
     * 获取剪辑结束的时间
     *
     * @return
     */
    public long getCutterEndTime() {
        return mCutterEndTime;
    }


    /**
     * ======================================================预览相关======================================================
     */
    public void setReverse(boolean isReverse) {
        mIsReverse = isReverse;
    }

    public boolean isReverse() {
        return mIsReverse;
    }

    @NonNull
    private TXVideoEditer.TXVideoPreviewListener mPreviewListener = new TXVideoEditer.TXVideoPreviewListener() {
        @Override
        public void onPreviewProgress(int time) {
            int currentTimeMs = (int) (time / 1000);//转为ms值
            synchronized (mPreviewWrapperList) {
                for (TXVideoPreviewListenerWrapper wrapper : mPreviewWrapperList) {
                    wrapper.onPreviewProgressWrapper(currentTimeMs);
                }
            }
        }

        @Override
        public void onPreviewFinished() {
            synchronized (mPreviewWrapperList) {
                for (TXVideoPreviewListenerWrapper wrapper : mPreviewWrapperList) {
                    wrapper.onPreviewFinishedWrapper();
                }
            }
        }
    };

    public void addTXVideoPreviewListenerWrapper(TXVideoPreviewListenerWrapper listener) {
        synchronized (mPreviewWrapperList) {
            if (mPreviewWrapperList.contains(listener)) {
                return;
            }
            mPreviewWrapperList.add(listener);
        }
    }

    public void removeTXVideoPreviewListenerWrapper(TXVideoPreviewListenerWrapper listener) {
        synchronized (mPreviewWrapperList) {
            mPreviewWrapperList.remove(listener);
        }
    }

    /**
     * 初始化新的TXVideoEditer
     */
    public void initSDK() {
        mTXVideoEditer = new TXVideoEditer(UGCKitImpl.getAppContext());
    }

    /**
     * 获取以前创建的TXVideoEditer
     *
     * @return
     */
    public TXVideoEditer getEditer() {
        return mTXVideoEditer;
    }

    public void constructVideoInfo(@NonNull TXVideoEditConstants.TXVideoInfo videoInfo, long duration) {
        videoInfo.width = 100;
        videoInfo.height = 100;
        videoInfo.duration = duration;
        mTXVideoInfo = videoInfo;
    }

    public void resetDuration() {
        if (mCutterEndTime - mCutterStartTime != 0) {
            mCutterDuration = mCutterEndTime - mCutterStartTime;
            mCutterStartTime = 0;
            mCutterEndTime = mCutterDuration;
        } else {
            TXVideoEditConstants.TXVideoInfo videoInfo = getTXVideoInfo();
            if (videoInfo != null) {
                mCutterDuration = videoInfo.duration;
            }
        }
        if (mTXVideoEditer != null) {
            mTXVideoEditer.setCutFromTime(0, mCutterDuration);
        }
    }

    public long getVideoDuration() {
        return mCutterDuration;
    }

    /**
     * 初始化缩略图
     *
     * @param listener
     * @param interval 缩略图的时间间隔
     */
    public void initThumbnailList(TXVideoEditer.TXThumbnailListener listener, int interval) {
        if (interval == 0) {
            Log.e(TAG, "interval error:0");
            return;
        }
        int durationS = 0;
        TXVideoEditConstants.TXVideoInfo videoInfo = getTXVideoInfo();
        if (videoInfo != null) {
            durationS = (int) (getTXVideoInfo().duration / interval);
        }
        // 每一秒/一张缩略图
        int thumbCount = durationS;
        LogUtils.i("------thumbCount---------------------------------------" + thumbCount);
        if (mTXVideoEditer != null) {
            mTXVideoEditer.setRenderRotation(0);
            // FIXBUG：获取缩略图之前需要设置缩略图的开始和结束时间点，SDK内部会根据开始时间和结束时间出缩略图
            mTXVideoEditer.setCutFromTime(0, mTXVideoInfo.duration);
            mTXVideoEditer.getThumbnail(thumbCount, IVideoCutLayout.DEFAULT_THUMBNAIL_WIDTH, IVideoCutLayout.DEFAULT_THUMBNAIL_HEIGHT, false, listener);
        }
    }

    public void setPublishFlag(boolean flag) {
        mPublishFlag = flag;
    }

    public boolean isPublish() {
        return mPublishFlag;
    }

    /**
     * 在特效页面设置特效，会调用SDK，特效点击"取消"后，还原设置进入SDK的特效
     */
    public void restore() {
        EffectEditer effectEditer = EffectEditer.getInstance();
        if (mTXVideoEditer != null) {
            mTXVideoEditer.setBGM(effectEditer.getBgmPath());
            mTXVideoEditer.setBGMVolume(effectEditer.getBgmVolume());
            mTXVideoEditer.setVideoVolume(effectEditer.getVideoVolume());
        }
    }

    /**
     * 由于SDK没有提供多个界面的预览进度的回调，所以在上层包装一下
     */
    public interface TXVideoPreviewListenerWrapper {
        /**
         * @param time
         */
        void onPreviewProgressWrapper(int time);

        void onPreviewFinishedWrapper();
    }

    /**
     * ======================================================缩略图相关======================================================
     */

    /**
     * 获取已经加载的缩略图
     *
     * @return
     */
    @NonNull
    public List<Bitmap> getThumbnailList(long startPts, long endPts) {
        List<Bitmap> list = new ArrayList<>();
        for (ThumbnailBitmapInfo info : mThumbnailList) {
            if (info.ptsMs >= startPts && info.ptsMs <= endPts) {
                list.add(info.bitmap);
            }
        }
        return list;
    }

    @NonNull
    public List<Bitmap> getAllThumbnails() {
        return getThumbnailList(0, mTXVideoInfo.duration);
    }


    public List<ThumbnailBitmapInfo> getAllThumbnailList(){ return mThumbnailList; }

    public void addThumbnailBitmap(long timeMs, Bitmap bitmap) {
        mThumbnailList.add(new ThumbnailBitmapInfo(timeMs, bitmap));
    }

    /**
     * 清空缩略图列表
     */
    public void clearThumbnails() {
        mThumbnailList.clear();
    }

    public int getThumbnailSize() {
        return mThumbnailList == null ? 0 : mThumbnailList.size();
    }

    public class ThumbnailBitmapInfo {
        public long ptsMs;
        public Bitmap bitmap;

        public ThumbnailBitmapInfo(long ptsMs, Bitmap bitmap) {
            this.ptsMs = ptsMs;
            this.bitmap = bitmap;
        }

    }

}
