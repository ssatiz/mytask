package com.influx.marcus.theatres.moviefilter

import com.influx.marcus.theatres.api.ApiModels.filter.FilterReq
import com.influx.marcus.theatres.api.ApiModels.filter.FilterResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilterRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun filterRequest(req: FilterReq) {
        val guestCall: Call<FilterResp> = webApi.getMovieFilters(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        guestCall.enqueue(object : Callback<FilterResp> {
            override fun onFailure(call: Call<FilterResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<FilterResp>?, response: Response<FilterResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

}