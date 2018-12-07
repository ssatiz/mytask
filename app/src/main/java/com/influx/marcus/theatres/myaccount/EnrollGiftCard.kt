package com.influx.marcus.theatres.myaccount

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookReq
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardReq
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardResp
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardsCardsListResp
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.payment.PaymentScreen
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CounterClass
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_enroll_gift_card.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnrollGiftCard : AppCompatActivity(), CounterClass.CountdownTick {
    private val webApi = RestClient.getApiClient()
    lateinit var myAccountVM: MyAccountVM
    private var context: Context = this@EnrollGiftCard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_gift_card)
        myAccountObserver()
        initViews()
        btAddCard.setOnClickListener {
            validate()
        }
        ivBack.setOnClickListener {
            onBackPressed()
        }
        if (AppConstants.isFromPayment) {
            tvTimer.visibility = View.VISIBLE
            tvTimerTxt.visibility = View.VISIBLE
        }
    }

    private fun initViews() {
        CounterClass.setListener(this)
//        if (AppConstants.getString(AppConstants.KEY_EMAIL, context).isNotBlank()) {
//            etEmailid.setText(AppConstants.getString(AppConstants.KEY_EMAIL, context))
//        }
    }


    private fun myAccountObserver() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getEnrollGiftData().observe(this, enrollCardObs)
