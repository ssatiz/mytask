package com.influx.marcus.theatres.api.ApiModels.forgotpassword


data class ForgotPasswordReq(
        val email: String,
        val app_version: String,
        val app_platform: String
)



