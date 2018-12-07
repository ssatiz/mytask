package com.influx.marcus.theatres.api.ApiModels.specials

import java.io.Serializable

data class SpecialResp(
    val STATUS: Boolean,
    val DATA: DATA
):Serializable

data class DATA(
    val specials: ArrayList<Special>,
    val promotion_types: List<PromotionType>
):Serializable

data class Special(
    val id: String,
    val title: String,
    val promotion_type: String,
    val promotion_image: String,
    val webView:String
):Serializable

data class PromotionType(
    val title: String,
    var favourite: Boolean = false
):Serializable