package com.influx.marcus.theatres.utils

import android.util.Log


/**
 * Created by Kavitha on 27-02-2018.
 */
class LogUtils {
    companion object {
        fun d(tag: String, message: String) {
            if (AppConstants.IS_DEBUG) {
                Log.d(tag, message)
            }
        }
        fun e(tag: String, message: String) {
            if (AppConstants.IS_DEBUG) {
                Log.e(tag, message)
            }
        }

        fun w(tag: String, message: String) {
            if (AppConstants.IS_DEBUG) {
                Log.w(tag, message)
            }
        }

        fun i(tag: String, message: String) {
            if (AppConstants.IS_DEBUG) {
                Log.i(tag, message)
            }
        }

        fun v(tag: String, message: String) {
            if (AppConstants.IS_DEBUG) {
                Log.v(tag, message)
            }
        }
    }
}