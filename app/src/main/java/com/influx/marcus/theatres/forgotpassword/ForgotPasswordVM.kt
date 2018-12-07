package com.influx.marcus.theatres.forgotpassword

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordReq
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class ForgotPasswordVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var forgotPasswordRepo: ForgotPasswordRepo
    private var forgotPasswordResp: MutableLiveData<ForgotPasswordResp> = MutableLiveData()

    init {
        forgotPasswordRepo = ForgotPasswordRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is ForgotPasswordResp) {
            forgotPasswordResp.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getForgotPasswordResponse(req: ForgotPasswordReq) {
        forgotPasswordRepo.fetchforgotPassword(req)
    }

    fun getForgotPasswordData(): MutableLiveData<ForgotPasswordResp> {
        return forgotPasswordResp
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }
}
