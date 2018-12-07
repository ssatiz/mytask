package com.influx.marcus.theatres.bookingreview

import com.influx.marcus.theatres.api.ApiModels.bookingreview.BookingReviewReq
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.CallBackResult

class ReviewRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchReviewResp(req: BookingReviewReq) {

    }
}
