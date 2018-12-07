package com.influx.marcus.theatres.bookingreview

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.bookingreview.BookingReviewResponse
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class ReviewVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var reviewRepo: ReviewRepo
    private var reviewResp: MutableLiveData<BookingReviewResponse> = MutableLiveData()

    init {
        reviewRepo = ReviewRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is BookingReviewResponse) {
            reviewResp.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }
}
