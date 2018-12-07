package com.influx.marcus.theatres.seatlayout

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.fenchtose.tooltip.Tooltip
import com.fenchtose.tooltip.TooltipAnimation
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.SeatNameGroup
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatReq
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.blockseat.SeatDetail
import com.influx.marcus.theatres.api.ApiModels.seatlayout.*
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.bookingreview.BookingReview
import com.influx.marcus.theatres.common.PopoverView
import com.influx.marcus.theatres.common.RecyclerItemClickListener
import com.influx.marcus.theatres.login.LoginScreen
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CounterClass
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_seat_layout.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat


class SeatLayoutActivity : AppCompatActivity(), PopoverView.PopoverViewDelegate {
    var popoverView: PopoverView? = null
    var customTooltip: Tooltip? = null

    private lateinit var seatTbl: TableLayout
    private lateinit var back: ImageView
    private lateinit var tvcontinue: TextView
    private lateinit var sessionArrayList: ArrayList<ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData>
    lateinit var cid: String
    lateinit var ccode: String
    lateinit var timeStr: String
    lateinit var sessionId: String
    lateinit var movieName: String
    lateinit var cinemaName: String
    lateinit var expImageUrl: String
    lateinit var exprience_single_logo: String
    private lateinit var ivBack: ImageView
    private lateinit var ivExpImage: ImageView
    private lateinit var ivLeft: ImageView
    private lateinit var ivRight: ImageView
    private lateinit var vpTime: ViewPager
    private lateinit var tvMovieName: TextView
    private lateinit var tvCinemaLocation: TextView
    private lateinit var ivLandscape: ImageView
    var selectedTicketList: ArrayList<Seatlist> = ArrayList()
    private lateinit var selectSeatLabel: TextView
    private lateinit var tvDate: TextView
    lateinit var context: Context
    private lateinit var seatVM: SeatVM
    private lateinit var imgurl: String
    var isApplytoAll: Boolean = false
    var maxTicketsCount = 0
    var applyToAllTicketTypeName = "invalid"
    var applyToAllTicketTypeCode = "invalid"
    var applyToAllPriceWithTax = ""
    var applyToAllPriceInCents = ""
    lateinit var tvSelectedSeats: TextView
    private var selectedTicketsStringToSubmit = ""
    private lateinit var tvTimeDisplay: TextView
    private lateinit var rlBotton: RelativeLayout
    private lateinit var tvTotalAmount: TextView
    private lateinit var ivLp: TableRow.LayoutParams
    private lateinit var ivLoveLeftLp: TableRow.LayoutParams
    private lateinit var ivLoveRightLp: TableRow.LayoutParams
    private lateinit var leftGradient: View
    private lateinit var rightGradient: View
    private var isTimerBlinking = false
    private var companionMsg = ""
    private var wheelchairMsg = ""
    private var seatResp: SeatResp? = null
    private var seatDetailsList = ArrayList<SeatDetail>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_layout)
        context = this@SeatLayoutActivity
        seatObserver()
        initViews()
    }

    private fun initViews() {
        ivLp = TableRow.LayoutParams(resources.getDimension(R.dimen._18sdp).toInt(),
                resources.getDimension(R.dimen._18sdp).toInt())
        ivLp.setMargins(8, 8, 8, 8)


        ivLoveLeftLp = TableRow.LayoutParams(resources.getDimension(R.dimen._18sdp).toInt(),
                resources.getDimension(R.dimen._18sdp).toInt())
        ivLoveLeftLp.setMargins(8, 8, 0, 8)

        ivLoveRightLp = TableRow.LayoutParams(resources.getDimension(R.dimen._18sdp).toInt(),
                resources.getDimension(R.dimen._18sdp).toInt())
        ivLoveRightLp.setMargins(0, 8, 8, 8)

        tvTotalAmount = findViewById(R.id.tvAmt)
        ivBack = findViewById(R.id.ivBackbtn)
        selectSeatLabel = findViewById(R.id.tvSelectedSeatsLabel)
        ivLeft = findViewById(R.id.ivLeft)
        ivRight = findViewById(R.id.ivRight)
        vpTime = findViewById(R.id.viewpagerTime)
        tvCinemaLocation = findViewById(R.id.tvCinemaLoc)
        tvMovieName = findViewById(R.id.tvMovieTitle)
        ivExpImage = findViewById(R.id.ivExpimg)
        ivLandscape = findViewById(R.id.ivPosterLandscape)
        rlBotton = findViewById(R.id.rrBottomLayout)
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats)
        tvDate = findViewById(R.id.tvDate)
        // tvTimeDisplay = findViewById(R.id.tvTime)
        leftGradient = findViewById(R.id.vredgradientl)
        rightGradient = findViewById(R.id.vredgradientr)
        ivBack.setOnClickListener {
            onBackPressed()
        }

        seatTbl = findViewById(R.id.tblSeats)
        tvcontinue = findViewById(R.id.tvContinue)
        tvcontinue.setOnClickListener {
            UtilsDialog.showProgressDialog(context, "")
            validateAndCheckUserLoggin()
        }
        //  hscrollView.setMinZoom(1.5f, 0)
        // hscrollView.setMaxZoom(7f,0)
        // get session data from appconstants here
        val expData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData =
                AppConstants.getObject(AppConstants.KEY_EXPDATA,
                        context, ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData::class.java
                        as Class<Any>) as ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData
        exprience_single_logo = expData.exprience_single_logo
        sessionArrayList = ArrayList<ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData>()
        seatResp = AppConstants.getObject(AppConstants.KEY_SEATDATA,
                context, SeatResp::class.java as Class<Any>) as SeatResp

        expData.session_data.forEachIndexed { index, sessionData ->
            sessionArrayList.add(sessionData)
        }
        if (intent.hasExtra("cid")) {
            cid = intent.getStringExtra("cid")
        } else {
            cid = ""
        }
        if (intent.hasExtra("ccode")) {
            ccode = intent.getStringExtra("ccode")
        } else {
            ccode = ""
        }
        if (intent.hasExtra("timestr")) {
            timeStr = intent.getStringExtra("timestr")
        } else {
            timeStr = ""
        }
        if (intent.hasExtra("sessionid")) {
            sessionId = intent.getStringExtra("sessionid")
        } else {
            sessionId = ""
        }
        if (intent.hasExtra("cname")) {
            cinemaName = intent.getStringExtra("cname")
            LogUtils.d("SeatLayout", "cinema name in intent $cinemaName")
        } else {
            cinemaName = AppConstants.cinemaclicked
        }
        if (intent.hasExtra("expimage")) {
            expImageUrl = intent.getStringExtra("expimage")
        } else {
            expImageUrl = ""
        }
        if (intent.hasExtra("imgurl")) {
            imgurl = intent.getStringExtra("imgurl")
        } else {
            imgurl = ""
        }
        if (intent.hasExtra("mname")) {
            movieName = intent.getStringExtra("mname")
        } else {
            movieName = ""
        }
        tvMovieName.text = movieName
        if (movieName.length > 30) {
            tvMovieName.textSize = 16f
        } else {
            tvMovieName.textSize = 18f
        }
        tvCinemaLocation.setText(cinemaName)
        if (exprience_single_logo.isNotBlank()) {
            if (exprience_single_logo.equals("0")) {
                ivExpImage.layoutParams.width = resources.getDimension(R.dimen.imageview_widthscaleonehalf).toInt()
                ivExpImage.requestLayout()
            }

        }
        if (expImageUrl.isNotBlank()) {
            Picasso.with(context).load(expImageUrl)
                    .into(ivExpImage, object : Callback {
                        override fun onSuccess() {
                            LogUtils.d("Picaso", "image load success for url $expImageUrl")
                        }

                        override fun onError() {
                            LogUtils.d("Picaso", "Failed to load seatlayout exp image")
                        }
                    })
        } else {
            ivExpImage.visibility = View.GONE
            val expname = AppConstants.getString(AppConstants.KEY_EXPERIENCETEXT, context)
            if (expname.isNotBlank()) {
                tvExperienceName.visibility = View.VISIBLE
                tvExperienceName.setText(expname)
            }
            LogUtils.d("SeatLayout", "experience name - $expname")
        }

        val datePager = TimePagerAdapter(sessionArrayList, this@SeatLayoutActivity)
        vpTime.offscreenPageLimit = datePager.count
        vpTime.adapter = datePager
        LogUtils.d("SeatLayout", "data items count ${datePager.count} ")
        vpTime.clipToPadding = false
        var dimension = resources.getDimension(R.dimen._60sdp)
        vpTime.setPadding(dimension.toInt(), 0, dimension.toInt(), 0)
        vpTime.setPageMargin(18)
        vpTime.setOnPageChangeListener(vpOnPageListener)
        leftGradient.setOnClickListener {
            ivLeft.performClick()
        }
        rightGradient.setOnClickListener {
            ivRight.performClick()
        }
        ivLeft.setOnClickListener {
            if (vpTime.currentItem == 0) {
                //do nothing already first item
            } else {
                val curItem = vpTime.currentItem
                vpTime.currentItem = curItem - 1
            }
        }
        ivRight.setOnClickListener {
            if (vpTime.currentItem == sessionArrayList.size - 1) {
                //do nothing already last item
            } else {
                val curItem = vpTime.currentItem
                vpTime.currentItem = curItem + 1
            }
        }

        if (seatResp != null) {
            sessionArrayList.forEachIndexed { index, eachItem ->
                if (eachItem.time_str.equals(timeStr, ignoreCase = true)) {
                    AppConstants.putString("time", eachItem.time, context)
                    val date = eachItem.bdate
                    val month = date.substring(4, 6).toInt()
                    val monthName = AppConstants.theMonth(month).toUpperCase()
                    val day = date.substring(6)
                    val year = date.substring(0, 4)
                    val formatedDate = "$day  $monthName $year"
                    tvDate.setText(formatedDate)
                    AppConstants.putString("date", formatedDate, context)
                    vpTime.setOnPageChangeListener(null)
                    vpTime.setCurrentItem(index)
                    vpTime.setOnPageChangeListener(vpOnPageListener)
                }//else do nothing
            }
            seatRespObs.onChanged(seatResp)
        } else {
            // call seatlayout for first item
            sessionArrayList.forEachIndexed { index, eachItem ->
                if (eachItem.time_str.equals(timeStr, ignoreCase = true)) {
                    AppConstants.putString("time", eachItem.time, context)
                    val date = eachItem.bdate
                    val month = date.substring(4, 6).toInt()
                    val monthName = AppConstants.theMonth(month).toUpperCase()
                    val day = date.substring(6)
                    val year = date.substring(0, 4)
                    val formatedDate = "$day  $monthName $year"
                    tvDate.setText("$day  $monthName")
                    AppConstants.putString("date", formatedDate, context)
                    if (datePager.count < 2) {
                        fetchSeatLayout(ccode, cid, sessionId, timeStr)
                    }
                    vpTime.setCurrentItem(index)
                }//else do nothing
            }
        }
        if (vpTime.currentItem == 0)
            ivLeft.visibility = View.VISIBLE
    }

    var vpOnPageListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            if (position == 0)
                ivLeft.visibility = View.GONE
            else if (position == vpTime.childCount - 1)
                ivRight.visibility = View.GONE
            else {
                ivRight.visibility = View.VISIBLE
                ivLeft.visibility = View.VISIBLE
            }
            AppConstants.putString("time", sessionArrayList.get(position).time, context)
            selectedTicketList.clear()
            updateUiAndApiData()
            val date = sessionArrayList.get(position).bdate
            val month = date.substring(4, 6).toInt()
            val monthName = AppConstants.theMonth(month).toUpperCase()
            val day = date.substring(6)
            val year = date.substring(0, 4)
            val formatedDate = "$day  $monthName $year"
            tvDate.setText("$day  $monthName")
            AppConstants.putString("date", formatedDate, context)
            timeStr = sessionArrayList.get(position).time_str
            fetchSeatLayout(ccode, cid, sessionId, timeStr)
        }
    }

    private fun validateAndCheckUserLoggin() {
        if (AppConstants.getString(AppConstants.KEY_USERID, context).isNotBlank()) {
            val userid = AppConstants.getString(AppConstants.KEY_USERID, context)
            val listLoyalty = arrayListOf<String>()
            listLoyalty.add(AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, context))
            if (selectedTicketsStringToSubmit.isNotBlank()) {
                val blockReq = BlockSeatReq(
                        userid,
                        AppConstants.getString(AppConstants.KEY_TMDBID, context),
                        AppConstants.getString(AppConstants.KEY_MOVIECODE, context),
                        ccode,
                        cid,
                        sessionId,
                        timeStr,
                        seatDetailsList,
                        selectedTicketsStringToSubmit,
                        listLoyalty,
                        AppConstants.APP_VERSION,
                        AppConstants.APP_PLATFORM
                )
                AppConstants.putObject(AppConstants.KEY_BLOCKREQ, blockReq, context)
                AppConstants.putString(AppConstants.KEY_SESSIONID, sessionId, context)
                AppConstants.putString(AppConstants.KEY_CCODE, ccode, context)
                AppConstants.putString(AppConstants.KEY_MOVIEPOSTER, imgurl, context)
                AppConstants.putString(AppConstants.KEY_EXPIMAGE, expImageUrl, context)
                AppConstants.putString(AppConstants.KEY_SINGLEEXPIMAGE, exprience_single_logo, context)
                AppConstants.putString(AppConstants.KEY_CINEMALOCATION, cinemaName, context)
                AppConstants.putString(AppConstants.KEY_MOVIENAME, movieName, context)
                callBlockSeat(blockReq)
            } else {
                alert("Please Select a Seat to book", "Marcus Theatres") {
                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                        fetchSeatLayout(ccode, cid, sessionId, timeStr)
                    }
                }.show()
            }
        } else {
            doAsync {
                var result = storeSeatDetailValuesToAppConstants()
                uiThread {
                    redirectToLoginScreen()
                }
            }
        }
    }

    private fun redirectToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
    }

    private fun storeSeatDetailValuesToAppConstants() {
        val listLoyalty = arrayListOf<String>()
        val blockReq = BlockSeatReq(
                "invalid",
                AppConstants.getString(AppConstants.KEY_TMDBID, context),
                AppConstants.getString(AppConstants.KEY_MOVIECODE, context),
                ccode,
                cid,
                sessionId,
                timeStr,
                seatDetailsList,
                selectedTicketsStringToSubmit,
                listLoyalty,
                AppConstants.APP_VERSION,
                AppConstants.APP_PLATFORM
        )
        AppConstants.putObject(AppConstants.KEY_BLOCKREQ, blockReq, context)
        AppConstants.putString(AppConstants.KEY_SESSIONID, sessionId, context)
        AppConstants.putString(AppConstants.KEY_CCODE, ccode, context)
        AppConstants.putString(AppConstants.KEY_MOVIEPOSTER, imgurl, context)
        AppConstants.putString(AppConstants.KEY_EXPIMAGE, expImageUrl, context)
        AppConstants.putString(AppConstants.KEY_SINGLEEXPIMAGE, exprience_single_logo, context)
        AppConstants.putString(AppConstants.KEY_CINEMALOCATION, cinemaName, context)
        AppConstants.putString(AppConstants.KEY_MOVIENAME, movieName, context)
        AppConstants.putString(AppConstants.KEY_FROM, "Blockseat", context)
        AppConstants.putBoolean(AppConstants.KEY_BOOKINGFLOW, true, context)
    }

    private fun callBlockSeat(blockReq: BlockSeatReq) {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            seatVM.submitBlockSeatRequest(blockReq)
        }
    }


    private fun formTablelayout(rowList: ArrayList<Rowlist>, tickettypes: List<Tickettype>) {
        tblSeats.removeAllViews()
        doAsync {

            var rowCount = 0
            val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
            lp.setMargins(40,
                    resources.getDimension(R.dimen._4sdp).toInt(),
                    40,
                    resources.getDimension(R.dimen._4sdp).toInt())

            val tvSeatLp = TableRow.LayoutParams(48, 48)
            tvSeatLp.setMargins(8, 8, 8, 8)

            for (row in rowList) {
                val tableRow = TableRow(context)
                tableRow.gravity = Gravity.CENTER_HORIZONTAL
                tableRow.layoutParams = lp

                // generateEmpty method makes new empty tv to be added
                tableRow.addView(generateEmptySpaceTV())

                var tvRowNameStart = TextView(context)
                tvRowNameStart.layoutParams = ivLp
                tvRowNameStart.textSize = 14f
                tvRowNameStart.gravity = Gravity.CENTER_VERTICAL
                tvRowNameStart.text = row.PhysicalName
                tvRowNameStart.textColor = resources.getColor(R.color.white)
                // val tvRowNameStart = getRowNameTextView(tvSeatLp, row)
                tableRow.addView(tvRowNameStart)
                tableRow.addView(generateEmptySpaceTV())



                for (eachSeat in row.seatlist) {
                    val ivSeat = TextView(context)

                    if (eachSeat.SeatStyle.toLowerCase().equals("loveseatleft")) {
                        ivSeat.layoutParams = ivLoveLeftLp
                    } else if (eachSeat.SeatStyle.toLowerCase().equals("loveseatright")) {
                        ivSeat.layoutParams = ivLoveRightLp
                    } else {
                        ivSeat.layoutParams = ivLp
                    }
                    ivSeat.gravity = Gravity.CENTER
                    setupSeatBackground(eachSeat, ivSeat)
                    eachSeat.isSelect = false
                    selectedTicketList.clear()
                    ivSeat.setOnClickListener({ v ->
                        SeatSelectedLogic(eachSeat, ivSeat, v, tickettypes)
                    })
                    tableRow.addView(ivSeat)
                }

                var tvRowNameEnd = TextView(context)
                tvRowNameEnd.layoutParams = ivLp
                tvRowNameEnd.textSize = 14f
                tvRowNameEnd.gravity = Gravity.CENTER_VERTICAL
                tvRowNameEnd.text = row.PhysicalName
                tvRowNameEnd.textColor = resources.getColor(R.color.white)
                tableRow.addView(generateEmptySpaceTV())
                tableRow.addView(tvRowNameEnd)
                tableRow.addView(generateEmptySpaceTV())
                uiThread {
                    seatTbl.addView(tableRow, rowCount)
                    rowCount++
                }

            }
            if (rowCount < 20) {
                val remainingFakeRows = 20 - rowCount// to push seats on top
                for (i in 0..remainingFakeRows) {
                    val tableRow = TableRow(context)
                    tableRow.gravity = Gravity.CENTER_HORIZONTAL
                    tableRow.layoutParams = lp
                    // generateEmpty method makes new empty tv to be added
                    tableRow.addView(generateEmptySpaceTV())
                    tableRow.addView(generateEmptySpaceTV())
                    tableRow.addView(generateEmptySpaceTV())
                    tableRow.addView(generateEmptySpaceTV())
                    tableRow.addView(generateEmptySpaceTV())
                    uiThread {
                        seatTbl.addView(tableRow)
                    }
                }
            } else {
                // do nothing seats already near top edge
            }
        }
        // dismissDialogAfterOneSec()
    }


    override fun onBackPressed() {
        if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
        super.onBackPressed()
    }

    fun generateEmptySpaceTV(): TextView {
        val tvEmptySpace = TextView(this)
        tvEmptySpace.layoutParams = ivLp
        return tvEmptySpace
    }


    private fun SeatSelectedLogic(eachSeat: Seatlist, ivSeat: TextView, v: View, tickettypes: List<Tickettype>) {

        if (eachSeat.Status.equals("Available", ignoreCase = true)) {

            if (eachSeat.isSelect) {
                setupSeatBackground(eachSeat, ivSeat)
                eachSeat.isSelect = false
                ivSeat.text = ""
                if (isApplytoAll) {
                    isApplytoAll = false
                }
                selectedTicketList.remove(eachSeat)
                updateUiAndApiData()
            } else if (isApplytoAll && selectedTicketList.size < maxTicketsCount) {
                eachSeat.isSelect = true

                selectedTicketList.add(eachSeat)
                if (eachSeat.SeatStyle.toLowerCase().equals("loveseatleft")) {
                    ivSeat.setBackgroundResource(R.drawable.sofaleftfill)
                } else if (eachSeat.SeatStyle.toLowerCase().equals("loveseatright")) {
                    ivSeat.setBackgroundResource(R.drawable.sofarightfill)
                } else {
                    ivSeat.setBackgroundResource(R.drawable.red_filled)
                }
                ivSeat.text = applyToAllTicketTypeName.substring(0, 1)
                eachSeat.ticketTypeCode = applyToAllTicketTypeCode
                eachSeat.ticketTypeName = applyToAllTicketTypeName
                eachSeat.priceWithTax = applyToAllPriceWithTax
                eachSeat.PriceInCents = applyToAllPriceInCents
                ivSeat.setTextColor(ContextCompat.getColor(context, R.color.white))
                updateUiAndApiData()
            } else if (selectedTicketList.size < maxTicketsCount) {
                try {
                    /* if (customTooltip != null && customTooltip!!.isShown()) {
                         customTooltip!!.dismiss(true)
                     } else {
                         showCustomTooltip(v,eachSeat, tickettypes)
                     }*/

                    if (popoverView != null && popoverView!!.getVisibility() != View.VISIBLE) {
                        popoverView!!.dissmissPopover(true)
                    } else {
                        showSeatPopup(v, eachSeat, tickettypes)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                alert("Please Select a Seat to book", "Marcus Theatres") {
                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()

            }
        }
    }

    private fun updateUiAndApiData() {
        if (selectedTicketList.size < 1) {
            rlBotton.visibility = View.GONE
            tvTotalAmount.setText("$0.00")
            tvSelectedSeats.setText("")
            selectSeatLabel.setText("SELECTED SEAT")
            selectedTicketsStringToSubmit = ""

        } else {
            rlBotton.visibility = View.VISIBLE
            var totalAmountStr = ""
            var totalAmountFloat = 0.0f
            selectedTicketList.forEachIndexed { index, eachSeat ->
                if (!eachSeat.priceWithTax.isEmpty())
                    totalAmountFloat += eachSeat.priceWithTax.toFloat()
            }
            val df = DecimalFormat()
            df.setMaximumFractionDigits(2)
            df.minimumFractionDigits = 2
            val formatedTotalamt = df.format(totalAmountFloat)
            totalAmountStr = "$$formatedTotalamt"
            tvTotalAmount.setText(totalAmountStr)
            /*  val hashSet = HashSet<Seatlist>()
              hashSet.addAll(selectedTicketList)
              selectedTicketList.clear()
              selectedTicketList.addAll(hashSet)*/
            var selectedSeatsDisplay = selectedTicketList.get(0).Name
            if (selectedTicketList.size > 1) {
                for (i in 1..selectedTicketList.size - 1) {
                    selectedSeatsDisplay = selectedSeatsDisplay + ", " + selectedTicketList.get(i).Name
                }
            }
            if (selectedTicketList.size > 9 && selectedTicketList.size < 20) {
                tvSelectedSeats.textSize = 10f
            } else {
                tvSelectedSeats.textSize = 14f
            }
            if (selectedTicketList.size > 1) {
                selectSeatLabel.setText("SELECTED SEATS")
            } else {
                selectSeatLabel.setText("SELECTED SEAT")
            }

            tvSelectedSeats.text = selectedSeatsDisplay
            AppConstants.putString(AppConstants.KEY_SEATSTR, selectedSeatsDisplay, context)

            var selectedSeatTOSend = selectedTicketList.get(0).VistaPassingValue + "|" + selectedTicketList.get(0).ticketTypeCode +
                    "|" + selectedTicketList.get(0).PriceInCents
            if (selectedTicketList.size > 1) {
                for (i in 1..selectedTicketList.size - 1) {
                    selectedSeatTOSend = selectedSeatTOSend + "," + selectedTicketList.get(i).VistaPassingValue + "|" +
                            selectedTicketList.get(i).ticketTypeCode + "|" + selectedTicketList.get(i).PriceInCents
                }
            }
            selectedTicketsStringToSubmit = selectedSeatTOSend

            var localSeatNameGroup: ArrayList<SeatNameGroup> = ArrayList()
            seatDetailsList.clear()
            selectedTicketList.forEachIndexed { index, seatlist ->
                localSeatNameGroup.add(SeatNameGroup(seatlist.ticketTypeName, seatlist.Name))
                seatDetailsList.add(SeatDetail(seatlist.Name, seatlist.ticketTypeName))
            }
            AppConstants.putObjectList(AppConstants.KEY_SEATSOBJECT, localSeatNameGroup as ArrayList<Any>, context)
            //AppConstants.putObject(AppConstants.KEY_SEATSOBJECT, localSeatNameGroup, context)

        }

    }


    fun fetchSeatLayout(ccode: String, cid: String, sessionId: String, timeStr: String) {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val request = SeatReq(ccode, cid, sessionId, timeStr)
            seatVM.fetchSeatLayout(request)
        }

    }

    private fun setupSeatBackground(eachSeat: Seatlist, ivSeat: TextView) {

        if (eachSeat.Status.equals("Available", ignoreCase = true)) {
            when (eachSeat.SeatStyle.toLowerCase()) {
                "normal" -> { //space
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                "loveseatright" -> {// unselected
                    ivSeat.setBackgroundResource((R.drawable.sofaright))
                }
                "loveseatleft" -> {//selecte
                    ivSeat.setBackgroundResource((R.drawable.sofaleft))
                }
                "companion" -> {//occupied
                    ivSeat.setBackgroundResource((R.drawable.companion_seat_icon))
                }
                "wheelchair" -> {//companion
                    ivSeat.setBackgroundResource((R.drawable.wheelchair_seat_icon))
                }
                "sold" -> {
                    ivSeat.setBackgroundResource((R.drawable.selected_seat_grey))
                }
                "available" -> {
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                else -> {
                    ivSeat.setBackgroundResource((R.drawable.selected_seat_grey))
                }
            }
        } else if (eachSeat.Status.equals("sold", ignoreCase = true)) {
            ivSeat.setBackgroundResource((R.drawable.selected_seat_grey))
        } else {
            ivSeat.setBackgroundResource((R.drawable.black_filled))
        }
    }

    private fun seatObserver() {
        seatVM = ViewModelProviders.of(this).get(SeatVM::class.java)
        seatVM.getApiErrorDetail().observe(this, apiErrorObs)
        seatVM.getSeatLayoutData().observe(this, seatRespObs)
        seatVM.getBlockSeatResult().observe(this, seatLockObs)
    }

    private var seatLockObs = object : Observer<BlockSeatResp> {
        override fun onChanged(t: BlockSeatResp?) {
            if (t!!.STATUS) {
                goToLoginScreen(t!!)
            } else {
                UtilsDialog.hideProgress()
                alert(t!!.DATA.message, "Marcus Theatres") {
                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                        fetchSeatLayout(ccode, cid, sessionId, timeStr)
                    }
                }.show()
            }
        }
    }

    private fun goToLoginScreen(blockSeatResp: BlockSeatResp) {
        doAsync {
            AppConstants.putObject(AppConstants.KEY_BLOCKRESP, blockSeatResp, context)
            uiThread {
                UtilsDialog.hideProgress()
                val intent = Intent(this@SeatLayoutActivity, BookingReview::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            }
        }
    }

    private var seatRespObs = object : Observer<SeatResp> {
        override fun onChanged(t: SeatResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == false) {
                alert(t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            } else {
                val rowList = t.DATA.arealist.rowlist as ArrayList<Rowlist>
                maxTicketsCount = t.DATA.arealist.maxTickets.toInt()
                wheelchairMsg = t.DATA.arealist.WheelchairMsg
                companionMsg = t.DATA.arealist.CampanionMsg
                formTablelayout(rowList, t.DATA.arealist.tickettypes)
            }
        }
    }

    private var apiErrorObs = object : Observer<String> {
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

    private fun showCustomTooltip(anchor: View, eachSeat: Seatlist,
                                  tickettypes: List<Tickettype>) {
        val root: RelativeLayout = findViewById(R.id.parent_layout)
        val popoverView = layoutInflater.inflate(R.layout.popup_seat, root, false)

        customTooltip = Tooltip.Builder(this)
                .anchor(anchor, Tooltip.TOP)

                .animate(TooltipAnimation(TooltipAnimation.SCALE_AND_FADE, 50, true))
                .autoAdjust(true)
                .withPadding(3)
                .content(popoverView)
                .cancelable(false)
                .withTip(Tooltip.Tip(20, 20, Color.RED, 2))
                .into(root)
                .debug(true)

                .show()
        //customTooltip.dismiss()
        var popupHeight = resources.getDimension(R.dimen._480sdp)
        if (tickettypes.size < 4) {
            if (eachSeat.SeatStyle.equals("wheelchair", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._202sdp)
            } else if (eachSeat.SeatStyle.equals("companion", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._214sdp)
            } else {
                popupHeight = resources.getDimension(R.dimen._172sdp)
            }

        } else if (tickettypes.size < 7 && tickettypes.size > 3) {
            if (eachSeat.SeatStyle.equals("wheelchair", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._282sdp)
            } else if (eachSeat.SeatStyle.equals("companion", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._296sdp)
            } else {
                popupHeight = resources.getDimension(R.dimen._252sdp)
            }

        }
        val width: Int = resources.getDimension(R.dimen._296sdp).toInt()
        val height = popupHeight.toInt()

        /* popoverView!!.contentSizeForViewInPopover = Point(width, height)
         popoverView!!.delegate = this
         popoverView!!.showPopoverFromRectInViewGroup(root, PopoverView.getFrameForView(tvSeat),
                 PopoverView.PopoverArrowDirectionDown, true)*/
        val txtSeatName = popoverView!!.findViewById<TextView>(R.id.txtWhositting) as TextView
        val checkApply = popoverView!!.findViewById<CheckBox>(R.id.checlApplyall) as CheckBox
        val textApply = popoverView!!.findViewById<TextView>(R.id.txtApply) as TextView
        val textCompanionMsg = popoverView!!.findViewById<TextView>(R.id.txtCompanion) as TextView
        if (eachSeat.SeatStyle.equals("wheelchair", ignoreCase = true)) {
            textCompanionMsg.setText("* $wheelchairMsg")
        } else if (eachSeat.SeatStyle.equals("companion", ignoreCase = true)) {
            textCompanionMsg.setText("* $companionMsg")
        }
        val rvTicketType = popoverView!!.findViewById<RecyclerView>(R.id.rvTicketTypes)
                as RecyclerView
        txtSeatName.text = getString(R.string.txt_who_sitting) + " " + eachSeat.Name
        textApply.setOnClickListener {
            if (checkApply.isChecked) {
                checkApply.isChecked = false
            } else {
                checkApply.isChecked = true
            }
        }
        checkApply.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (checkApply.isChecked) {
                    checkApply.setBackgroundResource(R.drawable.checked_checkbox)
                    isApplytoAll = true
                } else {
                    checkApply.setBackgroundResource(R.drawable.unchecked_checkbox)
                    isApplytoAll = false
                }
            }
        })
        val ivSeat = anchor as TextView
        rvTicketType.setLayoutManager(GridLayoutManager(this, 3))
        rvTicketType.setHasFixedSize(true);
//        rvTicketType.addItemDecoration(GridSpacingItemDecoration(3, 5,
//                true, 0))
        val ticAdapter = TicketTypeAdapter(context, tickettypes)
        rvTicketType.adapter = ticAdapter

        rvTicketType.addOnItemTouchListener(RecyclerItemClickListener(context,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        applyToAllTicketTypeCode = tickettypes[position].TicketTypeCode
                        applyToAllTicketTypeName = tickettypes[position].Description
                        //val pricewithtax = tickettypes[position].Price.toFloat() + tickettypes[position].Tax.toFloat()
                        val pricewithtax = tickettypes[position].Price.toFloat()
                        applyToAllPriceWithTax = pricewithtax.toString()
                        applyToAllPriceInCents = tickettypes[position].PriceInCents
                        eachSeat.isSelect = true
                        if (isApplytoAll) {
                            eachSeat.isApplyToAllSeat = true
                        }
                        selectedTicketList.add(eachSeat)
                        ivSeat.setBackgroundResource(R.drawable.red_filled)
                        ivSeat.text = applyToAllTicketTypeName.substring(0, 1)
                        eachSeat.ticketTypeCode = applyToAllTicketTypeCode
                        eachSeat.ticketTypeName = applyToAllTicketTypeName
                        eachSeat.PriceInCents = applyToAllPriceInCents
                        eachSeat.priceWithTax = pricewithtax.toString() + "0"
                        ivSeat.setTextColor(ContextCompat.getColor(context, R.color.white))
                        updateUiAndApiData()
                        customTooltip!!.dismiss(true)
                    }
                }
        ))
        //content.findViewById<TextView>(R.id.dismiss_button).setOnClickListener(View.OnClickListener { customTooltip.dismiss(true) })
    }


    fun showSeatPopup(tvSeat: View, eachSeat: Seatlist,
                      tickettypes: List<Tickettype>) {
        val rootView: RelativeLayout = findViewById(R.id.parent_layout)

        popoverView = PopoverView(context, R.layout.popup_seat)
        //val constraintPopupParent = popoverView.findViewById<ConstraintLayout>(R.id.constraintLayoutPopupParent)

        var popupHeight = resources.getDimension(R.dimen._480sdp)
        if (tickettypes.size < 4) {
            if (eachSeat.SeatStyle.equals("wheelchair", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._167sdp)
            } else if (eachSeat.SeatStyle.equals("companion", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._191sdp)
            } else {
                popupHeight = resources.getDimension(R.dimen._151sdp)
            }

        } else if (tickettypes.size < 7 && tickettypes.size > 3) {
            if (eachSeat.SeatStyle.equals("wheelchair", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._207sdp)
            } else if (eachSeat.SeatStyle.equals("companion", ignoreCase = true)) {
                popupHeight = resources.getDimension(R.dimen._232sdp)
            } else {
                popupHeight = resources.getDimension(R.dimen._191sdp)
            }

        }
        val width: Int = resources.getDimension(R.dimen._252sdp).toInt()
        val height = popupHeight.toInt()
        Log.w("popuheigh", eachSeat.SeatStyle + height)
        popoverView!!.contentSizeForViewInPopover = Point(width, height)
        popoverView!!.delegate = this
        popoverView!!.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(tvSeat),
                PopoverView.PopoverArrowDirectionDown, true)
        // popoverView!!.setPadding(0,0,20,0)
        val txtSeatName = popoverView!!.findViewById<TextView>(R.id.txtWhositting) as TextView
        val checkApply = popoverView!!.findViewById<CheckBox>(R.id.checlApplyall) as CheckBox
        val textApply = popoverView!!.findViewById<TextView>(R.id.txtApply) as TextView
        val textCompanionMsg = popoverView!!.findViewById<TextView>(R.id.txtCompanion) as TextView
        if (eachSeat.SeatStyle.equals("wheelchair", ignoreCase = true)) {
            textCompanionMsg.setText("* $wheelchairMsg")
        } else if (eachSeat.SeatStyle.equals("companion", ignoreCase = true)) {
            textCompanionMsg.setText("* $companionMsg")
        }
        val rvTicketType = popoverView!!.findViewById<RecyclerView>(R.id.rvTicketTypes)
                as RecyclerView
        txtSeatName.text = getString(R.string.txt_who_sitting) + " " + eachSeat.Name
        textApply.setOnClickListener {
            if (checkApply.isChecked) {
                checkApply.isChecked = false
            } else {
                checkApply.isChecked = true
            }
        }
        checkApply.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (checkApply.isChecked) {
                    checkApply.setBackgroundResource(R.drawable.checked_checkbox)
                    isApplytoAll = true
                } else {
                    checkApply.setBackgroundResource(R.drawable.unchecked_checkbox)
                    isApplytoAll = false
                }
            }
        })
        val ivSeat = tvSeat as TextView
        rvTicketType.setLayoutManager(GridLayoutManager(this, 3))
        rvTicketType.setHasFixedSize(true);
