package com.influx.marcus.theatres.bookingconfirmation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationReq
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class ConfirmationVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var confirmationRepo: ConfirmationRepo
    private var confirmationResp: MutableLiveData<BookingConfirmationResp> = MutableLiveData()

    init {
        confirmationRepo = ConfirmationRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is BookingConfirmationResp) {
            confirmationResp.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getBookingConfirmationResponse(req: BookingConfirmationReq) {
        confirmationRepo.fetchConfirmationResp(req)
    }

    fun getConfirmationRespData(): MutableLiveData<BookingConfirmationResp> {
        return confirmationResp
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }
}
