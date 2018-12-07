package com.influx.marcus.theatres.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.login.FacebookRequest
import com.influx.marcus.theatres.api.ApiModels.login.GoogleRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginResponse
import com.influx.marcus.theatres.api.ApiModels.rewardlogin.RewardLoginRequest
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class LoginVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var loginRepo: LoginRepo
    private var loginResp: MutableLiveData<LoginResponse> = MutableLiveData()

    init {
        loginRepo = LoginRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is LoginResponse) {
            loginResp.postValue(t)
        } /*else if (t is StateListResp) {
            loginResp.postValue(t)
        }*/ else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getloginResponse(req: LoginRequest) {
        loginRepo.fetchLoginResp(req)
    }

    fun getFacebookResponse(req: FacebookRequest) {
        loginRepo.fetchFbLoginResp(req)
    }

    fun getGoogleResponse(req: GoogleRequest) {
        loginRepo.fetchGoogleLoginResp(req)
    }

    fun getReviewLoginResponse(req: RewardLoginRequest) {
        loginRepo.fetchRewardLoginResp(req)
    }


    fun getLoginRespData(): MutableLiveData<LoginResponse> {
        return loginResp
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }
}
