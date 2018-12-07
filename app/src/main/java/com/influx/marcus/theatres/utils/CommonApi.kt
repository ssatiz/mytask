package com.influx.marcus.theatres.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatReq
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.home.HomeResponse
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedResp
import com.influx.marcus.theatres.api.ApiModels.updatePref.UpdatePrefReq
import com.influx.marcus.theatres.api.ApiModels.updatePref.resp.UpdatePrefResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.bookingreview.BookingReview
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.myaccount.MyAccountScreen
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommonApi {


    companion object {
        private val webApi = RestClient.getApiClient()


        fun updateUserPreferences(context: Context, userPref: UpdatePrefReq) {
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                val prefCall: Call<UpdatePrefResp> = webApi.updateUsersPreferences(
                        AppConstants.AUTHORISATION_HEADER,
                        userPref
                )
                prefCall.enqueue(object : Callback<UpdatePrefResp> {
                    override fun onFailure(call: Call<UpdatePrefResp>, t: Throwable) {
                        UtilsDialog.hideProgress()
                        context.alert(context.getString(R.string.ohinternalservererror),
                                context.getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                    }

                    override fun onResponse(call: Call<UpdatePrefResp>, response: Response<UpdatePrefResp>) {
                        if (response.isSuccessful) {
                            if (AppConstants.getString(AppConstants.KEY_FLOWFROMMODIFY, context).equals("modify")) {
                                AppConstants.putString(AppConstants.KEY_FLOWFROMMODIFY, "", context)
                                AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "preference", context)
                                val myAccount = Intent(context, MyAccountScreen::class.java)
                                myAccount.putExtra("modify", true)
                                context.startActivity(myAccount)
                                (context as Activity).finish()

                            } else {
                                AppConstants.isFromNavDraw=false
                                val homeIntent = Intent(context, HomeActivity::class.java)
                                context.startActivity(homeIntent)
                                (context as Activity).finish()

                            }

                        } else {
                            UtilsDialog.hideProgress()
                            context.alert(context.getString(R.string.ohinternalservererror),
                                    context.getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()

                        }
                    }
                })

            }
        }

        fun blockThisSeat(context: Context, userId: String) {
            val blockSeatReq: BlockSeatReq = AppConstants.getObject(AppConstants.KEY_BLOCKREQ,
                    context, BlockSeatReq::class.java as Class<Any>) as BlockSeatReq
            blockSeatReq.user_id = userId

            if (UtilsDialog.isNetworkStatusAvialable(context) && !blockSeatReq.user_id.equals("invalid")) {
                UtilsDialog.showProgressDialog(context, "")
                val seatLockCall: Call<BlockSeatResp> = webApi.blockSeat(
                        AppConstants.AUTHORISATION_HEADER,
                        blockSeatReq
                )
                seatLockCall.enqueue(object : Callback<BlockSeatResp> {
                    override fun onFailure(call: Call<BlockSeatResp>?, t: Throwable?) {
                        UtilsDialog.hideProgress()
                        context.alert("Sorry Could not block your selected seat, " +
                                "Please try Again",
                                context.getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()

                    }

                    override fun onResponse(call: Call<BlockSeatResp>?, response: Response<BlockSeatResp>?) {

                        if (response!!.isSuccessful) {
                            val blockSeatResp = response.body()!!
                            if (blockSeatResp.STATUS) {
                                AppConstants.putObject(AppConstants.KEY_BLOCKRESP, blockSeatResp, context)
                                android.os.Handler().postDelayed({
                                    val i = Intent(context, BookingReview::class.java)
                                    UtilsDialog.hideProgress()
                                    context.startActivity(i)
                                }, 1000)
                            } else {
                                context.alert(blockSeatResp.DATA.message,
                                        context.getString(R.string.marcus_theatre_title)) {
                                    positiveButton("OK") {
                                        it.dismiss()
                                    }
                                }.show()
                            }
                        } else {
                            UtilsDialog.hideProgress()
                            context.alert("Sorry Could not block your selected seat, " +
                                    "Please try Again",
                                    context.getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()
                        }
                    }
                })
            } else {
                context.alert("Expired login session, Please signout and signin " +
                        "again to continue",
                        context.getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            }
        }

        fun unReservedSeatLock(context: Context, userId: String) {
            val unReservedlockSeatReq: LockUnreservedReq = AppConstants.getObject(AppConstants.KEY_UNRESERVE_BLOCKREQ,
                    context, LockUnreservedReq::class.java as Class<Any>) as LockUnreservedReq
            unReservedlockSeatReq.user_id = userId
            if (UtilsDialog.isNetworkStatusAvialable(context) &&
                    !unReservedlockSeatReq.user_id.equals("invalid")) {
                UtilsDialog.showProgressDialog(context, "")
                val seatLockCall: Call<LockUnreservedResp> = webApi.lockUnreservedTickets(
                        AppConstants.AUTHORISATION_HEADER,
                        unReservedlockSeatReq
                )
                seatLockCall.enqueue(object : Callback<LockUnreservedResp> {
                    override fun onFailure(call: Call<LockUnreservedResp>?, t: Throwable?) {
                        UtilsDialog.hideProgress()
                        context.alert("Sorry Could not block your selected seat, " +
                                "Please try Again",
                                context.getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()

                    }

                    override fun onResponse(call: Call<LockUnreservedResp>?, response: Response<LockUnreservedResp>?) {

                        if (response!!.isSuccessful) {
                            val blockSeatResp = response.body()!!
                            if (blockSeatResp.STATUS) {
                                AppConstants.putObject(AppConstants.KEY_BLOCKRESP, blockSeatResp, context)
                                val i = Intent(context, BookingReview::class.java)
                                UtilsDialog.hideProgress()
                                context.startActivity(i)
                            } else {
                                context.alert(blockSeatResp.DATA.message,
                                        context.getString(R.string.marcus_theatre_title)) {
                                    positiveButton("OK") {
                                        it.dismiss()
                                    }
                                }.show()
                            }
                        } else {
                            UtilsDialog.hideProgress()
                            context.alert("Sorry Could not block your seat, " +
                                    "Please try Again",
                                    context.getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()

                        }
                    }
                })
            } else {
                context.alert("Expired login session, Please signout and signin " +
                        "again to continue",
                        context.getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }

        fun fetchHomeDataAndStoreLocally(mcontext: Context, request: HomeRequest) {
            val SchedulesByPreferencesCall: Call<HomeResponse> =
                    webApi.getSchedulesByPreferences(AppConstants.AUTHORISATION_HEADER, request)

            SchedulesByPreferencesCall.enqueue(object : Callback<HomeResponse> {
                override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                    LogUtils.d("HomeResponse", " throwable error - ${t.toString()}")
                }

                override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                    if (response!!.isSuccessful) {
                        AppConstants.putObject(AppConstants.KEY_HOMEPAGEDATA, response, mcontext)
                    } else {
                        //do nothing
                    }
                }

            })
        }
    }


}
