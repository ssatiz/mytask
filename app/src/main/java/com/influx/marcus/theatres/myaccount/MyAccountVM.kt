package com.influx.marcus.theatres.myaccount

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardReq
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardResp
import com.influx.marcus.theatres.api.ApiModels.enrollcard.EnrollCardReq
import com.influx.marcus.theatres.api.ApiModels.enrollcard.EnrollCardResp
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardReq
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardResp
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceReq
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceResp
import com.influx.marcus.theatres.api.ApiModels.giftdelete.GiftDeleteReq
import com.influx.marcus.theatres.api.ApiModels.giftdelete.GiftDeleteResp
import com.influx.marcus.theatres.api.ApiModels.myaccount.MyAccountReq
import com.influx.marcus.theatres.api.ApiModels.myaccount.MyAccountResp
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateAccountReq
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateResponse
import com.influx.marcus.theatres.api.ApiModels.rewardcarddetail.RewardCardDetailReq
import com.influx.marcus.theatres.api.ApiModels.rewardcarddetail.RewardCardDetailResp
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardCardsListReq
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardsCardsListResp
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardTransactionReq
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardTransactionResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class MyAccountVM : ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var myAccountRepo: MyAccountRepo
    private var MyAccountResp: MutableLiveData<MyAccountResp> = MutableLiveData()
    private var UpdateAccountResp: MutableLiveData<UpdateResponse> = MutableLiveData()
    private var rewardTransactionResp: MutableLiveData<RewardTransactionResp> = MutableLiveData()
    private var rewardCardDetailResp: MutableLiveData<RewardCardDetailResp> = MutableLiveData()
    private var EnrollResp: MutableLiveData<EnrollCardResp> = MutableLiveData()
    private var enrollGiftResp: MutableLiveData<EnrollGiftCardResp> = MutableLiveData()
    private var balanceData: MutableLiveData<GiftCardBalanceResp> = MutableLiveData()
    private var cardListResp: MutableLiveData<RewardsCardsListResp> = MutableLiveData()
    private var deleteCardResp: MutableLiveData<DeleteCardResp> = MutableLiveData()
    private var deleteGiftCardResp: MutableLiveData<GiftDeleteResp> = MutableLiveData()

    init {
        myAccountRepo = MyAccountRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is MyAccountResp) {
            MyAccountResp.postValue(t)
        } else if (t is UpdateResponse) {
            UpdateAccountResp.postValue(t)
        } else if (t is EnrollCardResp) {
            EnrollResp.postValue(t)
        } else if (t is RewardCardDetailResp) {
            rewardCardDetailResp.postValue(t)
        } else if (t is RewardTransactionResp) {
            rewardTransactionResp.postValue(t)
        } else if (t is RewardsCardsListResp) {
            cardListResp.postValue(t)
        } else if (t is DeleteCardResp) {
            deleteCardResp.postValue(t)
        } else if (t is EnrollGiftCardResp) {
            enrollGiftResp.postValue(t)
        } else if (t is GiftCardBalanceResp) {
            balanceData.postValue(t)
        }else if (t is GiftDeleteResp){
            deleteGiftCardResp.postValue(t)
        } else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getMyAccountResponse(req: MyAccountReq) {
        myAccountRepo.fetchmyaccount(req)
    }

    fun getMyAccountData(): MutableLiveData<MyAccountResp> {
        return MyAccountResp
    }

    fun getCardListResponse(req: RewardCardsListReq) {
        myAccountRepo.fetchCardList(req)
    }

    fun getcardListData(): MutableLiveData<RewardsCardsListResp> {
        return cardListResp
    }

    fun getUpdateAccountResponse(req: UpdateAccountReq) {
        myAccountRepo.fetchupdatemyaccount(req)
    }

    fun getUpdateAccountData(): MutableLiveData<UpdateResponse> {
        return UpdateAccountResp
    }

    fun getEnrollCardResponse(req: EnrollCardReq) {
        myAccountRepo.fetchCardResponse(req)
    }

    fun getEnrollGiftCardResponse(req: EnrollGiftCardReq) {
        myAccountRepo.fetchGiftCardResponse(req)
    }



    fun getEnrollData(): MutableLiveData<EnrollCardResp> {
        return EnrollResp
    }

    fun getEnrollGiftData(): MutableLiveData<EnrollGiftCardResp> {
        return enrollGiftResp
    }

    fun getBalanceGiftCardResponse(req: GiftCardBalanceReq) {
        myAccountRepo.fetchGiftCardBalanceResponse(req)
    }

    fun getBalanceGiftData(): MutableLiveData<GiftCardBalanceResp> {
        return balanceData
    }

    fun getRewardTransactionResponse(req: RewardTransactionReq) {
        myAccountRepo.fetchRewardTransaction(req)
    }

    fun getRewardTransactionData(): MutableLiveData<RewardTransactionResp> {
        return rewardTransactionResp
    }

    fun getRewardCardDetailResponse(req: RewardCardDetailReq) {
        myAccountRepo.fetchRewardCardDetail(req)
    }

    fun getRewardCardDetailData(): MutableLiveData<RewardCardDetailResp> {
        return rewardCardDetailResp
    }

    fun getDeleteDetailData(): MutableLiveData<DeleteCardResp> {
        return deleteCardResp
    }

    fun getDeleteDetailResponse(req: DeleteCardReq) {
        myAccountRepo.deleteCard(req)
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }

    fun getGiftCardDeleteResp():MutableLiveData<GiftDeleteResp>{
        return deleteGiftCardResp
    }

    fun deleteThisGiftCard(req: GiftDeleteReq){
        myAccountRepo.deleteGiftCard(req)
    }
}
