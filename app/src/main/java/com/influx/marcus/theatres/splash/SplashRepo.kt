package com.influx.marcus.theatres.splash

import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionReq
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashRepo (val result:CallBackResult) {

    private val webApi: ApiInterface = RestClient.getApiClient()

    fun fetchVersionDetails(authToken:String ,req : VersionReq){
        val versionCall: Call<VersionResp> = webApi.checkVersionDetails(authToken,req)
        versionCall.enqueue(object : Callback<VersionResp>{
            override fun onFailure(call: Call<VersionResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<VersionResp>?, response: Response<VersionResp>?) {
                if (response!!.isSuccessful){
                    result.onSuccess(response.body())
                }else{
                    result.onFailure("2")
                }
            }
        })
    }
}