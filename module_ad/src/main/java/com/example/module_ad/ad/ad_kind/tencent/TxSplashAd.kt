package com.example.module_ad.ad.ad_kind.tencent

import android.app.Activity
import android.widget.FrameLayout
import com.example.module_ad.ad.Ad
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener
import com.qq.e.comm.util.AdError
import com.twx.module_base.utils.LogUtils

class TxSplashAd( activity: Activity,container:FrameLayout):Ad(activity,container) {

    override fun showAd() {
        val splashAD = SplashAD(activity, mKgdtMobSDKKaiPingKey, object : SplashADListener {
            override fun onADDismissed() {
                mAdShowStateListener?.showSuccess()
            }

            override fun onNoAD(p0: AdError?) {
                LogUtils.i("---onNoAD-----${p0?.errorCode}---${p0?.errorMsg}------------------")
                mAdShowStateListener?.showError()
            }

            override fun onADPresent() {

            }

            override fun onADClicked() {

            }

            override fun onADTick(p0: Long) {

            }

            override fun onADExposure() {

            }

            override fun onADLoaded(p0: Long) {

            }

        },AD_TIME_OUT)
        splashAD.fetchAndShowIn(container)
    }

    override fun releaseAd() {

    }



}