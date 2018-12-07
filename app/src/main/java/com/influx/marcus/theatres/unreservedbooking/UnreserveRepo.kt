package com.influx.marcus.theatres.unreservedbooking

import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedResp
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnreserveRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchUnreserveTicketType(req: UnreservedReq) {
        val prefLocationCall: Call<UnreservedResp> =
                webApi.getUnreservedTicketType(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<UnreservedResp> {
            override fun onFailure(call: Call<UnreservedResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<UnreservedResp>?, response: Response<UnreservedResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun LockUnreservedSeat(req: LockUnreservedReq) {
        val prefLocationCall: Call<LockUnreservedResp> =
                webApi.lockUnreservedTickets(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<LockUnreservedResp> {
            override fun onFailure(call: Call<LockUnreservedResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<LockUnreservedResp>?, response: Response<LockUnreservedResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}
