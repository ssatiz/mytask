package com.influx.marcus.theatres.signup

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.influx.marcus.theatres.api.ApiModels.liststatecity.cityListResp
import com.influx.marcus.theatres.api.ApiModels.liststatecity.pincodeListResp
import com.influx.marcus.theatres.api.ApiModels.liststatecity.statelistResp
import com.influx.marcus.theatres.api.ApiModels.signup.SignupReq
import com.influx.marcus.theatres.api.ApiModels.signup.SignupResp
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyReq
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyResp
import com.influx.marcus.theatres.utils.CallBackResult
import com.influx.marcus.theatres.utils.LogUtils

class SignupVm: ViewModel(), CallBackResult {
    private var isApiError: MutableLiveData<String> = MutableLiveData()
    private var signupRepo: SignupRepo
    private var signupResp: MutableLiveData<SignupResp> = MutableLiveData()
    private var stateListResp: MutableLiveData<statelistResp> = MutableLiveData()
    private var cityListResp: MutableLiveData<cityListResp> = MutableLiveData()
    private var pincodeListResp: MutableLiveData<pincodeListResp> = MutableLiveData()
    private var upgradeResp : MutableLiveData<UpgradeLoyaltyResp> = MutableLiveData()


    init {
        signupRepo = SignupRepo(this)
    }

    override fun <T> onSuccess(t: T) {
        if (t is SignupResp) {
            signupResp.postValue(t)
        }else if(t is statelistResp){
            stateListResp.postValue(t)
        }else if(t is cityListResp){
            cityListResp.postValue(t)
        }else if(t is pincodeListResp){
            pincodeListResp.postValue(t)
        }
        else if(t is UpgradeLoyaltyResp){
            upgradeResp.postValue(t)
        }
        else {
            LogUtils.d("Preference", "type not defined")
        }
    }

    override fun onFailure(error: String) {
        isApiError.postValue(error)
    }

    fun getSignupResponse(req: SignupReq) {
        signupRepo.fetchSignupResp(req)
    }

    fun getSignupRespData(): MutableLiveData<SignupResp> {
        return  signupResp
    }

    fun getstateResp() {
        signupRepo.fetchAllStateList()
    }

    fun getStateRespData(): MutableLiveData<statelistResp> {
        return  stateListResp
    }

    fun getUpgradeResp(req: UpgradeLoyaltyReq) {
        signupRepo.upgradeLoyalty(req)
    }

    fun getUpgradeRespData(): MutableLiveData<UpgradeLoyaltyResp> {
        return  upgradeResp
    }

    fun getCityResp( code: String) {
        signupRepo.fetchAllCityList(code)
    }

    fun getCityRespData(): MutableLiveData<cityListResp> {
        return cityListResp
    }

    fun getZipcodeResp( code: String) {
        signupRepo.fetchPinCodeList(code)
    }

    fun getZipcodeRespData(): MutableLiveData<pincodeListResp> {
        return pincodeListResp
    }

    fun getApiErrorData(): MutableLiveData<String> {
        return isApiError
    }
}
