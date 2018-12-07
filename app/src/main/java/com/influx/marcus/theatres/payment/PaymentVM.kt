package com.influx.marcus.theatres.payment

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class PaymentVM : ViewModel(), CallBackResult {

    private val paymentRepo: PaymentRepo
    private val paymentCardListResp: MutableLiveData<PaymentCardListResp> = MutableLiveData()
    private val bookingCancelResp: MutableLiveData<CancelBookingResp> = MutableLiveData()
    private val cardDetailResp: MutableLiveData<CardDetailResp> = MutableLiveData()
    private val savedCardResp: MutableLiveData<SavedCardResp> = MutableLiveData()
    private val deleteSavedCardResp: MutableLiveData<DeleteSavedCardResp> = MutableLiveData()
    private val apiError: MutableLiveData<String> = MutableLiveData()

    init {
        paymentRepo = PaymentRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is PaymentCardListResp) {
            paymentCardListResp.postValue(t)
        } else if (t is CardDetailResp) {
            cardDetailResp.postValue(t)
        } else if (t is CancelBookingResp) {
            bookingCancelResp.postValue(t)
        } else if (t is SavedCardResp) {
            savedCardResp.postValue(t)
        } else if (t is DeleteSavedCardResp) {
            deleteSavedCardResp.postValue(t)
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

    fun getPaymentCardListDetails(): MutableLiveData<PaymentCardListResp> {
        return paymentCardListResp
    }

    fun getPaymentCardList(req: PaymentCardListReq) {
        paymentRepo.getPaymentCardList(req)
    }

    fun getCancelBook(req: CancelBookReq) {
        paymentRepo.getCancelBook(req)
    }

    fun getCancelBookingDetails(): MutableLiveData<CancelBookingResp> {
        return bookingCancelResp
    }

    fun getCardDetail(req: CardDetailReq) {
        paymentRepo.getCardDetail(req)
    }

    fun getCardDetail(): MutableLiveData<CardDetailResp> {
        return cardDetailResp
    }

    fun getSavedCard(req: SavedCardReq) {
        paymentRepo.getSavedCardDetail(req)
    }

    fun getSavedCardDetail(): MutableLiveData<SavedCardResp> {
        return savedCardResp
    }

    fun getDeleteSavedCardResp(): MutableLiveData<DeleteSavedCardResp> {
        return deleteSavedCardResp
    }

    fun deleteThisSavedCard(req: DeleteSavedCardReq) {
        paymentRepo.deleteSavedCard(req)
    }
}