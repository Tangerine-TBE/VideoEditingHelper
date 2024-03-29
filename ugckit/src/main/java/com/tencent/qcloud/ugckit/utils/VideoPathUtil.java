package com.tencent.qcloud.ugckit.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 视频路径生成器
 */
public class VideoPathUtil {
    private static final String TAG = "VideoPathUtil";


    public static String createPath(){
        return  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + UGCKitConstants.DEFAULT_MEDIA_OUT_PATH;
    }
    public static String createJoinPath(){
        return  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + UGCKitConstants.READY_JOIN_OUT_PATH;
    }


    public static String generateJoinVideoPath(int index) {
        String outputPath = createJoinPath();
        File outputFolder = new File(outputPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String current = String.valueOf(System.currentTimeMillis() / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(new Date(Long.valueOf(current + "000")));
        String saveFileName = String.format(index+"TWXVideo_%s.mp4", time);
        return outputFolder + "/" + saveFileName;
    }



    /**
     * 生成编辑后输出视频路径
     *
     * @return
     */
    public static String generateVideoPath() {
      /*  File sdcardDir = UGCKit.getAppContext().getExternalFilesDir(null);
        if (sdcardDir == null) {
            Log.e(TAG, "generateVideoPath sdcardDir is null");
            return "";
        }
        String outputPath = sdcardDir + File.separator + UGCKitConstants.DEFAULT_MEDIA_PACK_FOLDER;*/

        String outputPath = createPath();
        File outputFolder = new File(outputPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String current = String.valueOf(System.currentTimeMillis() / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(new Date(Long.valueOf(current + "000")));
        String saveFileName = String.format("TWXVideo_%s.mp4", time);
        return outputFolder + "/" + saveFileName;
    }

    /**
     * 生成编辑后输出视频路径
     *
     * @return
     */
    public static String generateVideoPath2(int index) {
      /*  File sdcardDir = UGCKit.getAppContext().getExternalFilesDir(null);
        if (sdcardDir == null) {
            Log.e(TAG, "generateVideoPath sdcardDir is null");
            return "";
        }
        String outputPath = sdcardDir + File.separator + UGCKitConstants.DEFAULT_MEDIA_PACK_FOLDER;*/

        String outputPath = createPath();
        File outputFolder = new File(outputPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String current = String.valueOf(System.currentTimeMillis() / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(new Date(Long.valueOf(current + "000")));
        String saveFileName = String.format(index+"TWXVideo_%s.mp4", time);
        return outputFolder + "/" + saveFileName;
    }


    public static String getCustomVideoOutputPath() {
        return getCustomVideoOutputPath(null);
    }

    public static String getCustomVideoOutputPath(String fileNamePrefix) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String time = sdf.format(new Date(currentTime));

        File sdcardDir = UGCKit.getAppContext().getExternalFilesDir(null);
        if (sdcardDir == null) {
            Log.e(TAG, "sdcardDir is null");
            return null;
        }

        String outputDir = sdcardDir + File.separator + UGCKitConstants.OUTPUT_DIR_NAME;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }
        String tempOutputPath;
        if (TextUtils.isEmpty(fileNamePrefix)) {
            tempOutputPath = outputDir + File.separator + "TXUGC_" + time + ".mp4";
        } else {
            tempOutputPath = outputDir + File.separator + "TXUGC_" + fileNamePrefix + time + ".mp4";
        }
        return tempOutputPath;
    }
}
