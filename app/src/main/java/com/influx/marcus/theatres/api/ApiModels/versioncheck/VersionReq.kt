package com.influx.marcus.theatres.api.ApiModels.versioncheck


data class VersionReq(
    val app_version: String,
    val app_platform: String,
    val device_name: String,
    val user_id: String
)

data class VersionResp(
        val DATA: DATA,
        val STATUS: Boolean
)

data class DATA(
        val message: String,
        val dev_webservice_url: String,
        val force: String,
        val live_webservice_url: String,
        val maintenance: String,
        val maintenance_image_url: String,
        val maintenance_redirect_url: String,
        val splash: Splash,
        val version: String,
        val web_view_url: String
)

data class Splash(
        val device: String,
        val image_url: String,
        val link: String,
        val seconds: String,
        val type: String
)