//        rvTicketType.addItemDecoration(GridSpacingItemDecoration(3, 5,
//                true, 0))
        val ticAdapter = TicketTypeAdapter(context, tickettypes)
        rvTicketType.adapter = ticAdapter

        rvTicketType.addOnItemTouchListener(RecyclerItemClickListener(context,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        applyToAllTicketTypeCode = tickettypes[position].TicketTypeCode
                        applyToAllTicketTypeName = tickettypes[position].Description
                        //val pricewithtax = tickettypes[position].Price.toFloat() + tickettypes[position].Tax.toFloat()
                        val pricewithtax = tickettypes[position].Price.toFloat()
                        applyToAllPriceWithTax = pricewithtax.toString() + "0"
                        applyToAllPriceInCents = tickettypes[position].PriceInCents

                        eachSeat.isSelect = true
                        if (isApplytoAll) {
                            eachSeat.isApplyToAllSeat = true
                        }
                        selectedTicketList.add(eachSeat)

                        if (eachSeat.SeatStyle.toLowerCase().equals("loveseatleft")) {
                            ivSeat.setBackgroundResource(R.drawable.sofaleftfill)
                        } else if (eachSeat.SeatStyle.toLowerCase().equals("loveseatright")) {
                            ivSeat.setBackgroundResource(R.drawable.sofarightfill)
                        } else {
                            ivSeat.setBackgroundResource(R.drawable.red_filled)
                        }
                        ivSeat.text = applyToAllTicketTypeName.substring(0, 1)
                        eachSeat.ticketTypeCode = applyToAllTicketTypeCode
                        eachSeat.ticketTypeName = applyToAllTicketTypeName
                        eachSeat.priceWithTax = pricewithtax.toString() + "0"
                        eachSeat.PriceInCents = applyToAllPriceInCents
                        ivSeat.setTextColor(ContextCompat.getColor(context, R.color.white))
                        updateUiAndApiData()
                        popoverView!!.dissmissPopover(true)
                    }
                }
        ))
    }

    override fun popoverViewWillShow(view: PopoverView?) {
        Log.i("POPOVER", "Will show")
    }

    override fun popoverViewDidShow(view: PopoverView?) {
        Log.i("POPOVER", "Did show")
    }

    override fun popoverViewWillDismiss(view: PopoverView?) {
        Log.i("POPOVER", "Will dismiss")
    }

    override fun popoverViewDidDismiss(view: PopoverView?) {
        Log.i("POPOVER", "Did dismiss")
    }
/*
    override fun onTick(secondsLeft: Long) {

        tvTimeDisplay.text = CounterClass.getFormatedTime()
        LogUtils.d("Timertick", " ${CounterClass.getFormatedTime()}")

        if (secondsLeft < 1) {
            // show timeout
            tvTimeDisplay.text = "00:00 LEFT"
            tvTimeDisplay.clearAnimation()
            if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
            if (!isFinishing) {
                alert("Your session has timed out", "Marcus Theatres")
                {
                    positiveButton("OK") { dialog ->
                        startActivity(Intent(context, ShowtimeActivity::class.java))
                        finish()
                        dialog.dismiss()
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

    }*/
}