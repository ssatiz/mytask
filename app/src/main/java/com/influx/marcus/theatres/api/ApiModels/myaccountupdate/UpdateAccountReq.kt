package com.influx.marcus.theatres.api.ApiModels.myaccountupdate


data class UpdateAccountReq(
    val userid: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val mobile: String,
    val img_url: String,
    val gender: String,
    val dob: String,
    val decrpt_pwd: String,
    val remove_facebook: String,
    val remove_google: String,
    val newsletter: String,
    val marcusOnfacebook: String,
    val app_version: String,
    val app_platform: String
)