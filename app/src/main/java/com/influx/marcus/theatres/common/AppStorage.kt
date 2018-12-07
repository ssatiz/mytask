package com.influx.marcus.theatres.common

import android.content.Context

/**
 * Created by Kavitha on 11-04-2018.
 */
class AppStorage {
    companion object {
        var DEVICE_TOKEN: String = "device_token"
        var QRID: String = "qrid"
        var ISFOODADD: String = "food"
        var isFromLogin: String = "fromlogin"
        fun putString(key: String, value: String, context: Context): Unit {
            val db: TinyDB = TinyDB(context)
            db.putString(key, value)
        }

        fun getString(key: String, context: Context): String {
            val db: TinyDB = TinyDB(context)
            return db.getString(key)
        }

        fun getBoolean(key: String, context: Context): Boolean {
            return TinyDB(context).getBoolean(key)
        }

        fun putBoolean(key: String, value: Boolean, context: Context): Unit {
            TinyDB(context).putBoolean(key, value)
        }
    }
}