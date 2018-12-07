package com.influx.marcus.theatres.forgotpassword

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.specials.SpecialResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class SpecialsVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var specialsRepo: SpecialsRepo
    private var specialsResp: MutableLiveData<SpecialResp> = MutableLiveData()

    init {
        specialsRepo = SpecialsRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is SpecialResp) {
            specialsResp.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getSpecialsResponse() {
        specialsRepo.getspecialOffers()
    }

    fun getSpecialsData(): MutableLiveData<SpecialResp> {
        return specialsResp
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }
}
