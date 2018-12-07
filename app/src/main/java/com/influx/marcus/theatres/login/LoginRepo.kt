package com.influx.marcus.theatres.login

import com.influx.marcus.theatres.api.ApiModels.login.FacebookRequest
import com.influx.marcus.theatres.api.ApiModels.login.GoogleRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginResponse
import com.influx.marcus.theatres.api.ApiModels.rewardlogin.RewardLoginRequest
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepo (val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchLoginResp(req: LoginRequest) {
        val prefLocationCall: Call<LoginResponse> =
                webApi.getLoginResp(AppConstants.AUTHORISATION_HEADER,req)
        prefLocationCall.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
    fun fetchFbLoginResp(req: FacebookRequest) {
        val prefLocationCall: Call<LoginResponse> =
                webApi.facebookLogin(AppConstants.AUTHORISATION_HEADER,req)
        prefLocationCall.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
    fun fetchGoogleLoginResp(req: GoogleRequest) {
        val prefLocationCall: Call<LoginResponse> =
                webApi.googleLogin(AppConstants.AUTHORISATION_HEADER,req)
        prefLocationCall.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
    fun fetchRewardLoginResp(req: RewardLoginRequest) {
        val prefLocationCall: Call<LoginResponse> =
                webApi.getRewardLogin(AppConstants.AUTHORISATION_HEADER,req)
        prefLocationCall.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

}