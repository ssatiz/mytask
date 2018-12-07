package com.influx.marcus.theatres.guestcheckout

import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegReq
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuestRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun registerGuestUser(req: GuestRegReq) {
        val guestCall: Call<GuestRegResp> = webApi.registerGuestUser(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        guestCall.enqueue(object : Callback<GuestRegResp> {
            override fun onFailure(call: Call<GuestRegResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<GuestRegResp>?, response: Response<GuestRegResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}