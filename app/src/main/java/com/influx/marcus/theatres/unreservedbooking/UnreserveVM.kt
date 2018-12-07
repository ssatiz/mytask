package com.influx.marcus.theatres.unreservedbooking

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedResp
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class UnreserveVM : ViewModel(), CallBackResult {

    private val unreserveRepo: UnreserveRepo
    private val unreserveResp: MutableLiveData<UnreservedResp> = MutableLiveData()
    private val lockSeatResp: MutableLiveData<LockUnreservedResp> = MutableLiveData()
    private val apiError: MutableLiveData<String> = MutableLiveData()

    init {
        unreserveRepo = UnreserveRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is UnreservedResp) {
            unreserveResp.postValue(t)
        } else if (t is LockUnreservedResp) {
            lockSeatResp.postValue(t)
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

    fun getunreserveTicketDetails(): MutableLiveData<UnreservedResp> {
        return unreserveResp
    }

    fun unreserveTicket(req: UnreservedReq) {
        unreserveRepo.fetchUnreserveTicketType(req)
    }

    fun getLockSeatDetails():MutableLiveData<LockUnreservedResp>{
        return lockSeatResp
    }

    fun lockSeatDetails(req: LockUnreservedReq){
        unreserveRepo.LockUnreservedSeat(req)
    }

}