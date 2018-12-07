package com.influx.marcus.theatres.moviefilter

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.filter.FilterReq
import com.influx.marcus.theatres.api.ApiModels.filter.FilterResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class FilterVM : ViewModel(), CallBackResult {

    private val filterRepo: FilterRepo
    private val filterResp: MutableLiveData<FilterResp> = MutableLiveData()
    private val apiError: MutableLiveData<String> = MutableLiveData()

    init {
        filterRepo = FilterRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is FilterResp) {
            filterResp.postValue(t)
        } else  {
            LogUtils.d("GuestVM", "Undefined response type")
        }
    }

    override fun onFailure(error: String) {
        apiError.postValue(error)
    }

    fun getApiErrorDetails(): MutableLiveData<String> {
        return apiError
    }

    fun getfilterData(): MutableLiveData<FilterResp> {
        return filterResp
    }

    fun filterRequest(req: FilterReq) {
        filterRepo.filterRequest(req)
    }
}