//        myAccountVM.getcardListData().observe(this, CardListObs)
        myAccountVM.getApiErrorData().observe(this, errorObs)
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        }
    }

    private var enrollCardObs = object : Observer<EnrollGiftCardResp> {
        override fun onChanged(t: EnrollGiftCardResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                // toast(t.DATA.successMessage)
                alert(t.DATA.successMessage, "Marcus Theatres")
                {
                    positiveButton("OK") { dialog ->
                        if (AppConstants.isFromPayment) {
                            AppConstants.isFromPayment = false
                            val i = Intent(context, PaymentScreen::class.java)
                            i.putExtra("fromGiftEnroll", true)
                            startActivity(i)
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                        } else {
                            val intent = intent
                            AppConstants.putObject(AppConstants.KEY_GIFT_CARD, t, this@EnrollGiftCard)
                            intent.putExtra("giftPinNo", etEmailid.text.toString())
                            //intent.putExtra("")
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                        }
                        dialog.dismiss()
                    }
                }.show().setCancelable(false)


            } else {
                alert(t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    private var CardListObs = object : Observer<RewardsCardsListResp> {
        override fun onChanged(t: RewardsCardsListResp?) {
//            if (t!!.STATUS) {
//                val cardList: ArrayList<CardInfo> = ArrayList()
//                lateinit var loylatyNumber: LoyaltyNo
//                t.DATA.card_info.forEachIndexed { index, eachCard ->
//                    val individualCard = CardInfo(
//                            eachCard.card_name,
//                            eachCard.card_no,
//                            eachCard.card_holder_name,
//                            eachCard.card_image)
//                    cardList.add(individualCard)
//                }
//                System.out.println("cardlistsize" + cardList.size)
//                loylatyNumber = LoyaltyNo(t.DATA.banner, t.DATA.title, cardList)
//                AppConstants.putObject(AppConstants.KEY_CARDLIST, loylatyNumber, this@EnrollGiftCard)
//                AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, loylatyNumber.card_info.get(0).card_no, this@EnrollGiftCard)
//                alert("Card has been added to your account", "Marcus Theatres")
//                {
//                    positiveButton("OK") { dialog ->
//                        UtilsDialog.hideProgress()
//                        dialog.dismiss()
//
//                        val intent = intent
//                        intent.putExtra("Key", "success")
//                        setResult(Activity.RESULT_OK, intent)
//                        finish()
//                    }
//
//                }.show()
//
//            } else {

//            }

        }
    }

    fun validate() {
        inputCardNo.isErrorEnabled = false
        inputEmail.isErrorEnabled = false
        if (etCardNo.text.toString().isBlank()) {
            inputCardNo.isErrorEnabled = true
            inputCardNo.error = "Please enter Card Number"
        } else if (etEmailid.text.toString().isBlank()) {
            inputEmail.isErrorEnabled = true
            inputEmail.error = "Please enter Pin"
        } else {
            val request = EnrollGiftCardReq(etCardNo.text.toString(), etEmailid.text.toString(),
                    AppConstants.getString(AppConstants.KEY_USERID,
                            this@EnrollGiftCard),
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(this@EnrollGiftCard)) {
                UtilsDialog.showProgressDialog(this@EnrollGiftCard, "")
                myAccountVM.getEnrollGiftCardResponse(request)
            }
        }
    }

    override fun onTick(secondsLeft: Long) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        tvTimer.text = CounterClass.getFormatedTime()
        if (secondsLeft < 1) {
            // show timeout
            tvTimer.text = "00:00"
            tvTimer.clearAnimation()
            if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
            if (!isFinishing) {
                //AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME,"payment",context)

                alert("Your session has timed out", "Marcus Theatres")
                {
                    positiveButton("OK") { dialog ->

                        AppConstants.putObject(AppConstants.KEY_SEATDATA, "", context)
                        AppConstants.putObject(AppConstants.KEY_BLOCKRESP, "", context)
                        AppConstants.putObject(AppConstants.KEY_BLOCKREQ, "", context)
                        AppConstants.putObject(AppConstants.KEY_UNRESERVE_BLOCKREQ, "", context)
                        val request = CancelBookReq(AppConstants.getString(AppConstants.KEY_THEATREID, context),
                                AppConstants.getString(AppConstants.KEY_CID, context), AppConstants.getString(AppConstants.KEY_SALEID, context),
                                AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                        if (UtilsDialog.isNetworkStatusAvialable(context)) {
//                            UtilsDialog.showProgressDialog(context, "")
//                            paymentVM.getCancelBook(request)
                        }
                        Log.i("block Resp", AppConstants.getString(AppConstants.KEY_SEATDATA, context))
                        val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME, context)
                        when (fromdata) {
                            "theatre_showtime" -> {
                                val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                                AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "payment", context)
                                intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intentShowtime)
                                finish()
                                overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                                dialog.dismiss()
                            }
                            "movie_showtime" -> {
                                fetchShowTimes()
                            }
                            else -> {
                                fetchShowTimes()
                            }
                        }
                    }
                }.show().setCancelable(false)

            }

        } else if (secondsLeft.toInt() == 90) {
            val v: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(500)
            }
        } else if (secondsLeft < 90) {
            blinkTimerText()
        }
    }

    var isTimerBlinking = false
    private fun blinkTimerText() {
        if (!isTimerBlinking) {
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 150 //You can manage the time of the blink with this parameter
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            tvTimer.startAnimation(anim)
            isTimerBlinking = true
        }
    }
    fun fetchShowTimes() {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val request = ShowtimeRequest(
                    AppConstants.getString(AppConstants.KEY_STATE_CODE, context),
                    AppConstants.getString(AppConstants.KEY_LATITUDE, context),
                    AppConstants.getString(AppConstants.KEY_LONGITUDE, context),
                    AppConstants.getString(AppConstants.KEY_MOVIECODE, context),
                    AppConstants.getString(AppConstants.KEY_TMDBID, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context))
            fetchShowtimeData(request)
        }
    }

    fun fetchShowtimeData(req: ShowtimeRequest) {
        val prefLocationCall: Call<ShowtimeResponse> =
                webApi.getShowtimes(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<ShowtimeResponse> {
            override fun onFailure(call: Call<ShowtimeResponse>?, t: Throwable?) {
                UtilsDialog.hideProgress()
                alert("No Showtimes Available", getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                        val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                        intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentShowtime.putExtra("fromcancelledtxn", true)
                        startActivity(intentShowtime)
                        finish()
                        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)                    }
                }.show()
            }

            override fun onResponse(call: Call<ShowtimeResponse>?, response: Response<ShowtimeResponse>?) {
                val dataitem = response!!.body()
                if (response!!.isSuccessful && dataitem!!.STATUS) {
                    doAsync {
                        AppConstants.putObject(AppConstants.KEY_SHOWTIMERESOBJ, dataitem, context)
                        uiThread {
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intentShowtime.putExtra("prefetcheddata", true)
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            UtilsDialog.hideProgress()
                        }
                    }
                } else {
                    UtilsDialog.hideProgress()
                    alert("No Showtimes Available", getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intentShowtime.putExtra("fromcancelledtxn", true)
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)                        }
                    }.show()
                }
            }
        })
    }
    override fun finish() {
        AppConstants.isFromPayment = false
        super.finish()
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)
    }
}
