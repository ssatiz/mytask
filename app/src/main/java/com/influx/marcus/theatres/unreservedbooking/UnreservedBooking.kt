package com.influx.marcus.theatres.unreservedbooking

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.*
import com.influx.marcus.theatres.bookingreview.BookingReview
import com.influx.marcus.theatres.login.LoginScreen
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_unreserved_booking.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.text.DecimalFormat
import java.util.concurrent.CopyOnWriteArrayList

class UnreservedBooking : AppCompatActivity() {

    val context = this@UnreservedBooking
    lateinit var unreserveVM: UnreserveVM
    private var unreservedResp: UnreservedResp? = null
    var size = 0
    val ticketarray = CopyOnWriteArrayList<Ticket>()
    val loyaltyArray = ArrayList<String>()
    var from = ""
    var df = DecimalFormat("0.00")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unreserved_booking)
        unreservedObserver()
        initViews()
    }

    fun initViews() {
        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from")
        }

        val title = intent.extras.getString("moviename")
        val experience = intent.extras.getString("cinema")
        val cid = intent.extras.getString("cid")
        val ccode = intent.extras.getString("ccode")
        val sessionid = intent.extras.getString("sessionid")
        val timestr = intent.extras.getString("time")
        val imgurl = intent.extras.getString("imgurl")
        val expurl = intent.extras.getString("expurl")
        val date = intent.extras.getString("date")
        var exprience_single_logo = "1"
        if (intent.hasExtra("exprience_single_logo")) {
            exprience_single_logo = intent.extras.getString("exprience_single_logo")
        }

        val posterImgUrl = AppConstants.putString(AppConstants.KEY_MOVIEPOSTER, imgurl, this@UnreservedBooking)
        val expImageUrl = AppConstants.putString(AppConstants.KEY_EXPIMAGE, expurl, this@UnreservedBooking)
        val movieName = AppConstants.putString(AppConstants.KEY_MOVIENAME, title, this@UnreservedBooking)
        val cinemaName = AppConstants.putString(AppConstants.KEY_CINEMALOCATION, experience, this@UnreservedBooking)
        AppConstants.putString(AppConstants.KEY_TIME, timestr, this@UnreservedBooking)
        AppConstants.putString(AppConstants.KEY_DATE, date, this@UnreservedBooking)
        AppConstants.putString(AppConstants.KEY_SEATSTR, "N/A", this@UnreservedBooking)
        loyaltyArray.add(AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, context))

        tvTicketCount.setText("No Ticket(s) Selected")
        tvAmount.setText("$0.00")

        if (from.equals("theatres")) {
            val request = UnreservedReq(ccode,
                    cid, sessionid, timestr)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                unreserveVM.unreserveTicket(request)
            }
        } else {
            try {
                unreservedResp = AppConstants.getObject(AppConstants.KEY_UNRESERVEDBOOKING,
                        applicationContext, UnreservedResp::class.java as Class<Any>) as? UnreservedResp
                if (unreservedResp != null) {
                    unReserveObs.onChanged(unreservedResp)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val request = UnreservedReq(ccode,
                        cid, sessionid, timestr)
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    unreserveVM.unreserveTicket(request)
                }
            }
        }
        ivBack.setOnClickListener {
            onBackPressed()
        }
        tvToolbarTitle.setText(title)
        tvTheatreName.setText(experience)
        rvTicketTypes.setHasFixedSize(true)
        rvTicketTypes.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        btContinue.setOnClickListener {
            if (ticketarray.size > 0) {
                Log.i("ticketArraylist", ticketarray.toString())
                if (AppConstants.getString(AppConstants.KEY_USERID, context).isNotEmpty()) {
                    val request = LockUnreservedReq(AppConstants.getString(AppConstants.KEY_USERID, context),
                            AppConstants.getString(AppConstants.KEY_TMDBID, context),
                            AppConstants.getString(AppConstants.KEY_MOVIECODE, context), ccode, cid, sessionid,
                            timestr, ticketarray, loyaltyArray, AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        unreserveVM.lockSeatDetails(request)
                    }
                } else {
                    val listLoyalty = arrayListOf<String>()
                    val unreserveBlockReq = LockUnreservedReq(
                            "invalid",
                            AppConstants.getString(AppConstants.KEY_TMDBID, context),
                            AppConstants.getString(AppConstants.KEY_MOVIECODE, context), ccode, cid, sessionid,
                            timestr, ticketarray, loyaltyArray, AppConstants.APP_VERSION, AppConstants.APP_PLATFORM
                    )

                    AppConstants.putObject(AppConstants.KEY_UNRESERVE_BLOCKREQ, unreserveBlockReq, context)
                    AppConstants.putString(AppConstants.KEY_SESSIONID, sessionid, context)
                    AppConstants.putString(AppConstants.KEY_CCODE, ccode, context)
                    AppConstants.putString(AppConstants.KEY_MOVIEPOSTER, imgurl, context)
                    AppConstants.putString(AppConstants.KEY_EXPIMAGE, expurl, context)
                    AppConstants.putString(AppConstants.KEY_SINGLEEXPIMAGE, exprience_single_logo, context)
                    AppConstants.putString(AppConstants.KEY_CINEMALOCATION, experience, context)
                    AppConstants.putString(AppConstants.KEY_MOVIENAME, title, context)
                    AppConstants.putString(AppConstants.KEY_FROM, "UnreserveBlockseat", context)
                    AppConstants.putBoolean(AppConstants.KEY_BOOKINGFLOW, true, context)
                    Handler().postDelayed({
                        val intent = Intent(this, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                    }, 1000)
                }
            } else {
                alert( "Select tickets to continue",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    private fun unreservedObserver() {
        unreserveVM = ViewModelProviders.of(this).get(UnreserveVM::class.java)
        unreserveVM.getunreserveTicketDetails().observe(this, unReserveObs)
        unreserveVM.getLockSeatDetails().observe(this, lockUnReserveObs)
        unreserveVM.getApiErrorDetails().observe(this, errorObs)
    }

    private var unReserveObs = object : Observer<UnreservedResp> {
        override fun onChanged(t: UnreservedResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                val mAdapter = ticketAdapter(t.DATA, context, ticketlistener)
                size = t.DATA.tickettypes.size
                rvTicketTypes.adapter = mAdapter
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
    private var lockUnReserveObs = object : Observer<LockUnreservedResp> {
        override fun onChanged(t: LockUnreservedResp?) {
            if (t!!.STATUS) {
                AppConstants.putString(AppConstants.KEY_SEATSTR, t.DATA.receipt.seatinfo, this@UnreservedBooking)
                goToLoginScreen(t!!)
            } else {
                UtilsDialog.hideProgress()
                alert( t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }


    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert( getString(R.string.ohinternalservererror), "Marcus Theatres") {
                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    private fun goToLoginScreen(blockSeatResp: LockUnreservedResp) {
        AppConstants.putObject(AppConstants.KEY_BLOCKRESP, blockSeatResp, context)
        Handler().postDelayed({
            val intent = Intent(this, BookingReview::class.java)
            startActivity(intent)
            UtilsDialog.hideProgress()
            finish()
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }, 2000)
    }

    private var ticketlistener = object : ticketAdapter.ticketAdapterListener {

        override fun plus(v: View, position: Int, bookid: String, count: Int,
                          ticketlist: CopyOnWriteArrayList<TicketList>, total: Float) {
            ticketarray.clear()
            for (eachitem in ticketlist) {
                val p = Ticket(eachitem.ticketTypeId, eachitem.quantity)
                ticketarray.add(p)
            }
            if (count == 0) {
                tvTicketCount.setText("No Ticket(s) Selected")
            } else {
                tvTicketCount.setText(count.toString() + " Ticket(s) Selected")
            }
            val Amount = df.format(total)
            tvAmount.setText("$" + Amount.toString())
        }

        override fun minus(v: View, position: Int, bookid: String, count: Int,
                           ticketlist: CopyOnWriteArrayList<TicketList>, total: Float) {
            ticketarray.clear()
            for (eachitem in ticketlist) {
                val p = Ticket(eachitem.ticketTypeId, eachitem.quantity)
                ticketarray.add(p)
            }

            if (count == 0) {
                tvTicketCount.setText("No Ticket(s) Selected")
            } else {
                tvTicketCount.setText(count.toString() + " Ticket(s) Selected")
            }
            val Amount = df.format(total)
            tvAmount.setText("$" + Amount.toString())
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}
