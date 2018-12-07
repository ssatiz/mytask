package com.influx.marcus.theatres.seatlayout

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatReq
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatReq
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class SeatVM : ViewModel(), CallBackResult {

    private val seatRepo: SeatlayoutRepo
    private var apiErrorData: MutableLiveData<String> = MutableLiveData()
    private var seatResp: MutableLiveData<SeatResp> = MutableLiveData()
    private var blockSeatResp: MutableLiveData<BlockSeatResp> = MutableLiveData()

    init {
        seatRepo = SeatlayoutRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is SeatResp) {
            seatResp.postValue(t)
        } else if (t is BlockSeatResp) {
            blockSeatResp.postValue(t)
        } else {
            LogUtils.d("SeatVM", "Undefined response")
        }
    }

    override fun onFailure(error: String) {
        apiErrorData.postValue(error)
    }

    fun getApiErrorDetail(): MutableLiveData<String> {
        return apiErrorData
    }

    fun getSeatLayoutData(): MutableLiveData<SeatResp> {
        return seatResp
    }

    fun fetchSeatLayout(req: SeatReq) {
        seatRepo.getSeatLayoutData(req)
    }

    fun submitBlockSeatRequest(req: BlockSeatReq) {
        seatRepo.blockThisSeat(req)
    }

    fun getBlockSeatResult(): MutableLiveData<BlockSeatResp> {
        return blockSeatResp
    }
}