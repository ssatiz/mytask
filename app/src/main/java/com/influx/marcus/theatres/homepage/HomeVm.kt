package com.influx.marcus.theatres.homepage

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.filter.FilteredMoviesReq
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.home.HomeResponse
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class HomeVm : ViewModel(),CallBackResult{

    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var homeRepo: HomeRepo
    private var homeResponse: MutableLiveData<HomeResponse> = MutableLiveData()

    init {
        homeRepo = HomeRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is HomeResponse) {
            homeResponse.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun SchedulesByPreferences(req: HomeRequest) {
        homeRepo.SchedulesByPreferences(req)
    }
    fun SchedulesByFilters(req: FilteredMoviesReq) {
        homeRepo.getfilteredSchedules(req)
    }
    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }

    fun getSchedulesByPreferences(): MutableLiveData<HomeResponse> {
        return homeResponse
    }
}