package com.influx.marcus.theatres.preferences

import com.influx.marcus.theatres.api.ApiModels.getpref.PrefDataReq
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsReq
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.statelist.StateListResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreferenceRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchPreferedLocationData(req: PreferedLocsReq) {

        val authToken = req.authToken
        val lat = req.lat
        val long = req.long
        val statecode = req.state
        val prefDataReq:PrefDataReq = PrefDataReq(statecode,lat,long,AppConstants.APP_VERSION,
                AppConstants.APP_PLATFORM)
        val prefLocationCall: Call<PreferedLocsResp> = webApi.getPreferenceData(authToken, prefDataReq)
        prefLocationCall.enqueue(object : Callback<PreferedLocsResp> {
            override fun onFailure(call: Call<PreferedLocsResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<PreferedLocsResp>?, response: Response<PreferedLocsResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchLocationBasedOnState(authToken: String, stateCode: String) {
        val locationListCall: Call<PreferedLocsResp> = webApi.getLocationsBasedOnState(authToken, stateCode)
        locationListCall.enqueue(object : Callback<PreferedLocsResp> {
            override fun onFailure(call: Call<PreferedLocsResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<PreferedLocsResp>?, response: Response<PreferedLocsResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchAllStateList(authToken: String) {
        val stateListCall: Call<StateListResp> = webApi.getAllLocations(authToken)
        stateListCall.enqueue(object : Callback<StateListResp> {
            override fun onFailure(call: Call<StateListResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<StateListResp>?, response: Response<StateListResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}