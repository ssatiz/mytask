package com.influx.marcus.theatres.api.ApiModels.forgotpassword


data class ForgotPasswordResp(
        val STATUS: Boolean,
        val DATA: FORGOTDATA
) {

    data class FORGOTDATA(
            val message: String,
            val messageDetail: String
    )
}