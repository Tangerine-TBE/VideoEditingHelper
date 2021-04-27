package com.twx.module_videoediting.viewmodel


import com.twx.module_base.base.BaseViewModel


/**
 * @name Wifi_Manager
 * @class name：com.example.wifi_manager.viewmodel
 * @class describe
 * @author wujinming QQ:1245074510
 * @time 2021/1/28 14:37:47
 * @class describe
 */
class BeginViewModel : BaseViewModel() {

/*    val loadAdState by lazy {
        MutableLiveData<RequestNetState>()
    }

    fun loadAdMsg(){
        viewModelScope.launch(Dispatchers.IO){
            AdRepository.getAdMsg().exAwait({
                LogUtils.i("-----AdRepository-----------$it-------------")
                loadAdState.postValue(RequestNetState.ERROR)
            }, {
                if (it.code()==200) {
                    val adBean = it.body()
                    if (adBean != null) {
                        loadAdState.postValue(RequestNetState.SUCCESS)
                        sp.putString(Contents.AD_INFO, Gson().toJson(adBean.data))

                    } else {
                        loadAdState.postValue(RequestNetState.ERROR)
                    }
                }else{
                    loadAdState.postValue(RequestNetState.ERROR)
                }
            })
        }
    }*/


}