package com.influx.marcus.theatres.preferences

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsReq
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.statelist.StateListResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class PreferenceVM : ViewModel(), CallBackResult {

    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var prefRepo: PreferenceRepo
    private var preferedLocsResp: MutableLiveData<PreferedLocsResp> = MutableLiveData()
    private var stateListResp: MutableLiveData<StateListResp> = MutableLiveData()

    init {
        prefRepo = PreferenceRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is PreferedLocsResp) {
            preferedLocsResp.postValue(t)
        } else if (t is StateListResp) {
            stateListResp.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun fetchPreferedLocationData(req: PreferedLocsReq) {
        prefRepo.fetchPreferedLocationData(req)
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }

    fun getPreferedLocationData(): MutableLiveData<PreferedLocsResp> {
        return preferedLocsResp
    }

    fun fetchAllStates(authToken: String) {
        prefRepo.fetchAllStateList(authToken)
    }

    fun getAllStateData(): MutableLiveData<StateListResp> {
        return stateListResp
    }

    fun fetchLocationsBasedOnState(authToken: String, stateCode: String) {
        prefRepo.fetchLocationBasedOnState(authToken, stateCode)
    }

}