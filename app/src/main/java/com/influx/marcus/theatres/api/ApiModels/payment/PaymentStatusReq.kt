package com.influx.marcus.theatres.api.ApiModels.payment

/**
 * Created by Kavitha on 04-04-2018.
 */
class PaymentStatusReq(var UniqueId: String, var UserId: String, var Amount: String,
                       var Status: String) {
}