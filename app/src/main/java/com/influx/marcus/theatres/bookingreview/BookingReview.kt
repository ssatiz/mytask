package com.influx.marcus.theatres.bookingreview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookReq
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookingResp
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.payment.PaymentScreen
import com.influx.marcus.theatres.payment.PaymentVM
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CounterClass
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_booking_review.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingReview : AppCompatActivity(), CounterClass.CountdownTick {

    lateinit var imgBack: ImageView
    lateinit var txtPay: TextView
    lateinit var ivExp: ImageView
    lateinit var context: Context
    lateinit var rvTickets: RecyclerView
    lateinit var tvCinemaLoc: TextView
    lateinit var tvSeats: TextView
    lateinit var tvTimer: TextView
    lateinit var paymentVM: PaymentVM

    private val webApi = RestClient.getApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_review)
        context = this@BookingReview
        CounterClass.setListener(this@BookingReview)
        PaymentObservers()
        initView()
    }


    override fun onResume() {
        CounterClass.setListener(this)
        LogUtils.d("LOGIN", "Resume ")
        super.onResume()
    }

    private fun initView() {
        tvTimer = findViewById(R.id.tvTimer)
        ivExp = findViewById(R.id.ivCinema)
        tvSeats = findViewById(R.id.tvSeats)
        tvCinemaLoc = findViewById(R.id.tvCinemaname)
        imgBack = findViewById(R.id.ivBack)
        txtPay = findViewById(R.id.tvPay)
        rvTickets = findViewById(R.id.rvTicketTypes)
        rvTickets.setHasFixedSize(true)
        rvTickets.setLayoutManager(LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false))
        txtPay.setOnClickListener({
            val i = Intent(context, PaymentScreen::class.java)
            startActivity(i)
        })

        imgBack.setOnClickListener({
            onBackPressed()
        })

        try {
            val blockresp: BlockSeatResp = AppConstants.getObject(AppConstants.KEY_BLOCKRESP,
                    context, BlockSeatResp::class.java as Class<Any>) as BlockSeatResp
            val blockList = blockresp.DATA
            val orderid = blockresp.DATA.order_id
            val saleid = blockresp.DATA.sale_id
            //   Log.i("saleidin booking review",saleid)
            AppConstants.putString(AppConstants.KEY_SALEID, saleid, context)
            val posterImgUrl = AppConstants.getString(AppConstants.KEY_MOVIEPOSTER, context)
            if (posterImgUrl.isNotBlank()) Picasso.with(context).load(posterImgUrl)
                    .into(ivBookreview, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            loader.visibility = View.GONE
                            ivBookreview.visibility = View.VISIBLE
                        }

                        override fun onError() {
                            loader.visibility = View.GONE
                        }
                    })

            val singleExpImageUrl = AppConstants.getString(AppConstants.KEY_SINGLEEXPIMAGE, context)

            if (singleExpImageUrl.isNotBlank()) {
                if (singleExpImageUrl.equals("0")) {
                    ivExp.getLayoutParams().width = resources.getDimension(R.dimen.imageview_width).toInt()
                    ivExp.requestLayout()
                }
            }

            val expImageUrl = AppConstants.getString(AppConstants.KEY_EXPERIENCELOGOURL, context)
            if (expImageUrl.isNotBlank()) {
                Picasso.with(context).load(expImageUrl).into(ivExp)
            } else {
                ivExp.visibility = View.GONE
                val expName = AppConstants.getString(AppConstants.KEY_EXPERIENCETEXT,
                        context)
                if (expName.isNotBlank()) {
                    tvExperienceName.visibility = View.VISIBLE
                    tvExperienceName.setText(expName)
                }
            }

            tvMovieName.setText(AppConstants.getString(AppConstants.KEY_MOVIENAME, context))
            val cinemaName = AppConstants.getString(AppConstants.KEY_CINEMALOCATION, context) + "-" +
                    AppConstants.getString(AppConstants.KEY_SCREENNAME, context)
            tvCinemaLoc.setText(cinemaName)

            val date = AppConstants.getString(AppConstants.KEY_DATE, context)
            tvDate.setText(date)

            val time = AppConstants.getString(AppConstants.KEY_TIME, context)
            tvTime.setText(time)


            val displayseat = AppConstants.getString(AppConstants.KEY_SEATSTR, context)
            tvSeats.setText(displayseat)

            tvTotalAmount.setText("$" + blockList.receipt.total)
            tvTicketAmount.setText("$" + blockList.receipt.total)
            val mAdapter = TicketTypeAdapter(blockList, context)
            rvTickets.adapter = mAdapter
        } catch (e: Exception) {
            LogUtils.d("BookingReview", "empty data ${e.message} ")
            e.printStackTrace()
        }
        dismissDialogAfterOneSec()
    }

    private fun dismissDialogAfterOneSec() {
        CounterClass.initInstance(420000, 1000)
        CounterClass.getInstance().start()
        CounterClass.setListener(this)
    }

    override fun onBackPressed() {
        alert("Do you want to cancel your booking ?", "Marcus Theatres")
        {
            positiveButton("YES") { dialog ->
                if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()

                AppConstants.putObject(AppConstants.KEY_SEATDATA, "", context)
                AppConstants.putObject(AppConstants.KEY_BLOCKRESP, "", context)
                AppConstants.putObject(AppConstants.KEY_BLOCKREQ, "", context)
                AppConstants.putObject(AppConstants.KEY_UNRESERVE_BLOCKREQ, "", context)
                val request = CancelBookReq(AppConstants.getString(AppConstants.KEY_THEATREID, context),
                        AppConstants.getString(AppConstants.KEY_CID, context), AppConstants.getString(AppConstants.KEY_SALEID, context),
                        AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)

                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    paymentVM.getCancelBook(request)
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
                        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
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
        }.show()
    }


    override fun onTick(secondsLeft: Long) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        tvTimer.text = CounterClass.getFormatedTime()
        LogUtils.d("Timertick", " ${CounterClass.getFormatedTime()}")

        //  tvTimer.text = CounterClass.getFormatedTime()
        if (secondsLeft < 1) {
            // show timeout
            tvTimer.text = "00:00"
            tvTimer.clearAnimation()
            if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
            if (!isFinishing) {
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
                                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
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

    private fun PaymentObservers() {
        paymentVM = ViewModelProviders.of(this).get(PaymentVM::class.java)
        paymentVM.getCancelBookingDetails().observe(this, paymentCancelObs)
        paymentVM.getApiErrorDetails().observe(this, errorObs)
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

    private var paymentCancelObs = object : Observer<CancelBookingResp> {
        override fun onChanged(t: CancelBookingResp?) {
            // toast(t!!)
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
                        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

                    }
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
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
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
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

                        }
                    }.show()
                }
            }
        })
    }

}