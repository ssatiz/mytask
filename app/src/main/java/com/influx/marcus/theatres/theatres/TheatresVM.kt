package com.influx.marcus.theatres.theatres

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSResp
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSResp
import com.influx.marcus.theatres.api.ApiModels.theatre.TheatreListResp
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class TheatresVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var theatresRepo: TheatresRepo
    private var theatresResp: MutableLiveData<TheatreListResp> = MutableLiveData()
    private var theatreDetailResp: MutableLiveData<TheatreShowtimeResp> = MutableLiveData()
    private var theatreGPSResp: MutableLiveData<OTGPSResp> = MutableLiveData()
    private var theatreNonGPSResp: MutableLiveData<OTNonGPSResp> = MutableLiveData()

    init {
        theatresRepo = TheatresRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is TheatreListResp) {
            theatresResp.postValue(t)
        } else if (t is TheatreShowtimeResp) {
            theatreDetailResp.postValue(t)
        } else if (t is OTNonGPSResp) {
            theatreNonGPSResp.postValue(t)
        } else if (t is OTGPSResp) {
            theatreGPSResp.postValue(t)
        } else {
            LogUtils.d("OurTheatres", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getTheatreDetailResponse(req: TheatreShowtimeRequest) {
        theatresRepo.getTheatreDetailsList(req)
    }

    fun getTheatreDetailData(): MutableLiveData<TheatreShowtimeResp> {
        return theatreDetailResp
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }

    fun getOTGPSDetails(): MutableLiveData<OTGPSResp> {
        return theatreGPSResp
    }

    fun fetchOTGPSDetails(req: OTGPSReq) {
        theatresRepo.getOTGPSDetails(req)
    }

    fun getOTNonGPSDetails(): MutableLiveData<OTNonGPSResp> {
        return theatreNonGPSResp
    }

    fun fetchOTNonGPSDetails(req: OTNonGPSReq) {
        return theatresRepo.getOTNonGPSDetails(req)
    }
}
