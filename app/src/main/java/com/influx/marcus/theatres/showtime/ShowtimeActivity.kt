package com.influx.marcus.theatres.showtime

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.influx.marcus.theatres.BuildConfig
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.NearCinemas.NearCinemasRequest
import com.influx.marcus.theatres.api.ApiModels.home.Banner
import com.influx.marcus.theatres.api.ApiModels.home.ComingSoon
import com.influx.marcus.theatres.api.ApiModels.home.EventCinema
import com.influx.marcus.theatres.api.ApiModels.home.NowShowing
import com.influx.marcus.theatres.api.ApiModels.myaccount.MovieYoulikeItem
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Rowlist
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatReq
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatResp
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Seatlist
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.DATA
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDReq
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RTomatoResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RtomatoReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedResp
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.bookingreview.BookingReview
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.seatlayout.SeatLayoutActivity
import com.influx.marcus.theatres.seatlayout.SeatVM
import com.influx.marcus.theatres.unreservedbooking.UnreserveVM
import com.influx.marcus.theatres.unreservedbooking.UnreservedBooking
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CustomScrollView
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.otaliastudios.zoom.ZoomApi
import com.otaliastudios.zoom.ZoomLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_showtime.*
import kotlinx.android.synthetic.main.expinfo_dialog.view.*
import org.apmem.tools.layouts.FlowLayout
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.influx.marcus.theatres.widget.ExpandableGridView


class ShowtimeActivity : AppCompatActivity() {

