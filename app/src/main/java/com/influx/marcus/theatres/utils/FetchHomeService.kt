package com.influx.marcus.theatres.utils

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.home.HomeResponse
import com.influx.marcus.theatres.api.ApiModels.home.Preference
import com.influx.marcus.theatres.api.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FetchHomeService() : IntentService("FetchHomeData") {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        LogUtils.d("FetchHomeService", "start api call")
        val webApi = RestClient.getApiClient()
        val context: Context = this@FetchHomeService
        val cinema = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context)
        val genrs = AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context)
        val language = AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context)
        val stateCode = AppConstants.getString(AppConstants.KEY_STATE_CODE, context)
        val prefs = Preference(cinema, language, genrs)
        val homeRequest = HomeRequest(AppConstants.APP_PLATFORM, AppConstants.APP_VERSION, prefs, stateCode)
        val SchedulesByPreferencesCall: Call<HomeResponse> =
                webApi.getSchedulesByPreferences(AppConstants.AUTHORISATION_HEADER, homeRequest)

        SchedulesByPreferencesCall.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                LogUtils.d("HomeResponse", " throwable error - ${t.toString()}")
                LogUtils.d("FetchHomeService", "api call error")
            }

            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response!!.isSuccessful) {
                    LogUtils.d("FetchHomeService", " api call response")
                    AppConstants.putObject(AppConstants.KEY_HOMEPAGEDATA, response, context)
                } else {
                    LogUtils.d("FetchHomeService", "api call response notsuccess")
                    //do nothing
                }
            }
        })
    }
}