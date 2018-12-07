package com.influx.marcus.theatres.splash

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionReq
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class SplashVM:ViewModel(), CallBackResult {

    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var versionData: MutableLiveData<VersionResp> = MutableLiveData()
    private var splashRepo: SplashRepo

    init {
        splashRepo = SplashRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is VersionResp) {
            versionData.postValue(t)
        } else {
            LogUtils.d("Splash", "response type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getVersionDetails(): MutableLiveData<VersionResp> {
        return versionData
    }

    fun getErrorDetails(): MutableLiveData<String> {
        return isApiError
    }

    fun fetchVersionData(authToken :String , req: VersionReq){
        splashRepo.fetchVersionDetails(authToken,req)
    }
}