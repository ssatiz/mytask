package com.influx.marcus.theatres.forgotpassword


import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordReq
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordRepo  (val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchforgotPassword(req: ForgotPasswordReq) {
        val prefLocationCall: Call<ForgotPasswordResp> =
                webApi.getForgotpassword(AppConstants.AUTHORISATION_HEADER,req)
        prefLocationCall.enqueue(object : Callback<ForgotPasswordResp> {
            override fun onFailure(call: Call<ForgotPasswordResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<ForgotPasswordResp>?, response: Response<ForgotPasswordResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")

                }
            }
        })
    }
}
