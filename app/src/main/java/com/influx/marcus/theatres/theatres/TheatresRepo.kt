package com.influx.marcus.theatres.theatres

import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSResp
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSResp
import com.influx.marcus.theatres.api.ApiModels.theatre.TheatreListReq
import com.influx.marcus.theatres.api.ApiModels.theatre.TheatreListResp
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TheatresRepo(val result: CallBackResult) {
    private val webApi: ApiInterface = RestClient.getApiClient()

    fun getTheatreList(req: TheatreListReq) {
        val theatreListCall: Call<TheatreListResp> = webApi.getTheatreList(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        theatreListCall.enqueue(object : Callback<TheatreListResp> {
            override fun onFailure(call: Call<TheatreListResp>, t: Throwable) {
                result.onFailure(t.message!!)
            }

            override fun onResponse(call: Call<TheatreListResp>, response: Response<TheatreListResp>) {
                if (response.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun getTheatreDetailsList(req: TheatreShowtimeRequest) {
        val theatreListCall: Call<TheatreShowtimeResp> = webApi.getTheatreDetails(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        theatreListCall.enqueue(object : Callback<TheatreShowtimeResp> {
            override fun onFailure(call: Call<TheatreShowtimeResp>, t: Throwable) {
                result.onFailure(t.message!!)
            }

            override fun onResponse(call: Call<TheatreShowtimeResp>, response: Response<TheatreShowtimeResp>) {
                if (response.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun getOTGPSDetails(req: OTGPSReq) {

        val gpsTheaterCall: Call<OTGPSResp> = webApi.getOurTheatresGPS(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        gpsTheaterCall.enqueue(object : Callback<OTGPSResp> {
            override fun onFailure(call: Call<OTGPSResp>, t: Throwable) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<OTGPSResp>, response: Response<OTGPSResp>) {
                if (response.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun getOTNonGPSDetails(req: OTNonGPSReq) {
        val nonTheatreCall: Call<OTNonGPSResp> = webApi.getOurTheatresNonGPS(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        nonTheatreCall.enqueue(object : Callback<OTNonGPSResp> {
            override fun onFailure(call: Call<OTNonGPSResp>, t: Throwable) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<OTNonGPSResp>, response: Response<OTNonGPSResp>) {
                if (response.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}
