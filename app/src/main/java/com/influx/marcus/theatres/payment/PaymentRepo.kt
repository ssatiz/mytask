package com.influx.marcus.theatres.payment

import com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard.DeleteSavedCardReq
import com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard.DeleteSavedCardResp
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookReq
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookingResp
import com.influx.marcus.theatres.api.ApiModels.carddetail.CardDetailReq
import com.influx.marcus.theatres.api.ApiModels.carddetail.CardDetailResp
import com.influx.marcus.theatres.api.ApiModels.paymentcardlist.PaymentCardListReq
import com.influx.marcus.theatres.api.ApiModels.paymentcardlist.PaymentCardListResp
import com.influx.marcus.theatres.api.ApiModels.savedcards.SavedCardReq
import com.influx.marcus.theatres.api.ApiModels.savedcards.SavedCardResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun getPaymentCardList(req: PaymentCardListReq) {
        val guestCall: Call<PaymentCardListResp> = webApi.paymentLists(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        guestCall.enqueue(object : Callback<PaymentCardListResp> {
            override fun onFailure(call: Call<PaymentCardListResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<PaymentCardListResp>?, response: Response<PaymentCardListResp>?) {
                if (response!!.isSuccessful) {
                    try {
                        result.onSuccess(response.body())
                    } catch (e: Exception) {
                        result.onFailure("2")
                    }
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun getCancelBook(req: CancelBookReq) {
        val guestCall: Call<CancelBookingResp> = webApi.CancelBooking(
                AppConstants.AUTHORISATION_HEADER, req)
        guestCall.enqueue(object : Callback<CancelBookingResp> {
            override fun onFailure(call: Call<CancelBookingResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<CancelBookingResp>?, response: Response<CancelBookingResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun getCardDetail(req: CardDetailReq) {
        val guestCall: Call<CardDetailResp> = webApi.cardDetail(
                AppConstants.AUTHORISATION_HEADER, req)
        guestCall.enqueue(object : Callback<CardDetailResp> {
            override fun onFailure(call: Call<CardDetailResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<CardDetailResp>?, response: Response<CardDetailResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun getSavedCardDetail(req: SavedCardReq) {
        val guestCall: Call<SavedCardResp> = webApi.getSavedCards(
                AppConstants.AUTHORISATION_HEADER, req)
        guestCall.enqueue(object : Callback<SavedCardResp> {
            override fun onFailure(call: Call<SavedCardResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<SavedCardResp>?, response: Response<SavedCardResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun deleteSavedCard(req: DeleteSavedCardReq) {
        val delSavedCardCall: Call<DeleteSavedCardResp> = webApi.deleteThisSavedCard(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        delSavedCardCall.enqueue(object : Callback<DeleteSavedCardResp> {
            override fun onFailure(call: Call<DeleteSavedCardResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<DeleteSavedCardResp>?, response: Response<DeleteSavedCardResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}