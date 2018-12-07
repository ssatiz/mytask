package com.influx.marcus.theatres.theatres

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Rowlist
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatReq
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatResp
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Seatlist
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.seatlayout.SeatLayoutActivity
import com.influx.marcus.theatres.seatlayout.SeatVM
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.unreservedbooking.UnreservedBooking
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CustomScrollView
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.otaliastudios.zoom.ZoomApi
import com.otaliastudios.zoom.ZoomLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_theatre_show_time.*
import kotlinx.android.synthetic.main.expinfo_dialog.view.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TheatreShowTimeActivity : AppCompatActivity() {
    private val context: Context = this
    lateinit var theatresVM: TheatresVM
    lateinit var theatreCode: String
    lateinit var theatreImg: String
    lateinit var theatreName: String
    private var isMovieDetailShown = false
    private lateinit var seatVM: SeatVM
    lateinit var dateInfo: List<TheatreShowtimeResp.DATa.Date>
    private lateinit var currentShowTimeData: TheatreShowtimeResp
    var currentCinemaList: ArrayList<TheatreShowtimeResp.DATa.MovieInfo> = ArrayList()
    private val webApi = RestClient.getApiClient()
    private var cid = ""
    private var ccode = ""
    private var timeStr = ""
    private var sessionId = ""
    private var cname = ""
    private var expimage = ""
    private var imgurl = ""
    private var mname = ""
    private var movieNameStr = ""
    private lateinit var ivLp: TableRow.LayoutParams
    private lateinit var svParent: CustomScrollView
    //private lateinit var svTheatreDetail: CustomScrollView
    private lateinit var tvCloseText: TextView
    private lateinit var viewLineCloseText: View
    private lateinit var ivLoveLeftLp: TableRow.LayoutParams
    private lateinit var ivLoveRightLp: TableRow.LayoutParams


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theatre_show_time)
        theatreObserver()
        initViews()
    }

    fun initViews() {

        if (AppConstants.getString(AppConstants.KEY_THEATRECODE, context).isNotBlank()) {
            theatreCode = AppConstants.getString(AppConstants.KEY_THEATRECODE, context)
            theatreImg = AppConstants.getString(AppConstants.KEY_THEATREIMAGE, context)
            theatreName = AppConstants.getString(AppConstants.KEY_THEATRENAME, context)

        } else {
            val intent = Intent(context, ShowtimeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        viewLineCloseText = findViewById(R.id.viewLineCloseText)
        tvCloseText = findViewById<TextView>(R.id.tvCloseText)
        svParent = findViewById(R.id.svParent)
        //svTheatreDetail = findViewById(R.id.svTheatreDetail)
        ivLoveLeftLp = TableRow.LayoutParams(resources.getDimension(R.dimen._18sdp).toInt(),
                resources.getDimension(R.dimen._18sdp).toInt())
        ivLoveLeftLp.setMargins(8, 8, 0, 8)

        ivLoveRightLp = TableRow.LayoutParams(resources.getDimension(R.dimen._18sdp).toInt(),
                resources.getDimension(R.dimen._18sdp).toInt())
        ivLoveRightLp.setMargins(0, 8, 8, 8)
        ivLp = TableRow.LayoutParams(resources.getDimension(R.dimen._10sdp).toInt(),
                resources.getDimension(R.dimen._10sdp).toInt())
        ivLp.setMargins(4, 4, 4, 4)

        if (!theatreImg.isEmpty()) {
            Picasso.with(context).load(theatreImg).into(ivTheatre, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    loader.visibility = View.GONE
                    ivTheatre.visibility = View.VISIBLE
                }

                override fun onError() {
                    loader.visibility = View.GONE
                }
            })
        }
        tvMovieTitle.setText(theatreName)
        ivInfoMovieDetail.setOnClickListener {
            llMovieDetail.visibility = View.VISIBLE
            llSpace.visibility = View.VISIBLE
            ivInfoMovieDetail.visibility = View.GONE
            tvCloseText.visibility = View.VISIBLE
            viewLineCloseText.visibility = View.VISIBLE
            svParent.scrollTo(0, 0)
            svParent.isEnableScrolling = false
            //svTheatreDetail.isEnableScrolling = true
        }
        tvCloseText.setOnClickListener {
            llMovieDetail.visibility = View.GONE
            llSpace.visibility = View.GONE
            ivInfoMovieDetail.visibility = View.VISIBLE
            ivInfoMovieDetail.setImageDrawable(resources.getDrawable(R.drawable.info))
            tvCloseText.visibility = View.GONE
            viewLineCloseText.visibility = View.GONE
            svParent.isEnableScrolling = true

            if (webview.canGoBack()) {
                webview.goBack()
            }
            //svTheatreDetail.isEnableScrolling = false
        }

        ivBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
        }
        ivLeft.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var tab = viewpagerDate.getCurrentItem()
                try {
                    if (tab > 0) {
                        tab--
                        viewpagerDate.setCurrentItem(tab)
                        //  updateShowTimesForDate(dateInfo[tab].bdate)
                    } else if (tab == 0) {
                        viewpagerDate.setCurrentItem(tab)
                        //updateShowTimesForDate(dateInfo[tab].bdate)
                    }
                } catch (e: Exception) {
                    LogUtils.d("Showtime", "dateinfo uninitialized crash ${e.message}")
                }
            }
        })

        ivRight.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var tab = viewpagerDate.getCurrentItem()
                try {

                    tab++
                    if (dateInfo.size > tab) {
                        viewpagerDate.setCurrentItem(tab)
                        //updateShowTimesForDate(dateInfo[tab].bdate)
                    }
                } catch (e: Exception) {
                    LogUtils.d("Showtime", "dateinfo uninitialized crash ${e.message}")
                }
            }
        })

        viewpagerDate?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                try {
                    updateShowTimesForDate(dateInfo[position].bdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        var theatreShowtimedata = AppConstants.getObject(AppConstants.KEY_THEATRESHOWTIMEOBJ,
                context, TheatreShowtimeResp::class.java as Class<Any>) as? TheatreShowtimeResp
        if (theatreShowtimedata != null) {
            theatresObs.onChanged(theatreShowtimedata)
        } else {
            fetchTheatreDetail(theatreCode)
        }
    }

    private fun updateShowTimesForDate(bdate: String) {
        populateShowTimesData(currentShowTimeData, bdate)
    }

    private fun theatreObserver() {
        theatresVM = ViewModelProviders.of(this).get(TheatresVM::class.java)
        theatresVM.getTheatreDetailData().observe(this, theatresObs)
        theatresVM.getApiErrorData().observe(this, errorObs)
        seatVM = ViewModelProviders.of(this).get(SeatVM::class.java)
        seatVM.getSeatLayoutData().observe(this, seatRespObs)
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
                //todo save data and proceed to seatlayout activity
                AppConstants.putObject(AppConstants.KEY_SEATDATA, t, context)
                val intent = Intent(context, SeatLayoutActivity::class.java)
                intent.putExtra("cid", cid)
                intent.putExtra("ccode", ccode)
                intent.putExtra("sessionid", sessionId)
                intent.putExtra("timestr", timeStr)
                intent.putExtra("mname", mname)
                intent.putExtra("cname", cname)
                intent.putExtra("expimage", expimage)
                intent.putExtra("imgurl", imgurl)
                startActivity(intent)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            }
        }
    }

    private var theatresObs = object : Observer<TheatreShowtimeResp> {
        override fun onChanged(t: TheatreShowtimeResp?) {
            if (t!!.STATUS) {
                theatreWebview(t)
                dateInfo = t!!.DATA.dates
                var datePager = DatePAgerAdapter(dateInfo, this@TheatreShowTimeActivity)
                viewpagerDate.offscreenPageLimit = datePager.count
                viewpagerDate.adapter = datePager
                viewpagerDate.clipToPadding = false
                var dimension = resources.getDimension(R.dimen._70sdp)
                viewpagerDate.setPadding(dimension.toInt(), 0, dimension.toInt(), 0)
                viewpagerDate.setPageMargin(resources.getDimension(R.dimen._15sdp).toInt());
                currentShowTimeData = t!!
                populateShowTimesData(t!!, dateInfo.get(0).bdate)
            } else {
                alert(t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
                UtilsDialog.hideProgress()
            }
        }
    }

    fun theatreWebview(t: TheatreShowtimeResp) {
        //webview.setWebViewClient(MyWebViewClient())
        webview.getSettings().setLoadsImagesAutomatically(true)
        webview.getSettings().setJavaScriptEnabled(true)
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        webview.getSettings().setLoadWithOverviewMode(true)
        webview.getSettings().setUseWideViewPort(true);
        webview.settings.setAllowFileAccess(true)
        webview.settings.setAllowContentAccess(true)

        openWebview(t.DATA.web_view_url)
    }

    private fun populateShowTimesData(t: TheatreShowtimeResp?, bdate: String) {
        val firstDate = bdate
        var tempDataList: ArrayList<TheatreShowtimeResp.DATa.MovieInfo> = ArrayList<TheatreShowtimeResp.DATa.MovieInfo>()
        for (movieItem in t!!.DATA.movies) {
            if (movieItem.first().show_date.equals(firstDate, ignoreCase = true)) {
                tempDataList.add(movieItem.first())
            }
        }

        generateDynamicShowtimeFrom(tempDataList)
        if (tempDataList.isEmpty()) {
            tvNoShowtimes.visibility = View.VISIBLE
            llShowtimesHolder.visibility = View.GONE
        } else {
            tvNoShowtimes.visibility = View.GONE
            llShowtimesHolder.visibility = View.VISIBLE
        }
        UtilsDialog.hideProgress()
    }


    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.no_showtime_available),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }

    private fun fetchTheatreDetail(theatreCode: String) {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val req = TheatreShowtimeRequest(
                    theatreCode,
                    AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM)
            theatresVM.getTheatreDetailResponse(req)
        }
    }

    private fun openWebview(web_view_url: String) {

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                UtilsDialog.showProgressDialog(this@TheatreShowTimeActivity, "")

                if (url.endsWith(".pdf", ignoreCase = true)) {
                    val pdfUrl = "https://docs.google.com/gview?embedded=true&url=" + url
                    view.loadUrl(pdfUrl)
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                UtilsDialog.hideProgress()
                Log.d("URL", "onPageFinished: : $url")
            }
        }

        webview.loadUrl(web_view_url)
    }

    private fun generateDynamicShowtimeFrom(currCinemaList:
                                            ArrayList<TheatreShowtimeResp.DATa.MovieInfo>) {
        llShowtimesHolder.removeAllViews()
        currCinemaList.forEachIndexed { index, eachCinema ->
            val cinemaLayout = LayoutInflater.from(context).inflate(R.layout.row_cinemasshowtime,
                    svParent, false)
            var maintananceFlag = false
            val tvCinemaName = cinemaLayout.findViewById<TextView>(R.id.tvCinemaName)
            val tvMiles = cinemaLayout.findViewById<TextView>(R.id.tvMiles)
            val tvNearby = cinemaLayout.findViewById<TextView>(R.id.tvNearby)
            val ivLocationMiles = cinemaLayout.findViewById<ImageView>(R.id.ivLocationMiles)

            val ivMiles = cinemaLayout.findViewById<ImageView>(R.id.ivLocationMiles)
            val ivPreferredHeart = cinemaLayout.findViewById<ImageView>(R.id.ivPreferred)
            val tblShows = cinemaLayout.findViewById<TableLayout>(R.id.tblShows)
            ivPreferredHeart.visibility = View.GONE
            ivMiles.visibility = View.GONE
            ivLocationMiles.visibility = View.GONE
            tvCinemaName.text = eachCinema.cinemas!!.mname.toUpperCase()
            tblShows.removeAllViews()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val lastExpIndex = eachCinema.cinemas.exp_data.size - 1

            val cinemaName = eachCinema.cinemas.cname
            cname = cinemaName
            mname = eachCinema.cinemas.mname

            eachCinema.cinemas.exp_data.forEachIndexed { indexNest, expData ->
                val expRow = inflater.inflate(R.layout.row_experienceheader, null)
                val ivExp = expRow.findViewById<ImageView>(R.id.ivExperienceLogo)
                val ivInfo = expRow.findViewById<ImageView>(R.id.ivExperienceInfo)
                val tvMoreExp = expRow.findViewById<TextView>(R.id.tvMoreExpDesc)
                val tvExperienceName = expRow.findViewById<TextView>(R.id.tvExpName)
                val ivMoreLogo = expRow.findViewById<ImageView>(R.id.ivIndineLogo)
                val clDineLayout = expRow.findViewById<ConstraintLayout>(R.id.clDineIn)
                val clMaintanance = expRow.findViewById<ConstraintLayout>(R.id.clMaintanance)
                val tvMaintananceDesc = expRow.findViewById<TextView>(R.id.tvMaintananceDesc)
                val ivMaintananceLogo = expRow.findViewById<ImageView>(R.id.ivMaintananceLogo)
                val tvMovieFlag = expRow.findViewById<TextView>(R.id.tvMovieFlag)
                if (expData.movie_flag.isNotBlank()) {
                    tvMovieFlag.setText(expData.movie_flag)
                    tvMovieFlag.visibility = View.VISIBLE
                }
                val cid = eachCinema.cinemas.cid
                val ccode = eachCinema.cinemas.ccode
                val localExpImageStr = expData.exp_image
                movieNameStr = eachCinema.cinemas.mname
                val movieid = eachCinema.cinemas.tmdb_id
                if (expData.maintenance != null) {
                    if (expData.maintenance.desc.isNullOrBlank()) {
                        clMaintanance.visibility = View.GONE
                    } else {
                        clMaintanance.visibility = View.VISIBLE
                        tvMaintananceDesc.setText(expData.maintenance.desc)
                        maintananceFlag = true
                        if (expData.maintenance.icon!!.isNotBlank()) {
                            Picasso.with(context)
                                    .load(expData.maintenance.icon).into(ivMaintananceLogo)
                        } else {
                            ivMaintananceLogo.visibility = View.GONE
                        }
                    }
                }
                if (expData.more_desc != null) {
                    if (expData.more_desc.desc.isNullOrBlank()) {
                        clDineLayout.visibility = View.GONE
                    } else {
                        LogUtils.d("SHOWTIMEADAPTER", "data is ${expData.more_desc.desc}")
                        clDineLayout.visibility = View.VISIBLE
                        tvMoreExp.setText(expData.more_desc.desc)
                        if (expData.more_desc.icon!!.isNotBlank()) {
                            Picasso.with(context)
                                    .load(expData.more_desc.icon).into(ivMoreLogo)
                        } else {
                            ivMoreLogo.visibility = View.GONE
                        }
                    }
                } else {
                    clDineLayout.visibility = View.GONE
                }


                var localVariableExperienceImage = ""
                var localVariableExperienceText = ""


                if (expData.exp_image.isNotBlank()) {
                    Picasso.with(context).load(expData.exp_image).into(ivExp)
                    localVariableExperienceImage = expData.exp_image
                    localVariableExperienceText = ""

                } else {
                    ivExp.visibility = View.GONE
                    if (expData.exp_name.length > 30) {
                        tvExperienceName.textSize = 10f
                    }

                    localVariableExperienceImage = ""
                    localVariableExperienceText = expData.exp_name

                    tvExperienceName.setText(expData.exp_name)
                }
                if (expData.exprience_single_logo.isNotBlank()) {
                    if (expData.exprience_single_logo.equals("0")) {
                        ivExp.getLayoutParams().width = resources.getDimension(R.dimen.imageview_width).toInt()
                        ivExp.requestLayout()

                    }
                }
                ivExp.setOnClickListener {
                    showPopupExperienceInfo(expData)
                }
                if (expData.exp_desc.isNotBlank()) {
                    ivInfo.setOnClickListener {
                        showPopupExperienceInfo(expData)
                    }
                } else {
                    ivInfo.visibility = View.GONE
                }
                expRow.id = index
                tblShows.addView(expRow)
                val tableWidth = tblShows.width
                val parentExpGridview = GridLayout(context)
                parentExpGridview.columnCount = 4
                val params = GridLayout.LayoutParams(GridLayout.spec(
                        GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f))
                parentExpGridview.layoutParams = params
                val lastTimeIndex = expData.session_data.size - 1
                if (!maintananceFlag) {
                    val rootViewParent: LinearLayout = (context as TheatreShowTimeActivity)
                            .findViewById(R.id.llParentShowLayout)

                    expData.session_data.forEachIndexed { timeIndex, sessionData ->
                        val showTimeLayout = inflater.inflate(R.layout.row_showtimeindividual, null)
                        val gridLayoutParams = GridLayout.LayoutParams()
                        gridLayoutParams.setMargins(8, 8, 8, 8)
                        showTimeLayout.layoutParams = gridLayoutParams
                        val tvShowTime = showTimeLayout.findViewById<TextView>(R.id.tvShowTime)
                        tvShowTime.text = sessionData.time
                        tvShowTime.setOnClickListener {
                            movieNameStr = currCinemaList.get(index).cinemas!!.mname
                            if (expData.movie_flag.isNotBlank()) {
                                movieNameStr = movieNameStr + " - " + expData.movie_flag
                            }
                            AppConstants.putString(AppConstants.KEY_EXPERIENCELOGOURL,
                                    localVariableExperienceImage, context)
                            AppConstants.putString(AppConstants.KEY_EXPERIENCETEXT,
                                    localVariableExperienceText, context)

                            AppConstants.putString(AppConstants.KEY_CID,
                                    cid, context)
                            AppConstants.putString(AppConstants.KEY_SCREENNAME,
                                    sessionData.screen, context)
                            AppConstants.putString(AppConstants.KEY_TMDBID,
                                    currCinemaList.get(index).cinemas!!.tmdb_id, context)
                            AppConstants.putString(AppConstants.KEY_SHOWTIME, "theatre_showtime",
                                    context)
                            imgurl = currCinemaList.get(index).cinemas!!.mhimage

                            val innerCinemaName = eachCinema.cinemas!!.cname
                            AppConstants.cinemaclicked = innerCinemaName

                            if (sessionData.is_reserved) {
                                showSeatPreviewPopup(expData, sessionData, cid, ccode, innerCinemaName,
                                        tvShowTime, tblShows, localExpImageStr,
                                        eachCinema.cinemas.mdetails.movie_id)
                            } else {

                                this.cid = cid
                                this.ccode = ccode
                                this.sessionId = sessionId

                                AppConstants.putString(AppConstants.KEY_MOVIECODE,
                                        eachCinema.cinemas.mdetails.movie_id, context)
                                var year = sessionData.bdate.toString()
                                year = year.substring(0, Math.min(year.length, 4))
                                var date = sessionData.number + " " + sessionData.text + " " + year
                                val i = Intent(context, UnreservedBooking::class.java)
                                i.putExtra("moviename", movieNameStr)
                                i.putExtra("cinema", innerCinemaName)
                                i.putExtra("ccode", ccode)
                                i.putExtra("sessionid", sessionData.session_id)
                                i.putExtra("time", sessionData.time)
                                i.putExtra("date", date)
                                i.putExtra("imgurl", imgurl)
                                i.putExtra("expurl", expimage)
                                i.putExtra("cid", cid)
                                i.putExtra("from", "theatres")
                                startActivity(i)
                                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                            }
                            //sessionClicked(expData, sessionData, cid, ccode, cinemaName)
                        }
                        parentExpGridview.addView(showTimeLayout)
                        LogUtils.d("ShowtimeAdapter", "added  -$index - ${sessionData.time} ")
                    }
                }
                LogUtils.d("ShowtimeAdapter", " ${parentExpGridview.childCount} number of elements ")
                tblShows.addView(parentExpGridview)
            }
            llShowtimesHolder.addView(cinemaLayout)
        }
    }

    fun showSeatPreviewPopup(expData: TheatreShowtimeResp.DATa.MovieInfo.Cinemas.ExpData,
                             sessionData: TheatreShowtimeResp.DATa.MovieInfo.Cinemas.ExpData.SessionData,
                             cid: String, ccode: String, cinemaName: String,
                             anchorView: View, tableView: View, localExpImageStr: String, movieid: String) {

        AppConstants.putString(AppConstants.KEY_MOVIECODE, movieid, context)
        AppConstants.putObject(AppConstants.KEY_EXPDATA, expData, context)
        AppConstants.putString(AppConstants.KEY_TIMESTR, sessionData.time_str, context)

        /**
         * Center dialog
         */
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_seatpreview);
        //dialog.getWindow().setLayout(matchParent, matchParent);
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true)

        val btnOpenPanel = dialog.findViewById<RelativeLayout>(R.id.btnOpenPanel)
        val btnBuyTickets = dialog.findViewById<Button>(R.id.btnBuyTickets)
        val panelLayout = dialog.findViewById<LinearLayout>(R.id.llSeatPreviewLayout)
        val tblSeats = dialog.findViewById<TableLayout>(R.id.tblSeats)
        val zoomLayout = dialog.findViewById<ZoomLayout>(R.id.zlParent)
        val topClose = dialog.findViewById<ConstraintLayout>(R.id.clTopClose)
        val bottomClose = dialog.findViewById<ConstraintLayout>(R.id.clBottomClose)
        bottomClose.setOnClickListener {
            dialog.dismiss()
        }
        topClose.setOnClickListener {
            dialog.dismiss()
        }
        btnOpenPanel.setOnClickListener {
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                val req = SeatReq(ccode, cid, sessionData.session_id, sessionData.time_str)
                val seatCall: Call<SeatResp> = webApi.getSeatLayout(
                        AppConstants.AUTHORISATION_HEADER,
                        req
                )
                seatCall.enqueue(object : Callback<SeatResp> {
                    override fun onFailure(call: Call<SeatResp>?, t: Throwable?) {
                        alert(getString(R.string.ohinternalservererror),
                                getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                        UtilsDialog.hideProgress()
                    }

                    override fun onResponse(call: Call<SeatResp>?, response: Response<SeatResp>?) {
                        if (response!!.isSuccessful) {
                            UtilsDialog.hideProgress()
                            val seatResp = response.body()
                            if (seatResp!!.STATUS == true) {
                                try {
                                    panelLayout.visibility = View.VISIBLE
                                    topClose.visibility = View.GONE
                                    val rowList = seatResp!!.DATA.arealist.rowlist as ArrayList<Rowlist>
                                    formTablelayout(rowList, tblSeats, zoomLayout)
                                } catch (e: Exception) {
                                    LogUtils.d("SEATPREIVEW-Exception", e.message!!)
                                    e.printStackTrace()
                                }
                            } else {
                                UtilsDialog.hideProgress()
                                alert(seatResp!!.DATA.message,
                                        getString(R.string.marcus_theatre_title)) {
                                    positiveButton("OK") {
                                        it.dismiss()
                                    }
                                }.show()

                            }
                        }
                    }
                })
            }
        }
        btnBuyTickets.setOnClickListener {
            fetchSeatLayout(ccode, cid, sessionData.session_id, sessionData.time_str,
                    movieNameStr, cname, localExpImageStr, imgurl)
        }
        dialog.show()
    }

    public fun fetchSeatLayout(ccode: String, cid: String, sessionId: String, timeStr: String,
                               mname: String, cname: String, expimage: String, imgurl: String) {
        this.cid = cid
        this.ccode = ccode
        this.sessionId = sessionId
        this.timeStr = timeStr
        this.mname = mname
        this.cname = cname
        this.expimage = expimage
        this.imgurl = imgurl
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val request = SeatReq(ccode, cid, sessionId, timeStr)
            seatVM.fetchSeatLayout(request)
        }
    }

    private fun formTablelayout(rowList: ArrayList<Rowlist>, tblSeats: TableLayout,
                                zlParent: ZoomLayout) {
        tblSeats.removeAllViews()
        doAsync {

            var rowCount = 0
            val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
            lp.setMargins(4, 0, 4, 0)

            for (row in rowList) {

                val tableRow = TableRow(context)
                tableRow.gravity = Gravity.CENTER_HORIZONTAL
                tableRow.layoutParams = lp

                // generateEmpty method makes new empty tv to be added
                tableRow.addView(generateEmptySpaceTV())
                val tvRowNameStart = getRowNameTextView(lp, row.PhysicalName)
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
                    tableRow.addView(ivSeat)
                }

                var tvRowNameEnd = TextView(context)
                tvRowNameEnd.layoutParams = lp
                tvRowNameEnd.textSize = 11f
                tvRowNameEnd.gravity = Gravity.CENTER
                tvRowNameEnd.text = row.PhysicalName
                tvRowNameEnd.textColor = resources.getColor(R.color.white)
                tableRow.addView(generateEmptySpaceTV())
                tableRow.addView(tvRowNameEnd)
                tableRow.addView(generateEmptySpaceTV())
                uiThread {
                    tblSeats.addView(tableRow, rowCount)
                    rowCount++
                }
            }
        }
        dismissDialogAfterOneSec(tblSeats, zlParent)
    }


    private fun showPopupExperienceInfo(expData: TheatreShowtimeResp.DATa.MovieInfo.Cinemas.ExpData) {
        val dialog = Dialog(context)
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.expinfo_dialog, null)
        dialog.setContentView(mDialogView);

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (expData.exp_desc.isNotBlank()) {
            Picasso.with(context)
                    .load(expData.exp_desc)
                    .into(mDialogView.ivExpPopupImg, object : com.squareup.picasso.Callback {
                        override fun onError() {

                        }

                        override fun onSuccess() {
                            dialog.window.setLayout(matchParent, matchParent)
                            dialog.show();
                        }

                        fun onError(ex: Exception) {
                        }
                    })
        } else {
            alert("Image Not Found",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
         //   Toast.makeText(context, "Image Not Found", Toast.LENGTH_SHORT).show()
        }

        mDialogView.ivClosePopup.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun generateEmptySpaceTV(): TextView {
        val tvEmptySpace = TextView(this)
        tvEmptySpace.layoutParams = ivLp
        return tvEmptySpace
    }

    fun getRowNameTextView(tvSeatLp: TableRow.LayoutParams, rowName: String): TextView {
        var tvRowNameStart = TextView(this)
        tvRowNameStart.textSize = 11f
        tvRowNameStart.layoutParams = tvSeatLp
        tvRowNameStart.setPadding(0, 0, 0, 2)
        tvRowNameStart.text = rowName
        tvRowNameStart.gravity = Gravity.CENTER
        tvRowNameStart.textColor = resources.getColor(R.color.white)
        return tvRowNameStart
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

    private fun dismissDialogAfterOneSec(tblSeats: TableLayout,
                                         zlLayout: ZoomLayout) {
        Handler().postDelayed({
            zlLayout.engine.setTransformation(ZoomApi.TRANSFORMATION_CENTER_INSIDE,
                    Gravity.CENTER)
            UtilsDialog.hideProgress()
        }, 1000)
    }

    override fun onBackPressed() {
        val fromdata = AppConstants.getString(AppConstants.KEY_THEATRESHOWTIME, context)
        if (fromdata.equals("payment")) {
            AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "", context)
            val theatreIntent = Intent(context, OurTheatres::class.java)
            theatreIntent.putExtra("back", "home")
            startActivity(theatreIntent)
            finish()
            overridePendingTransition(R.animator.slide_from_right , R.animator.slide_to_left)
        } else {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                super.onBackPressed();
                overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
            }
        }
    }
}
