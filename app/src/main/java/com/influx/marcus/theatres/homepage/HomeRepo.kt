package com.influx.marcus.theatres.homepage

import com.influx.marcus.theatres.api.ApiModels.filter.FilteredMoviesReq
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.home.HomeResponse
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepo(val Result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun SchedulesByPreferences(req: HomeRequest) {
        val SchedulesByPreferencesCall: Call<HomeResponse> =
                webApi.getSchedulesByPreferences(AppConstants.AUTHORISATION_HEADER, req)

        SchedulesByPreferencesCall.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                LogUtils.d("HomeResponse", t.toString())
                Result.onFailure(t!!.message!!)
                // Result.onFailure(call.toString())
            }

            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response!!.isSuccessful) {
                    // LogUtils.d("HomeResponse",response!!.body()!!.DATA.movies.now_showing.toString())
                    Result.onSuccess(response.body())
                } else {
                    //  LogUtils.d("HomeResponse",response!!.body()!!.DATA.movies.now_showing.toString())
                    Result.onFailure("2")
                }
            }

        })
    }

    fun getfilteredSchedules(req: FilteredMoviesReq) {
        val guestCall: Call<HomeResponse> = webApi.getMoviesByFilter(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        guestCall.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>?, t: Throwable?) {
                Result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<HomeResponse>?, response: Response<HomeResponse>?) {
                if (response!!.isSuccessful) {
                    Result.onSuccess(response.body())
                } else {
                    Result.onFailure("2")
                }
            }
        })
    }
}