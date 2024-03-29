package com.twx.module_base.base

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.twx.module_base.livedata.MakeBackLiveData
import com.twx.module_base.utils.Constants
import com.twx.module_base.utils.MyActivityManager
import com.twx.module_base.utils.MyStatusBarUtil
import com.twx.module_base.utils.SPUtil
import com.twx.module_base.widget.LoadingDialog
import com.twx.module_base.widget.popup.LoadingPopup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @author: 铭少
 * @date: 2021/1/16 0016
 * @description：
 */
open class BaseActivity:FragmentActivity() {


    protected val mJob=Job()
    protected val mScope by lazy {
        CoroutineScope(mJob)
    }
    protected val sp: SPUtil by lazy{
        SPUtil.getInstance()
    }

    protected val mLoadingDialog by lazy{
        LoadingDialog(this)
    }

    protected val themeState by lazy {
        SPUtil.getInstance().getBoolean(Constants.SP_THEME_STATE)
    }

    protected val loadingPopup by lazy {
        LoadingPopup(this).apply { setTitle("视频导出中...") }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreenWindow()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        MyActivityManager.addActivity(this)

    }

    open fun setFullScreenWindow(){
        MyStatusBarUtil.setColor(this, Color.TRANSPARENT)
        MyStatusBarUtil.setFullWindow(this)
    }


    fun visibleView(vararg view:View){
        view.forEach {
            it.visibility=View.VISIBLE
        }
    }

    fun goneView(vararg view:View){
        view.forEach {
            it.visibility=View.GONE
        }
    }


    open fun release() {

        loadingPopup.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        MyActivityManager.removeActivity(this)

        release()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (!MakeBackLiveData.getMakeState()){
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}