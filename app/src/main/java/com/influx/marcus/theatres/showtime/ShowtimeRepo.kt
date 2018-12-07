package com.influx.marcus.theatres.showtime

import com.influx.marcus.theatres.api.ApiModels.NearCinemas.NearCinemasRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDReq
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RTomatoResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RtomatoReq
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowtimeRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchShowtimeData(req: ShowtimeRequest) {
        val prefLocationCall: Call<ShowtimeResponse> =
                webApi.getShowtimes(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<ShowtimeResponse> {
            override fun onFailure(call: Call<ShowtimeResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<ShowtimeResponse>?, response: Response<ShowtimeResponse>?) {
                if (response!!.isSuccessful && response.body()!!.STATUS) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchNearCinemaData(req: NearCinemasRequest) {
        val prefLocationCall: Call<ShowtimeResponse> =
                webApi.getNearestCinemasShowtime(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<ShowtimeResponse> {
            override fun onFailure(call: Call<ShowtimeResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<ShowtimeResponse>?, response: Response<ShowtimeResponse>?) {
                if (response!!.isSuccessful && response.body()!!.STATUS) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }


    fun fetchMovieDetails(req: ShowtimeMDReq) {
        val movieDetailCall: Call<ShowtimeMDResp> =
                webApi.getMovieDetails(AppConstants.AUTHORISATION_HEADER, req)
        movieDetailCall.enqueue(object : Callback<ShowtimeMDResp> {
            override fun onFailure(call: Call<ShowtimeMDResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<ShowtimeMDResp>?, response: Response<ShowtimeMDResp>?) {
                if (response!!.isSuccessful && response.body()!!.STATUS) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchRottenTomatoScore(req: RtomatoReq) {
        val rtScoreCall: Call<RTomatoResp> =
                webApi.getRTScore(AppConstants.AUTHORISATION_HEADER, req)
        rtScoreCall.enqueue(object : Callback<RTomatoResp> {
            override fun onFailure(call: Call<RTomatoResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<RTomatoResp>?, response: Response<RTomatoResp>?) {
                if (response!!.isSuccessful && response.body()!!.STATUS) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}