package com.twx.module_base.base

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.alibaba.android.arouter.launcher.ARouter
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.PackageUtil
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.utils.initUm
import com.umeng.analytics.MobclickAgent

import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @name Wifi_Manager
 * @class name：com.example.module_base.base
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/1/7 10:59:12
 * @class describe
 */
open class BaseApplication : Application() {

    companion object {
        lateinit var application: BaseApplication
        lateinit var mHandler: Handler
        lateinit var mContext: Context
        lateinit var mPackName: String
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        mContext = applicationContext
        mHandler = Handler(Looper.getMainLooper())
        mPackName = packageName
        SPUtil.init(this@BaseApplication)
        GlobalScope.launch {
            ARouter.openDebug()
            ARouter.openLog()
            ARouter.init(this@BaseApplication)
        }

        //友盟 609b9d5c53b6726499fab726
        UMConfigure.preInit(
            this,
            "609b9d5c53b6726499fab726",
            PackageUtil.getAppMetaData(this, Constants.CHANNEL)
        )
//        if (SPUtil.getInstance().getBoolean(Constants.SP_AGREE)) {
//            initUm(this)
//            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
//        }

        initData()

    }

    open fun initData() {

    }
}