package com.influx.marcus.theatres.signup

import com.influx.marcus.theatres.api.ApiModels.liststatecity.cityListResp
import com.influx.marcus.theatres.api.ApiModels.liststatecity.pincodeListResp
import com.influx.marcus.theatres.api.ApiModels.liststatecity.statelistResp
import com.influx.marcus.theatres.api.ApiModels.signup.SignupReq
import com.influx.marcus.theatres.api.ApiModels.signup.SignupResp
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyReq
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CallBackResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupRepo(val result: CallBackResult) {

    private val webApi = RestClient.getApiClient()

    fun fetchSignupResp(req: SignupReq) {
        val prefLocationCall: Call<SignupResp> =
                webApi.registration(AppConstants.AUTHORISATION_HEADER,req)
        prefLocationCall.enqueue(object : Callback<SignupResp> {
            override fun onFailure(call: Call<SignupResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<SignupResp>?, response: Response<SignupResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchAllStateList() {
        val stateListCall: Call<statelistResp> = webApi.getStates(AppConstants.AUTHORISATION_HEADER)
        stateListCall.enqueue(object : Callback<statelistResp> {
            override fun onFailure(call: Call<statelistResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<statelistResp>?, response: Response<statelistResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchAllCityList(code: String ) {
        val cityListCall: Call<cityListResp> = webApi.getCity(AppConstants.AUTHORISATION_HEADER,code)
        cityListCall.enqueue(object : Callback<cityListResp> {
            override fun onFailure(call: Call<cityListResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<cityListResp>?, response: Response<cityListResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun fetchPinCodeList(code: String ) {
        val pincodeListCall: Call<pincodeListResp> = webApi.getPincode(AppConstants.AUTHORISATION_HEADER,code)
        pincodeListCall.enqueue(object : Callback<pincodeListResp> {
            override fun onFailure(call: Call<pincodeListResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<pincodeListResp>?, response: Response<pincodeListResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }

    fun upgradeLoyalty(req: UpgradeLoyaltyReq ) {
        val pincodeListCall: Call<UpgradeLoyaltyResp> = webApi.upgradeMemberToLoyalty(AppConstants.AUTHORISATION_HEADER,req)
        pincodeListCall.enqueue(object : Callback<UpgradeLoyaltyResp> {
            override fun onFailure(call: Call<UpgradeLoyaltyResp>?, t: Throwable?) {
                result.onFailure(t!!.message!!)
            }

            override fun onResponse(call: Call<UpgradeLoyaltyResp>?, response: Response<UpgradeLoyaltyResp>?) {
                if (response!!.isSuccessful) {
                    result.onSuccess(response.body())
                } else {
                    result.onFailure("2")
                }
            }
        })
    }
}
