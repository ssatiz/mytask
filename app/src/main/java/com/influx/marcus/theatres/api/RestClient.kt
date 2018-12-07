package com.influx.marcus.theatres.api

import com.influx.marcus.theatres.utils.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Kavitha on 02-04-2018.
 */
class RestClient {
    companion object {
        val BASE_URL = AppConstants.KEY_API_BASEURL
        fun getApiClient(): ApiInterface {
            var retrofit: Retrofit
            var apiInterface: ApiInterface
            if (AppConstants.IS_DEBUG) {
                val httpInterceptor = HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                val okhttpClient = OkHttpClient.Builder()
                        .addInterceptor(httpInterceptor)
                        .retryOnConnectionFailure(true)
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)

                        .build()
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okhttpClient)
                        .addConverterFactory(GsonConverterFactory.create())

                        .build()
            } else {
                val okhttpClient = OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .build()
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okhttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            apiInterface = retrofit.create(ApiInterface::class.java)
            return apiInterface
        }

//        fun getApiClient1(): ApiInt {
//            var retrofit: Retrofit
//            var apiInterface: ApiInt
//            if (AppConstants.IS_DEBUG) {
//                val httpInterceptor = HttpLoggingInterceptor()
//                        .setLevel(HttpLoggingInterceptor.Level.BODY)
//                val okhttpClient = OkHttpClient.Builder()
//                        .addInterceptor(httpInterceptor)
//                        .connectTimeout(60, TimeUnit.SECONDS)
//                        .readTimeout(130, TimeUnit.SECONDS)
//                        .writeTimeout(60, TimeUnit.SECONDS)
//                        .addInterceptor(BasicAuthInterceptor("admin","mar22"))
//                        .build()
//                retrofit = Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .client(okhttpClient)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build()
//            } else {
//                retrofit = Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build()
//            }
//            apiInterface = retrofit.create(ApiInt::class.java)
//            return apiInterface
//        }
    }
}