    private lateinit var llShowtimeHolder: LinearLayout
    private lateinit var llMovieDetail: LinearLayout
    private lateinit var showtimeVM: ShowtimeVM
    private lateinit var context: Context
    private lateinit var movieDetailNS: NowShowing
    private lateinit var movieDetailCS: ComingSoon
    private lateinit var movieDetailsEC: EventCinema
    private lateinit var movieDetailULike: MovieYoulikeItem
    private lateinit var scrollParent: CustomScrollView
    private lateinit var scrollMovieDetails: CustomScrollView
    private lateinit var ivPlayTrailer: ImageView
    private var dataName = "invalid"
    private var youtubeTrailerUrl = "error"
    private var youtubeApiKey = BuildConfig.YOUTUBE_API
    private var isMovieDetailShown = false
    private lateinit var castFlowLayout: FlowLayout
    private lateinit var directorFlowLayout: FlowLayout
    private var font: Typeface? = null
    private val tvSize = 12
    private lateinit var constrainLayoutPrt: ConstraintLayout
    private lateinit var constraintSet: ConstraintSet
    private lateinit var tomatoScore: String
    private var rtScore = "NA"
    lateinit var dateInfo: List<ShowtimeResponse.DATa.Date>
    lateinit var movies: List<List<ShowtimeResponse.DATa.MovieInfo>>
    private lateinit var showsAdapter: ShowTimesAdapter
    private lateinit var etSearchCinemaName: EditText
    private lateinit var currentShowTimeData: ShowtimeResponse
    var currentCinemaList: ArrayList<ShowtimeResponse.DATa.MovieInfo> = ArrayList()
    lateinit var viewpagerDate: ViewPager
    private var movieNameStr = ""
    var tmdbid: String = ""
    private lateinit var gradLeft: View
    private lateinit var gradRight: View
    private lateinit var seatVM: SeatVM
    lateinit var unreserveVM: UnreserveVM
    private var cid = ""
    private var ccode = ""
    private var timeStr = ""
    private var sessionId = ""
    private var cname = ""
    private var expimage = ""
    private var exprience_single_logo = ""
    private var localexprience_single_logo = ""
    private var imgurl = ""
    private var mname = ""
    private lateinit var noShowtime: TextView
    private lateinit var ivBack: ImageView
    private var currentListSizeLimit = 5
    private lateinit var searchLayout: ConstraintLayout
    private lateinit var clViewMore: ConstraintLayout
    private lateinit var btnViewMore: Button
    private val webApi = RestClient.getApiClient()
    private lateinit var ivLp: TableRow.LayoutParams
    private var cinemaArraylist = ArrayList<String>()
    private var isNearBy = false
    private var isFromCancelledTransaction = false
    private var KEY_CANCELLEDTXN = "fromcancelledtxn"
    var size = 0
    var cinemaNameStr = ""
    var sessionDataStr: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData? = null
    private lateinit var llParentShowtime: LinearLayout
    private var isGPSLayout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showtime)
        if (intent.hasExtra(KEY_CANCELLEDTXN)) {
            isFromCancelledTransaction = intent.getBooleanExtra(KEY_CANCELLEDTXN, false)
        }

        LogUtils.d("SHOWTIME-isfromcancel", " is from cancellation " + isFromCancelledTransaction)
        context = this@ShowtimeActivity
        if (AppConstants.getString(AppConstants.KEY_LATITUDE, context).isNotBlank()) {
            isGPSLayout = true
        }
        showtimeobserver()
        initViews()
        val movieType: String = AppConstants.getString(AppConstants.KEY_MOVIETYPE, context)
        when (movieType) {
            "comingsoon" -> {
                val comingSoonData: ComingSoon = AppConstants.getObject(AppConstants.KEY_MOVIEDETAILSOBJECT,
                        context, ComingSoon::class.java as Class<Any>) as ComingSoon
                movieDetailCS = comingSoonData
                populateMovieDetails(movieDetailCS)
            }
            "nowshowing" -> {
                val nowShowingData: NowShowing = AppConstants.getObject(AppConstants.KEY_MOVIEDETAILSOBJECT,
                        context, NowShowing::class.java as Class<Any>) as NowShowing
                movieDetailNS = nowShowingData
                populateMovieDetails(movieDetailNS)
            }
            "eventcinema" -> {
                val eventCinemaData: EventCinema = AppConstants.getObject(AppConstants.KEY_MOVIEDETAILSOBJECT,
                        context, EventCinema::class.java as Class<Any>) as EventCinema
                movieDetailsEC = eventCinemaData
                populateMovieDetails(movieDetailsEC)
            }
            "moviesulike" -> {
                val movieULikeItem: MovieYoulikeItem = AppConstants.getObject(AppConstants.KEY_MOVIEDETAILSOBJECT,
                        context, MovieYoulikeItem::class.java as Class<Any>) as MovieYoulikeItem
                movieDetailULike = movieULikeItem
                populateMovieDetails(movieDetailULike)
            }
            "banner" -> {
                val banner: Banner = AppConstants.getObject(AppConstants.KEY_MOVIEDETAILSOBJECT,
                        context, Banner::class.java as Class<Any>) as Banner
                tmdbid = banner.tMDBId
                fetchMovieDetailsForBannerMovies(tmdbid)
            }
            else -> {
                LogUtils.d("Showtime", "underfined movietype found")
            }
        }
        var isPreloaded = false
        if (intent.hasExtra("prefetcheddata")) {
            isPreloaded = intent.getBooleanExtra("prefetcheddata", false)
        }
        if (isPreloaded) {
            val showTimesResp = AppConstants.getObject(AppConstants.KEY_SHOWTIMERESOBJ, context
                    , ShowtimeResponse::class.java as Class<Any>) as ShowtimeResponse?
            ShowtimeObs.onChanged(showTimesResp)
        } else {
            fetchShowtimes()
        }
        unreservedObserver()

    }


    private fun initViews() {

        UtilsDialog.showProgressDialog(context, "")
        llParentShowtime = findViewById(R.id.llParentShowLayout)
        llShowtimeHolder = findViewById(R.id.llShowtimesHolder)
        llMovieDetail = findViewById(R.id.llMovieDetail)
        scrollParent = findViewById(R.id.svParent)
        scrollMovieDetails = findViewById(R.id.svMovidedata)
        ivPlayTrailer = findViewById(R.id.ivPlayTrailer)
        noShowtime = findViewById(R.id.tvNoShowtimes)
        searchLayout = findViewById(R.id.searchLayout)
        btnViewMore = findViewById<Button>(R.id.btnViewMore)
        clViewMore = findViewById(R.id.clViewmore)
        ivBack = findViewById(R.id.ivBack)
        btnViewMore.setOnClickListener {
            currentListSizeLimit = currentListSizeLimit + 5
            refreshShowTimes()
        }

        ivLp = TableRow.LayoutParams(resources.getDimension(R.dimen._10sdp).toInt(),
                resources.getDimension(R.dimen._10sdp).toInt())
        ivLp.setMargins(4, 4, 4, 4)
        editCinema.setOnDismissListener(onDismissListeneretCinema)
        editCinema.setSingleLine()

        ivBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
        }

        ivPlayTrailer.setOnClickListener {
            playYoutubeTrailer()
        }

        btnBookNow.setOnClickListener {
            btnBookNow.visibility = View.GONE
            isMovieDetailShown = false
            hideMovieDetailsLayout()
            editCinema.dismissDropDown()
            scrollParent.isEnableScrolling = true
            ivInfoMovieDetail.visibility = View.VISIBLE
            tvclose.visibility = View.GONE
            ivInfoMovieDetail.setImageDrawable(resources.getDrawable(R.drawable.info))
        }

        /*  editCinema.setOnClickListener {
              editCinema.showDropDown()
          }*/
        ivInfoMovieDetail.setOnClickListener {
            scrollParent.scrollTo(0, 0)

            scrollParent.isEnableScrolling = false
            scrollMovieDetails.isEnableScrolling = true
            showMovieDetailsLayout()
            tvclose.visibility = View.VISIBLE
            ivInfoMovieDetail.visibility = View.GONE
            isMovieDetailShown = true

        }
        tvclose.setOnClickListener {
            isMovieDetailShown = false
            scrollParent.scrollTo(0, 0)
            hideMovieDetailsLayout()
            editCinema.dismissDropDown()
            scrollParent.isEnableScrolling = true
            scrollMovieDetails.isEnableScrolling = false
            ivInfoMovieDetail.visibility = View.VISIBLE
            tvclose.visibility = View.GONE
            ivInfoMovieDetail.setImageDrawable(resources.getDrawable(R.drawable.info))
        }
        icClose.setOnClickListener {
            editCinema.setText("")
            editCinema.dismissDropDown()
            isNearBy = false
            val showTimesResp = AppConstants.getObject(AppConstants.KEY_SHOWTIMERESOBJ, context
                    , ShowtimeResponse::class.java as Class<Any>) as ShowtimeResponse?
            ShowtimeObs.onChanged(showTimesResp)
            icClose.visibility = View.GONE
        }

        editCinema.isFocusable = false
        editCinema.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                editCinema.setFocusableInTouchMode(true);
                return false;
            }
        })
        editCinema.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            editCinema.setText(selected)
            editCinema.setSelection(editCinema.getText().toString().trim().length);
            if (editCinema.text.toString().length > 1) {
                editCinema.isFocusable = false
                for (eachCinema in movies) {
                    LogUtils.i("whole cinema name", eachCinema.first().cinemas!!.cname)
                    if (eachCinema.first().cinemas!!.cname.equals(selected)) {
                        val lat = eachCinema.first().cinemas!!.lat
                        val long = eachCinema.first().cinemas!!.long
                        isNearBy = true
                        icClose.visibility = View.VISIBLE
                        val req = NearCinemasRequest(AppConstants.getString(AppConstants.KEY_TMDBID, context), lat, long, AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                        if (UtilsDialog.isNetworkStatusAvialable(context)) {
                            UtilsDialog.showProgressDialog(context, "")
                            showtimeVM.fetchNearbyShowTime(req)
                        }
                        LogUtils.i("lat long", selected + lat + "   " + long)
                    }
                }
            } else {
                //do nothing
            }
        }

        font = ResourcesCompat.getFont(this, R.font.gotham_book)
        gradLeft = findViewById(R.id.vredgradientl)
        gradLeft.setOnClickListener { ivLeft.performClick() }
        gradRight = findViewById(R.id.vredgradientr)
        gradRight.setOnClickListener { ivRight.performClick() }
        castFlowLayout = findViewById(R.id.flCastData)
        directorFlowLayout = findViewById(R.id.flDirectorData)
        viewpagerDate = findViewById(R.id.viewpagerDate)
        castFlowLayout.removeAllViews()
        directorFlowLayout.removeAllViews()
        constraintSet = ConstraintSet()

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
                ivLeft.visibility = View.VISIBLE
                ivRight.visibility = View.VISIBLE
                if (position == 0)
                    ivLeft.visibility = View.GONE
                else if (position == viewpagerDate.childCount - 1)
                    ivRight.visibility = View.GONE

                try {
                    currentListSizeLimit = 5
                    updateShowTimesForDate(dateInfo[position].bdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    /**
     * This method will relayout showtimes according to new current cinemalistsize
     */
    private fun refreshShowTimes() {
        generateDynamicShowtimeFrom(currentCinemaList)
    }

    /**
     * this method will filter remaining data and set showtimes adapter
     */
    private fun updateShowTimesForDate(bdate: String) {
        populateShowTimesData(currentShowTimeData, bdate)
    }


    private fun filterCinemaListByText(cinemaName: String) {
        var moviesList: ArrayList<ShowtimeResponse.DATa.MovieInfo> = ArrayList()
        for (eachItem in currentCinemaList) {
            if (eachItem.cinemas!!.cname.contains(cinemaName, ignoreCase = true)) {
                moviesList.add(eachItem)
            }//else do nothing
        }

        generateDynamicShowtimeFrom(moviesList)
        if (currentCinemaList.isEmpty()) {
            noShowtime.visibility = View.VISIBLE
            llShowtimeHolder.visibility = View.GONE
        } else {
            noShowtime.visibility = View.GONE
            llShowtimeHolder.visibility = View.VISIBLE
        }
    }


    private fun showMovieDetailsLayout() {
        // get device hieght - movietop heigh = height for movie detail
        btnViewMore.visibility = View.GONE
        btnBookNow.visibility = View.VISIBLE
        searchLayout.visibility = View.GONE
        llShowtimeHolder.visibility = View.GONE
        tvNoShowtimes.visibility = View.GONE
        llMovieDetail.visibility = View.VISIBLE
    }


    private fun hideMovieDetailsLayout() {
        btnViewMore.visibility = View.VISIBLE
        searchLayout.visibility = View.VISIBLE
        llShowtimeHolder.visibility = View.VISIBLE
        if (currentCinemaList.isEmpty()) {
            tvNoShowtimes.visibility = View.VISIBLE
        }
        llMovieDetail.visibility = View.GONE
    }


    private fun playYoutubeTrailer() {
        if (youtubeTrailerUrl.equals("error", ignoreCase = true)) {
            ivPlayTrailer.visibility = View.GONE
        } else {
            try {
                val youtubeId = AppConstants.extractYTId(youtubeTrailerUrl)
                val intent = YouTubeStandalonePlayer.createVideoIntent(this, youtubeApiKey, youtubeId)
                startActivity(intent)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            } catch (e: Exception) {

                ivPlayTrailer.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }


    private fun populateMovieDetails(movieDetailData: Any) {

        if (movieDetailData is ComingSoon) {
            populateComingSoonData(movieDetailData)
        } else if (movieDetailData is EventCinema) {
            populateEventCinemaData(movieDetailData)
        } else if (movieDetailData is NowShowing) {
            populateNowShowingData(movieDetailData)
        } else if (movieDetailData is ShowtimeMDResp) {
            populateBannerMovieDetail(movieDetailData)
        } else if (movieDetailData is MovieYoulikeItem) {
            populateNowShowingData(getNowShowingFromMoviesYouLike(movieDetailData))
        } else {
            LogUtils.d("Showtime", " default case when populate moviedetails")
        }
    }

    private fun getNowShowingFromMoviesYouLike(movieDetailData: MovieYoulikeItem): NowShowing {
        val MovieInNSFormat = NowShowing(
                movieDetailData.tMDBId,
                movieDetailData.cinema_id,
                movieDetailData.theatre_code,
                movieDetailData.movie_id,
                movieDetailData.movie_code,
                movieDetailData.long_name,
                movieDetailData.short_name,
                movieDetailData.name,
                movieDetailData.movie_slug,
                movieDetailData.desc,
                movieDetailData.length,
                movieDetailData.rating,
                movieDetailData.ratingReasons,
                movieDetailData.dDDFlag.toString(),
                movieDetailData.starring,
                movieDetailData.genre,
                movieDetailData.language,
                movieDetailData.director as List<String>,
                "",
                movieDetailData.trailerUrl,
                movieDetailData.releaseDate,
                movieDetailData.releaseDateStr,
                movieDetailData.image_url,
                movieDetailData.h_image_url,
                // movieDetailData.experiences as Experience,
                movieDetailData.caution
        )
        return MovieInNSFormat
    }

    private fun populateBannerMovieDetail(movieDetailData: ShowtimeMDResp) {
        val movieData = movieDetailData.DATA

        if (movieData.caution != null) {
            if (movieData.caution.isNotBlank()) {
                showCautionPopup(movieData.caution)
            }
        }

        movieNameStr = movieData.name
        getRottenTomatoScoreFor(movieData.name)
        imgurl = movieData.h_image_url
        if (movieData.h_image_url.isNotBlank()) {
            Picasso.with(context).load(movieData.h_image_url)
                    .into(ivTrailer, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            loader.visibility = View.GONE
                            ivTrailer.visibility = View.VISIBLE
                        }

                        override fun onError() {
                            loader.visibility = View.GONE
                        }
                    })
        }
        youtubeTrailerUrl = movieData.trailerUrl
        showPlayIcon(youtubeTrailerUrl)
        try {
            tvMovieTitle.setText(movieData.name)
            if (movieData.name.length > 20) {
                tvMovieTitle.textSize = 12f
            }
            var movieGenreStr: String = ""
            if (movieData.genre.size > 0) {
                movieGenreStr = "${movieData.language} - ${movieData.genre.get(0)}"
            } else {
                movieGenreStr = movieData.language
            }
            if (movieData.genre.size > 1) {
                for (i in 1..movieData.genre.size - 1) {
                    movieGenreStr = movieGenreStr + ", " + movieData.genre.get(i)
                }
            }

            if (movieGenreStr.length > 30) {
                tvMovieTitle.textSize = 12f
            }
            tvLanguageGenre.setText(movieGenreStr)
            tvDuration.setText(movieData.length)
            tvSynopsisData.setText(movieData.desc)
            tvRating.setText(movieData.rating)
            //tvRottenTomatoScore.setText(rtScore)
            val params = FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT
            )
            dynamicFlowLayoutGenratorBanner(movieData)
            LogUtils.d("Showtime", "data in flowlayout ${castFlowLayout.childCount}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun populateEventCinemaData(movieDetailData: EventCinema) {
        if (movieDetailData.caution != null) {
            if (movieDetailData.caution.isNotBlank()) {
                showCautionPopup(movieDetailData.caution)
            }
        }
        getRottenTomatoScoreFor(movieDetailData.name)
        movieNameStr = movieDetailData.name
        imgurl = movieDetailData.h_image_url
        if (imgurl.isNotBlank()) {
            Picasso.with(context).load(imgurl)
                    .into(ivTrailer, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            loader.visibility = View.GONE
                            ivTrailer.visibility = View.VISIBLE
                        }

                        override fun onError() {
                            loader.visibility = View.GONE
                        }
                    })
        }
        youtubeTrailerUrl = movieDetailData.trailerUrl
        showPlayIcon(youtubeTrailerUrl)
        try {
            tvMovieTitle.setText(movieDetailData.name)
            if (movieDetailData.name.length > 34) {
                tvMovieTitle.textSize = 12f
            }
            var movieGenreStr: String = ""
            if (movieDetailData.genre.size > 0) {
                movieGenreStr = "${movieDetailData.language} - ${movieDetailData.genre.get(0)}"
            } else {
                movieGenreStr = movieDetailData.language
            }
            if (movieDetailData.genre.size > 1) {
                for (i in 1..movieDetailData.genre.size - 1) {
                    movieGenreStr = movieGenreStr + ", " + movieDetailData.genre.get(i)
                }
            }

            if (movieGenreStr.length > 30) {
                tvMovieTitle.textSize = 12f
            }
            tvLanguageGenre.setText(movieGenreStr)
            tvDuration.setText(movieDetailData.length)
            tvSynopsisData.setText(movieDetailData.desc)
            tvRating.setText(movieDetailData.rating)
            // tvRottenTomatoScore.setText(rtScore)
            val params = FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT
            )
            dynamicFlowLayoutGenratorEventCinema(movieDetailData)
            LogUtils.d("Showtime", "data in flowlayout ${castFlowLayout.childCount}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun populateNowShowingData(movieDetailData: NowShowing) {
        if (movieDetailData.caution != null) {
            if (movieDetailData.caution.isNotBlank()) {
                showCautionPopup(movieDetailData.caution)
            }
        }
        getRottenTomatoScoreFor(movieDetailData.name)
        imgurl = movieDetailData.h_image_url
        movieNameStr = movieDetailData.name
        if (imgurl.isNotBlank()) {
            Picasso.with(context).load(imgurl)
                    .into(ivTrailer, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            loader.visibility = View.GONE
                            ivTrailer.visibility = View.VISIBLE
                        }

                        override fun onError() {
                            loader.visibility = View.GONE
                        }
                    })
        }
        youtubeTrailerUrl = movieDetailData.trailerUrl
        showPlayIcon(youtubeTrailerUrl)
        try {
            tvMovieTitle.setText(movieDetailData.name)
            if (movieDetailData.name.length > 34) {
                tvMovieTitle.textSize = 12f
            }
            var movieGenreStr: String = ""
            if (movieDetailData.genre.size > 0) {
                movieGenreStr = "${movieDetailData.language} - ${movieDetailData.genre.get(0)}"
            } else {
                movieGenreStr = movieDetailData.language
            }
            if (movieDetailData.genre.size > 1) {
                for (i in 1..movieDetailData.genre.size - 1) {
                    movieGenreStr = movieGenreStr + ", " + movieDetailData.genre.get(i)
                }
            }

            if (movieGenreStr.length > 30) {
                tvMovieTitle.textSize = 12f
            }
            tvLanguageGenre.setText(movieGenreStr)
            tvDuration.setText(movieDetailData.length)
            tvSynopsisData.setText(movieDetailData.desc)
            tvRating.setText(movieDetailData.rating)
            // tvRottenTomatoScore.setText(rtScore)
            val params = FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT
            )
            dynamicFlowLayoutGenratorNowShowing(movieDetailData)
            LogUtils.d("Showtime", "data in flowlayout ${castFlowLayout.childCount}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showPlayIcon(youtubeTrailerUrl: String) {
        var youtubeId = "error"
        youtubeId = AppConstants.extractYTId(youtubeTrailerUrl)
        if (youtubeId.equals("error")) {
            ivPlayTrailer.visibility = View.GONE
        } else {
            ivPlayTrailer.visibility = View.VISIBLE
        }
    }

    private fun populateComingSoonData(movieDetailData: ComingSoon) {
        if (movieDetailData.caution != null) {
            if (movieDetailData.caution.isNotBlank()) {
                showCautionPopup(movieDetailData.caution)
            }
        }
        getRottenTomatoScoreFor(movieDetailData.name)
        imgurl = movieDetailData.h_image_url
        movieNameStr = movieDetailData.name
        if (imgurl.isNotBlank()) {
            Picasso.with(context).load(imgurl)
                    .into(ivTrailer, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            loader.visibility = View.GONE
                            ivTrailer.visibility = View.VISIBLE
                        }

                        override fun onError() {
                            loader.visibility = View.GONE
                        }
                    })
        }
        youtubeTrailerUrl = movieDetailData.trailerUrl
        showPlayIcon(youtubeTrailerUrl)
        try {
            tvMovieTitle.setText(movieDetailData.name)
            if (movieDetailData.name.length > 25) {
                tvMovieTitle.textSize = 12f
            }
            var movieGenreStr: String = ""
            if (movieDetailData.genre.size > 0) {
                movieGenreStr = "${movieDetailData.language} - ${movieDetailData.genre.get(0)}"
            } else {
                movieGenreStr = movieDetailData.language
            }
            if (movieDetailData.genre.size > 1) {
                for (i in 1..movieDetailData.genre.size - 1) {
                    movieGenreStr = movieGenreStr + ", " + movieDetailData.genre.get(i)
                }
            }

            if (movieGenreStr.length > 30) {
                tvMovieTitle.textSize = 12f
            }
            tvLanguageGenre.setText(movieGenreStr)
            tvDuration.setText(movieDetailData.length)
            tvSynopsisData.setText(movieDetailData.desc)
            tvRating.setText(movieDetailData.rating)
            //tvRottenTomatoScore.setText(rtScore)
            val params = FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT
            )
            dynamicFlowLayoutGenrator(movieDetailData)
            LogUtils.d("Showtime", "data in flowlayout ${castFlowLayout.childCount}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun dynamicFlowLayoutGenratorEventCinema(movieDetailData: EventCinema) {
        if (movieDetailData.starring.size > 6) {
            for (i in 0..5) {
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        } else {
            movieDetailData.starring.forEachIndexed { i, eachCast ->
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        }


        if (movieDetailData.director.size > 6) {
            for (i in 0..5) {

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else if (movieDetailData.director.size > 0 && movieDetailData.director.size <= 6) {
            movieDetailData.director.forEachIndexed { i, eachCast ->

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else {
            tvDirectorLabel.visibility = View.GONE
            directorFlowLayout.visibility = View.GONE
//            constraintSet.connect(tvSynopsislabel.id, ConstraintSet.TOP, castFlowLayout.id, ConstraintSet.BOTTOM)
//            constraintSet.applyTo(constraintLayoutParent)
        }
    }


    private fun dynamicFlowLayoutGenratorBanner(movieDetailData: DATA) {
        if (movieDetailData.starring.size > 6) {
            for (i in 0..5) {

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        } else {
            movieDetailData.starring.forEachIndexed { i, eachCast ->

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        }


        if (movieDetailData.director.size > 6) {
            for (i in 0..5) {

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else if (movieDetailData.director.size > 0 && movieDetailData.director.size <= 6) {

            movieDetailData.director.forEachIndexed { i, eachCast ->

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else {
            tvDirectorLabel.visibility = View.GONE
            directorFlowLayout.visibility = View.GONE
//            constraintSet.connect(tvSynopsislabel.id, ConstraintSet.TOP, castFlowLayout.id, ConstraintSet.BOTTOM)
//            constraintSet.applyTo(constraintLayoutParent)
        }
    }

    private fun dynamicFlowLayoutGenrator(movieDetailData: ComingSoon) {
        if (movieDetailData.starring.size > 6) {
            for (i in 0..5) {
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        } else {

            movieDetailData.starring.forEachIndexed { i, eachCast ->

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        }


        if (movieDetailData.director.size > 6) {
            for (i in 0..5) {

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else if (movieDetailData.director.size > 0 && movieDetailData.director.size <= 6) {
            movieDetailData.director.forEachIndexed { i, eachCast ->

                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else {
            tvDirectorLabel.visibility = View.GONE
            directorFlowLayout.visibility = View.GONE
//            constraintSet.connect(tvSynopsislabel.id, ConstraintSet.TOP, castFlowLayout.id, ConstraintSet.BOTTOM)
//            constraintSet.applyTo(constraintLayoutParent)
        }
    }


    private fun dynamicFlowLayoutGenratorNowShowing(movieDetailData: NowShowing) {

        if (movieDetailData.starring.size > 6) {
            for (i in 0..5) {
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        } else {
            movieDetailData.starring.forEachIndexed { i, eachCast ->
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.starring.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                castFlowLayout.addView(parent)
            }
        }


        if (movieDetailData.director.size > 6) {
            for (i in 0..5) {
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)

                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else if (movieDetailData.director.size > 0 && movieDetailData.director.size <= 6) {
            movieDetailData.director.forEachIndexed { i, eachCast ->
                val parent = LinearLayout(this)
                val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT)
                parent.layoutParams = parLP
                parLP.setMargins(12, 12, 12, 12)
                parent.orientation = LinearLayout.HORIZONTAL
                parent.setPadding(20, 30, 20, 30)
                parent.setBackgroundResource(R.drawable.black_prefbackground)
                val tvStaring = buildLabel(movieDetailData.director.get(i))
                tvStaring.setTextColor(Color.WHITE)
                tvStaring.setTypeface(font)
                tvStaring.setTextSize(tvSize.toFloat())
                tvStaring.setSingleLine(true)
                tvStaring.setId(i + 1)
                parent.addView(tvStaring)
                directorFlowLayout.addView(parent)
            }
        } else {
            tvDirectorLabel.visibility = View.GONE
            directorFlowLayout.visibility = View.GONE
            //todo verify what places it affects
//            constraintSet.connect(tvSynopsislabel.id, ConstraintSet.TOP, castFlowLayout.id, ConstraintSet.BOTTOM)
//            constraintSet.applyTo(constraintLayoutParent)
        }
    }


    private fun getRottenTomatoScoreFor(name: String?) {
        if (name != null) {
            val rtInfoReq = RtomatoReq(
                    name,
                    AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM
            )
            //  showtimeVM.fetchRottenTomatoScore(rtInfoReq)
        }
    }

    /**
     * this method will fetch moviedetails while coming from banner click
     */
    private fun fetchMovieDetailsForBannerMovies(tmdbid: String) {

        val state = AppConstants.getString(AppConstants.KEY_STATE_CODE, context)
        val req: ShowtimeMDReq = ShowtimeMDReq(state, tmdbid)
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            showtimeVM.fetchMovieDetails(req)
        }
    }

    var movieDetailObs = object : Observer<ShowtimeMDResp> {
        override fun onChanged(t: ShowtimeMDResp?) {
            populateMovieDetails(t!!)
        }
    }

    private fun fetchShowtimes() {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val request = ShowtimeRequest(
                    AppConstants.getString(AppConstants.KEY_STATE_CODE, context),
                    AppConstants.getString(AppConstants.KEY_LATITUDE, context),
                    AppConstants.getString(AppConstants.KEY_LONGITUDE, context),
                    AppConstants.getString(AppConstants.KEY_MOVIECODE, context),
                    AppConstants.getString(AppConstants.KEY_TMDBID, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context))
            showtimeVM.SchedulesByPreferences(request)
        }
    }

    private fun showtimeobserver() {
        showtimeVM = ViewModelProviders.of(this).get(ShowtimeVM::class.java)
        showtimeVM.getApiErrorData().observe(this, apiErrorObs)
        showtimeVM.getSchedulesByPreferences().observe(this, ShowtimeObs)
        showtimeVM.getRottenTomatoScore().observe(this, rtObs)
        showtimeVM.getShowtimeMovieDetails().observe(this, movieDetailObs)

        /**
         * seatlayout observer to prefetch data
         */
        seatVM = ViewModelProviders.of(this).get(SeatVM::class.java)
        seatVM.getApiErrorDetail().observe(this, apiErrorObs)
        seatVM.getSeatLayoutData().observe(this, seatRespObs)
    }

    private fun unreservedObserver() {
        unreserveVM = ViewModelProviders.of(this).get(UnreserveVM::class.java)
        unreserveVM.getunreserveTicketDetails().observe(this, unReserveObs)
        unreserveVM.getLockSeatDetails().observe(this, lockUnReserveObs)
        unreserveVM.getApiErrorDetails().observe(this, apiErrorObs)
    }

    private var unReserveObs = object : Observer<UnreservedResp> {
        override fun onChanged(t: UnreservedResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                AppConstants.putObject(AppConstants.KEY_UNRESERVEDBOOKING, t, context)

                var year = sessionDataStr!!.bdate.toString()
                year = year.substring(0, Math.min(year.length, 4))
                var date = sessionDataStr!!.number + " " + sessionDataStr!!.text + " " + year
                val i = Intent(context, UnreservedBooking::class.java)
                i.putExtra("moviename", movieNameStr)
                i.putExtra("cinema", AppConstants.cinemaclicked)
                i.putExtra("ccode", ccode)
                i.putExtra("sessionid", sessionDataStr!!.session_id)
                i.putExtra("time", sessionDataStr!!.time)
                i.putExtra("date", date)
                i.putExtra("imgurl", imgurl)
                i.putExtra("expurl", expimage)
                i.putExtra("exprience_single_logo", exprience_single_logo)
                i.putExtra("cid", cid)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
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
                AppConstants.putString(AppConstants.KEY_SEATSTR, t.DATA.receipt.seatinfo, this@ShowtimeActivity)
                goToLoginScreen(t!!)
            } else {
                UtilsDialog.hideProgress()
                alert(t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            }
        }
    }

    /**
     * seatlayout response
     */
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
                intent.putExtra("cname", AppConstants.cinemaclicked)
                intent.putExtra("expimage", expimage)
                intent.putExtra("exprience_single_logo", exprience_single_logo)
                intent.putExtra("imgurl", imgurl)
                startActivity(intent)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            }
        }
    }

    /**
     * Seatlayout request
     */
    public fun fetchSeatLayout(ccode: String, cid: String, sessionId: String, timeStr: String,
                               mname: String, cname: String, expimage: String, exprience_single_logo: String, imgurl: String) {
        this.cid = cid
        this.ccode = ccode
        this.sessionId = sessionId
        this.timeStr = timeStr
        this.mname = mname
        this.cname = cname
        this.expimage = expimage
        this.exprience_single_logo = exprience_single_logo
        this.imgurl = imgurl
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val request = SeatReq(ccode, cid, sessionId, timeStr)
            seatVM.fetchSeatLayout(request)
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

    private var rtObs = object : Observer<RTomatoResp> {
        override fun onChanged(t: RTomatoResp?) {
            if (t!!.STATUS) {
                //  tvRottenTomatoScore.setText(t.DATA.ratings)
            } else {
                //   toast("Failed to Load Showtimes")
            }
        }
    }


    private var ShowtimeObs = object : Observer<ShowtimeResponse> {
        override fun onChanged(t: ShowtimeResponse?) {

            if (t!!.STATUS) {
                try {
                    if (isNearBy == false) {
                        movies = t!!.DATA.movies
                        dateInfo = t!!.DATA.dates
                        for (eachCinema in movies) {
                            if (cinemaArraylist.contains(eachCinema.first().cinemas!!.cname)) {

                            } else {
                                cinemaArraylist.add(eachCinema.first().cinemas!!.cname)
                            }
                        }
                        AppConstants.putObject(AppConstants.KEY_SHOWTIMERESOBJ, t, context)
                    }
                    LogUtils.i("cinemaArraylist", cinemaArraylist.toString())
                    val adapter = ArrayAdapter<String>(this@ShowtimeActivity, R.layout.statelayout, R.id.tvState, cinemaArraylist)
                    editCinema.threshold = 1//will start working from first character
                    editCinema.setAdapter(adapter)
                    var datePager = DatePAgerAdapter(dateInfo, this@ShowtimeActivity)
                    viewpagerDate.offscreenPageLimit = datePager.count
                    viewpagerDate.adapter = datePager
                    viewpagerDate.clipToPadding = false
                    var dimension = resources.getDimension(R.dimen._70sdp)
                    viewpagerDate.setPadding(dimension.toInt(), 0, dimension.toInt(), 0)
                    viewpagerDate.setPageMargin(resources.getDimension(R.dimen._15sdp).toInt());
                    ivLeft.visibility = View.GONE
                    if (datePager.count == 1)
                        ivRight.visibility = View.GONE
                    currentShowTimeData = t!!
                    populateShowTimesData(t, dateInfo.get(0).bdate)
                } catch (e: Exception) {
                    hideLoaderAfterThreeSeconds()
                    LogUtils.d("Showtime", "on showtime response exception ${e.message}")
                    e.printStackTrace()
                    alert(getString(R.string.no_showtime_available),
                            getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }
                }
            } else {
                UtilsDialog.hideProgress()
                alert(getString(R.string.no_showtime_available),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }
                llParentShowLayout.visibility = View.VISIBLE
                tvNoShowtimes.visibility = View.VISIBLE
            }
        }
    }

    private fun hideLoaderAfterThreeSeconds() {
        Handler().postDelayed({
            UtilsDialog.hideProgress()
        }, 2000)
    }


    private fun populateShowTimesData(t: ShowtimeResponse?, bdate: String) {
        val firstDate = bdate
        var tempDataList: ArrayList<ShowtimeResponse.DATa.MovieInfo> = ArrayList<ShowtimeResponse.DATa.MovieInfo>()
        for (movieItem in t!!.DATA.movies) {
            if (movieItem.first().show_date.equals(firstDate, ignoreCase = true)) {
                tempDataList.add(movieItem.first())
            }
        }
        currentCinemaList.clear()
        currentCinemaList = shortListByPreference(tempDataList)
        generateDynamicShowtimeFrom(currentCinemaList)
        if (currentCinemaList.isEmpty()) {
            noShowtime.visibility = View.VISIBLE
            llShowtimeHolder.visibility = View.GONE
        } else {
            noShowtime.visibility = View.GONE
            llShowtimeHolder.visibility = View.VISIBLE
        }
        UtilsDialog.hideProgress()
        llParentShowLayout.visibility = View.VISIBLE
    }

    private fun generateDynamicShowtimeFrom(currCinemaList:
                                            ArrayList<ShowtimeResponse.DATa.MovieInfo>) {
        llShowtimeHolder.removeAllViews()
        var showNearByLabel = false
        if (currCinemaList.size > 1) {
            showNearByLabel = true
        }
        currCinemaList.forEachIndexed { index, eachCinema ->
            if (index < currentListSizeLimit) {
                var cinemaLayout = LayoutInflater.from(context).inflate(R.layout.row_cinemanongps,
                        scrollParent, false)
                if (isGPSLayout) {
                    cinemaLayout = LayoutInflater.from(context).inflate(R.layout.row_cinemasshowtime,
                            scrollParent, false)
                }
                var maintananceFlag = false
                val tvCinemaName = cinemaLayout.findViewById<TextView>(R.id.tvCinemaName)
                val tvMiles = cinemaLayout.findViewById<TextView>(R.id.tvMiles)
                val tvNearby = cinemaLayout.findViewById<TextView>(R.id.tvNearby)

                val ivMiles = cinemaLayout.findViewById<ImageView>(R.id.ivLocationMiles)
                val ivPreferredHeart = cinemaLayout.findViewById<ImageView>(R.id.ivPreferred)
                val tblShows = cinemaLayout.findViewById<TableLayout>(R.id.tblShows)
                tvCinemaName.text = eachCinema.cinemas!!.cname

                if (isNearBy && showNearByLabel) {
                    if (index == 0) {
                        tvNearby.visibility = View.VISIBLE
                    } else {
                        tvNearby.visibility = View.GONE

                    }
                }

                if (eachCinema.cinemas.miles != null && !eachCinema.cinemas.miles.equals("", ignoreCase = true)) {
                    tvMiles.text = eachCinema.cinemas.miles
                    ivMiles.visibility = View.VISIBLE
                    tvMiles.visibility = View.VISIBLE
                } else {
                    ivMiles.visibility = View.GONE
                    tvMiles.visibility = View.GONE
                }
                if (!eachCinema.isPreferred) {
                    Picasso.with(context).load(R.drawable.whiteborderheart).into(ivPreferredHeart)
                } else {
                    Picasso.with(context).load(R.drawable.redfilledheart).into(ivPreferredHeart)
                }
                tblShows.removeAllViews()
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val lastExpIndex = eachCinema.cinemas.exp_data.size - 1

                cinemaNameStr = eachCinema.cinemas.cname
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

                    val lnrExpand = expRow.findViewById<LinearLayout>(R.id.lnrExpand)
                    val gridView = ExpandableGridView(this)
                    gridView.refreshIconInfoList(expData.session_data, ccode, cid)
                    lnrExpand.addView(gridView)

                    if (expData.exprience_single_logo != null)
                        localexprience_single_logo = expData.exprience_single_logo


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
                    if (expData.exprience_single_logo != null) {
                        if (expData.exprience_single_logo.equals("0")) {
                            ivExp.getLayoutParams().width = resources.getDimension(R.dimen.imageview_width).toInt()
                            ivExp.requestLayout()
                        }
                    }

                    var localVariableExperienceImage = ""
                    var localVariableExperienceText = ""


                    if (expData.exp_image.isNotBlank()) {
                        Picasso.with(context).load(expData.exp_image).into(ivExp)
                        localVariableExperienceImage = expData.exp_image
                        localVariableExperienceText = ""
                    } else {
                        ivExp.visibility = View.GONE
                        localVariableExperienceImage = ""
                        localVariableExperienceText = expData.exp_name

                        tvExperienceName.setText(expData.exp_name)
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
                            0, GridLayout.CENTER, 1f),
                            GridLayout.spec(0, GridLayout.CENTER, 1f))

                    val viewGroupParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    parentExpGridview.layoutParams = viewGroupParams
                    val lastTimeIndex = expData.session_data.size - 1
                    if (!maintananceFlag) {
                        val rootViewParent: LinearLayout = (context as ShowtimeActivity)
                                .findViewById(R.id.llParentShowLayout)

                        var leftItem = false
                        expData.session_data.forEachIndexed { timeIndex, sessionData ->
                            val showTimeLayout = inflater.inflate(R.layout.row_showtimeindividual, null)
                            val gridLayoutParams = GridLayout.LayoutParams()

                            // if first item remove margin left, if last item remove margin right
                            // 0,4,8,12,16 - remove left margin
                            // 3,7,11,15 - remove right margin
                            if (timeIndex == 0 || timeIndex % 4 == 0) {
                                gridLayoutParams.setMargins(0, 8, 8, 8)
                            } else {
                                val toCheckLast = timeIndex + 1
                                if (toCheckLast % 4 == 0) {
                                    gridLayoutParams.setMargins(8, 8, 0, 8)
                                } else {
                                    gridLayoutParams.setMargins(8, 8, 8, 8)
                                }
                            }
                            showTimeLayout.layoutParams = gridLayoutParams
                            val tvShowTime = showTimeLayout.findViewById<TextView>(R.id.tvShowTime)
                            tvShowTime.text = sessionData.time
                            tvShowTime.setOnClickListener {
                                if (expData.movie_flag.isNotBlank()) {
                                    movieNameStr = movieNameStr + " - " + expData.movie_flag
                                }
                                AppConstants.putString(AppConstants.KEY_EXPERIENCELOGOURL,
                                        localVariableExperienceImage, context)
                                AppConstants.putString(AppConstants.KEY_EXPERIENCETEXT,
                                        localVariableExperienceText, context)
                                AppConstants.putString(AppConstants.KEY_CID, cid, context)
                                AppConstants.putString(AppConstants.KEY_SCREENNAME, sessionData.screen, context)

                                AppConstants.putString(AppConstants.KEY_SHOWTIME, "movie_showtime", context)

                                sessionDataStr = sessionData

                                val innerCinemaName = eachCinema.cinemas!!.cname
                                AppConstants.cinemaclicked = innerCinemaName

                                if (sessionData.is_reserved) {
                                    showSeatPreviewPopup(expData, sessionData, cid, ccode, innerCinemaName,
                                            tvShowTime, tblShows, localExpImageStr, localexprience_single_logo)
                                } else {
                                    this.cid = cid
                                    this.ccode = ccode
                                    this.sessionId = sessionId

                                    val request = UnreservedReq(ccode,
                                            cid, sessionData.session_id, sessionData.time)
                                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                                        UtilsDialog.showProgressDialog(context, "")
                                        unreserveVM.unreserveTicket(request)
                                    }
                                }
                                //sessionClicked(expData, sessionData, cid, ccode, cinemaNameStr)
                            }
                            parentExpGridview.addView(showTimeLayout)
                            LogUtils.d("ShowtimeAdapter", "added  -$index - ${sessionData.time} ")
                        }
                    }
                    LogUtils.d("ShowtimeAdapter", " ${parentExpGridview.childCount} number of elements ")
                    tblShows.addView(parentExpGridview)
                }
                llShowtimeHolder.addView(cinemaLayout)
            }//else do nothing
        }
        if (currCinemaList.size > currentListSizeLimit) {
            clViewMore.visibility = View.VISIBLE
        } else {
            clViewMore.visibility = View.GONE
        }
    }

    fun showSeatPreviewPopup(expData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData,
                             sessionData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData,
                             cid: String, ccode: String, cinemaName: String,
                             anchorView: View, tableView: View, localExpImageStr: String, localexprience_single_logo: String) {
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
        //val hScrollView = dialog.findViewById<HorizontalScrollView>(R.id.hscrollView)
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
            dialog.dismiss()
            AppConstants.cinemaclicked = cinemaName
            fetchSeatLayout(ccode, cid, sessionData.session_id, sessionData.time_str,
                    movieNameStr, cinemaName, localExpImageStr, localexprience_single_logo, imgurl)
        }
        dialog.show()
    }

    private fun showPopupExperienceInfo(expData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData) {
        val dialog = Dialog(context)
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.expinfo_dialog, null)
        dialog.setContentView(mDialogView);

        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

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
        }

        mDialogView.ivClosePopup.setOnClickListener {
            dialog.dismiss()
        }
    }


    private var apiErrorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert("No Showtimes Available", getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }


    fun shortListByPreference(localList: java.util.ArrayList<ShowtimeResponse.DATa.MovieInfo>):
            java.util.ArrayList<ShowtimeResponse.DATa.MovieInfo> {

        if (isNearBy) {
            return localList
        } else {
            val preferredCinemas: java.util.ArrayList<String> = AppConstants.getStringList(
                    AppConstants.KEY_PREFEREDCINEMA, context)
            val tempLocalList = localList
            var sortedLocalList: ArrayList<ShowtimeResponse.DATa.MovieInfo>
            val prefLocalList: java.util.ArrayList<ShowtimeResponse.DATa.MovieInfo> = java.util.ArrayList()
            val remainingLocalList: java.util.ArrayList<ShowtimeResponse.DATa.MovieInfo> = java.util.ArrayList()

            tempLocalList.forEachIndexed { index, movieInfo ->
                if (!movieInfo.isHeader) {
                    if (movieInfo.cinemas!!.ccode in preferredCinemas) {
                        movieInfo.isPreferred = true
                        prefLocalList.add(movieInfo)
                    } else {
                        remainingLocalList.add(movieInfo)
                    }
                } else {
                    remainingLocalList.add(movieInfo)
                }
            }
            sortedLocalList = prefLocalList
            for (eachCinema in remainingLocalList) {
                sortedLocalList.add(eachCinema)
            }
            return sortedLocalList
        }
    }


    /**
     * This function will show a caution popup for users of extra light
     */
    private fun showCautionPopup(caution: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_caution);
        //dialog.getWindow().setLayout(matchParent, matchParent);
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false)
        val tvCautionContent: TextView = dialog!!.findViewById(R.id.tvCautionContent)
        val btnAgree: Button = dialog!!.findViewById(R.id.btnAgree)
        val tvGoback: TextView = dialog!!.findViewById(R.id.tvGoback)
        tvCautionContent.setText(caution)
        btnAgree.setOnClickListener {
            dialog.dismiss()
        }
        tvGoback.setOnClickListener {
            onBackPressed()
        }
        dialog.show();
    }

    /**
     * buildlabel for lang and genre type tv
     */
    private fun buildLabel(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setTextColor(Color.WHITE)
        textView.setSingleLine(true)
        textView.gravity = Gravity.CENTER
        return textView
    }

    override fun onBackPressed() {
        if (AppConstants.getString(AppConstants.KEY_SHOWTIME, context).equals("home_showtime")) {
            super.onBackPressed()
        } else {
            val intent = Intent(this@ShowtimeActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
        }
    }

    private fun formTablelayout(rowList: ArrayList<Rowlist>, tblSeats: TableLayout, zlParent: ZoomLayout) {
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
                    ivSeat.layoutParams = ivLp
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
        dismissDialogAfterOneSec(zlParent)
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

    private fun dismissDialogAfterOneSec(zlLayout: ZoomLayout) {
        Handler().postDelayed({
            //            val offsetX = tblSeats.width / 5
//            horizontalScrollView.scrollTo(offsetX, 0)
//            zlLayout.setTransformation(ZoomApi.TRANSFORMATION_CENTER_INSIDE,
//                    Gravity.CENTER)
            zlLayout.engine.setTransformation(ZoomApi.TRANSFORMATION_CENTER_INSIDE,
                    Gravity.CENTER)
            UtilsDialog.hideProgress()
        }, 1000)
    }

    private fun setupSeatBackground(eachSeat: Seatlist, ivSeat: TextView) {
        if (eachSeat.Status.equals("Available", ignoreCase = true)) {
            when (eachSeat.SeatStyle.toLowerCase()) {
                "normal" -> { //space
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                "loveseatright" -> {// unselected
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                "loveseatleft" -> {//selecte
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
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

    fun generateEmptySpaceTV(): TextView {
        val tvEmptySpace = TextView(this)
        tvEmptySpace.layoutParams = ivLp
        return tvEmptySpace
    }

    private val onDismissListeneretCinema = AutoCompleteTextView.OnDismissListener {
        if (editCinema.text.length == 0) {
            // do nothing
            editCinema.isFocusable = false
        }
    }
}
