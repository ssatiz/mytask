package com.influx.marcus.theatres.myaccount

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
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchmyaccount(req: MyAccountReq) {
        val fetchmyaccountCall: Call<MyAccountResp> =
                webApi.getMyAccount(AppConstants.AUTHORISATION_HEADER, req)
        fetchmyaccountCall.enqueue(object : Callback<MyAccountResp> {
            override fun onFailure(call: Call<MyAccountResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<MyAccountResp>?, response: Response<MyAccountResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchupdatemyaccount(req: UpdateAccountReq) {
        val updatemyaccounCall: Call<UpdateResponse> =
                webApi.getUpdateAccount(AppConstants.AUTHORISATION_HEADER, req)
        updatemyaccounCall.enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<UpdateResponse>?, response: Response<UpdateResponse>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchCardResponse(req: EnrollCardReq) {
        val CardResponseCall: Call<EnrollCardResp> =
                webApi.getEnrollCardResponse(AppConstants.AUTHORISATION_HEADER, req)
        CardResponseCall.enqueue(object : Callback<EnrollCardResp> {
            override fun onFailure(call: Call<EnrollCardResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<EnrollCardResp>?, response: Response<EnrollCardResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchGiftCardResponse(req: EnrollGiftCardReq) {
        val CardResponseCall: Call<EnrollGiftCardResp> =
                webApi.getEnrollGiftCardResponse(AppConstants.AUTHORISATION_HEADER, req)
        CardResponseCall.enqueue(object : Callback<EnrollGiftCardResp> {
            override fun onFailure(call: Call<EnrollGiftCardResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<EnrollGiftCardResp>?, response: Response<EnrollGiftCardResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }




    fun fetchRewardTransaction(req: RewardTransactionReq) {
        val RewardTransactionCall: Call<RewardTransactionResp> =
                webApi.getRewardTransactions(AppConstants.AUTHORISATION_HEADER, req)
        RewardTransactionCall.enqueue(object : Callback<RewardTransactionResp> {
            override fun onFailure(call: Call<RewardTransactionResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<RewardTransactionResp>?, response: Response<RewardTransactionResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchRewardCardDetail(req: RewardCardDetailReq) {
        val RewardCardDetailCall: Call<RewardCardDetailResp> =
                webApi.getRewardCardDetails(AppConstants.AUTHORISATION_HEADER, req)
        RewardCardDetailCall.enqueue(object : Callback<RewardCardDetailResp> {
            override fun onFailure(call: Call<RewardCardDetailResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<RewardCardDetailResp>?, response: Response<RewardCardDetailResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
    fun fetchGiftCardBalanceResponse(req: GiftCardBalanceReq) {
        val CardResponseCall: Call<GiftCardBalanceResp> =
                webApi.getBalanceGiftCardResponse(AppConstants.AUTHORISATION_HEADER, req)
        CardResponseCall.enqueue(object : Callback<GiftCardBalanceResp> {
            override fun onFailure(call: Call<GiftCardBalanceResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<GiftCardBalanceResp>?, response: Response<GiftCardBalanceResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun deleteGiftCard(req: GiftDeleteReq){
        val delGiftCall: Call<GiftDeleteResp> =
                webApi.removeThisGiftCardByUserID(
                        AppConstants.AUTHORISATION_HEADER,
                        req
                )
        delGiftCall.enqueue(object : Callback<GiftDeleteResp>{
            override fun onFailure(call: Call<GiftDeleteResp>, t: Throwable) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<GiftDeleteResp>, response: Response<GiftDeleteResp>) {
                if (response.isSuccessful) {
                    result.onSuccess(response.body())
                }else{
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchCardList(req: RewardCardsListReq) {
        val RewardCardlistCall: Call<RewardsCardsListResp> =
                webApi.getRewardCardslist(AppConstants.AUTHORISATION_HEADER, req)
        RewardCardlistCall.enqueue(object : Callback<RewardsCardsListResp> {
            override fun onFailure(call: Call<RewardsCardsListResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<RewardsCardsListResp>?, response: Response<RewardsCardsListResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun deleteCard(req: DeleteCardReq) {
        val RewardCardlistCall: Call<DeleteCardResp> =
                webApi.removeLoyaltyCard(AppConstants.AUTHORISATION_HEADER, req)
        RewardCardlistCall.enqueue(object : Callback<DeleteCardResp> {
            override fun onFailure(call: Call<DeleteCardResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<DeleteCardResp>?, response: Response<DeleteCardResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}