package com.influx.marcus.theatres.bookingconfirmation

import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationReq
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmationRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchConfirmationResp(req: BookingConfirmationReq) {
        val prefLocationCall: Call<BookingConfirmationResp> =
                webApi.getBookingReviewConf(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<BookingConfirmationResp> {
            override fun onFailure(call: Call<BookingConfirmationResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<BookingConfirmationResp>?, response: Response<BookingConfirmationResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}
