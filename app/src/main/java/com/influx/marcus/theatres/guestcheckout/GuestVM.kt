package com.influx.marcus.theatres.guestcheckout

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegReq
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class GuestVM : ViewModel(), CallBackResult {

    private val guestRepo: GuestRepo
    private val guestResp: MutableLiveData<GuestRegResp> = MutableLiveData()
    private val apiError: MutableLiveData<String> = MutableLiveData()

    init {
        guestRepo = GuestRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is GuestRegResp) {
            guestResp.postValue(t)
        } else {
            LogUtils.d("GuestVM", "Undefined response type")
        }
    }

    override fun onFailure(error: String) {
        apiError.postValue(error)
    }

    fun getApiErrorDetails(): MutableLiveData<String> {
        return apiError
    }

    fun getGuestRegDetails(): MutableLiveData<GuestRegResp> {
        return guestResp
    }

    fun registerGuestUser(req: GuestRegReq) {
        guestRepo.registerGuestUser(req)
    }
}