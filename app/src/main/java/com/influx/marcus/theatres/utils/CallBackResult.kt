package com.influx.marcus.theatres.utils

interface CallBackResult {
  fun <T> onSuccess(t:T)
  fun onFailure(error:String)
}
