package com.influx.marcus.theatres.forgotpassword


import com.influx.marcus.theatres.api.ApiModels.specials.SpecialResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpecialsRepo  (val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun getspecialOffers() {
        val specialCall: Call<SpecialResp> = webApi.getspecialOffers(AppConstants.AUTHORISATION_HEADER)
        specialCall.enqueue(object : Callback<SpecialResp> {
            override fun onFailure(call: Call<SpecialResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<SpecialResp>?, response: Response<SpecialResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")

                }
            }
        })
    }
}
