package com.influx.marcus.theatres.showtime

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.NearCinemas.NearCinemasRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDReq
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RTomatoResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RtomatoReq
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class ShowtimeVM : ViewModel(), CallBackResult {


    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var showtimeRepo: ShowtimeRepo
    private var showtimeResponse: MutableLiveData<ShowtimeResponse> = MutableLiveData()
    private var showtimeMDResp: MutableLiveData<ShowtimeMDResp> = MutableLiveData()
    private var rtScore: MutableLiveData<RTomatoResp> = MutableLiveData()


    init {
        showtimeRepo = ShowtimeRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is ShowtimeResponse) {
            showtimeResponse.postValue(t)
        } else if (t is ShowtimeMDResp) {
            showtimeMDResp.postValue(t)
        } else if (t is RTomatoResp) {
            rtScore.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun SchedulesByPreferences(req: ShowtimeRequest) {
        showtimeRepo.fetchShowtimeData(req)
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }

    fun getSchedulesByPreferences(): MutableLiveData<ShowtimeResponse> {
        return showtimeResponse
    }

    fun getShowtimeMovieDetails(): MutableLiveData<ShowtimeMDResp> {
        return showtimeMDResp
    }

    fun fetchMovieDetails(req: ShowtimeMDReq) {
        showtimeRepo.fetchMovieDetails(req)
    }
    fun fetchNearbyShowTime(req: NearCinemasRequest){
        showtimeRepo.fetchNearCinemaData(req)
    }
    fun getNearbyShowTime(): MutableLiveData<ShowtimeResponse> {
        return showtimeResponse
    }
    fun getRottenTomatoScore(): MutableLiveData<RTomatoResp> {
        return rtScore
    }

    fun fetchRottenTomatoScore(req: RtomatoReq) {
        showtimeRepo.fetchRottenTomatoScore(req)
    }
}