package com.influx.marcus.theatres.seatlayout

import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatReq
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatReq
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeatlayoutRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun getSeatLayoutData(req: SeatReq) {
        val seatCall: Call<SeatResp> = webApi.getSeatLayout(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        seatCall.enqueue(object : Callback<SeatResp> {
            override fun onFailure(call: Call<SeatResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<SeatResp>?, response: Response<SeatResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun blockThisSeat(req: BlockSeatReq) {
        val seatLockCall: Call<BlockSeatResp> = webApi.blockSeat(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        seatLockCall.enqueue(object : Callback<BlockSeatResp> {
            override fun onFailure(call: Call<BlockSeatResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<BlockSeatResp>?, response: Response<BlockSeatResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}