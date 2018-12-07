package com.influx.marcus.theatres.movielisting

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.filter.FilteredMoviesReq
import com.influx.marcus.theatres.api.ApiModels.home.*
import com.influx.marcus.theatres.homepage.*
import com.influx.marcus.theatres.moviefilter.MovieFilterActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_movie_listing.*
import org.jetbrains.anko.alert


class MovieListingActivity : AppCompatActivity() {


    lateinit var mcontext: Context
    private lateinit var homeVM: HomeVm
    var homeResponse: HomeResponse? = null
    lateinit var movieabViewpager: ViewPager
    private var viewpagerAdapter: ViewPagerAdapter? = null
    lateinit var tabLayout: TabLayout
    lateinit var back: ImageView

    var nowShowing: ArrayList<NowShowing> = ArrayList<NowShowing>()
    var comingSoon: ArrayList<ComingSoon> = ArrayList<ComingSoon>()
    var eventCinema: ArrayList<EventCinema> = ArrayList<EventCinema>()
    lateinit var ivFilter: ImageView
    lateinit var Movies: Movies


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_listing)
        mcontext = this@MovieListingActivity
        tabLayout = findViewById(R.id.movieTabs)
        back = findViewById(R.id.ivBack)
        movieabViewpager = findViewById(R.id.movieabViewpager)
        ivFilter = findViewById(R.id.ivFilter)
        tabLayout.setupWithViewPager(movieabViewpager)
        movieabViewpager.offscreenPageLimit = 3

        back.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })

        ivFilter.setOnClickListener {
            val i = Intent(this@MovieListingActivity, MovieFilterActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        etSearchByMve.setImeOptions(EditorInfo.IME_ACTION_DONE)
        etSearchByMve.setSingleLine()
        etSearchByMve.setPressed(true)
        etSearchByMve.setOnTouchListener(View.OnTouchListener { v, event ->
            etSearchByMve.setFocusableInTouchMode(true)
            etSearchByMve.isCursorVisible = true

            false
        })

        etSearchByMve.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.length == 0) {
                    setupViewPager(movieabViewpager, Movies)
                } else {
                    filterByText(cs.toString())
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int,
                                           arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        homeobserver()
        if (AppConstants.is_filterApplied) {
            fetchFilteredMovieList()
        } else {
            fetchNonFilteredMovieListing()
        }
    }

    private fun filterByText(search: String) {
        var filteredNowShowing: ArrayList<NowShowing> = ArrayList<NowShowing>()
        var filteredComingSoon: ArrayList<ComingSoon> = ArrayList<ComingSoon>()
        var filteredEventCinema: ArrayList<EventCinema> = ArrayList<EventCinema>()
        for (eachMovie in nowShowing) {
            if (eachMovie.name.contains(search, ignoreCase = true)) {
                filteredNowShowing.add(eachMovie)
            }
        }
        for (eachMovie in eventCinema) {
            if (eachMovie.name.contains(search, ignoreCase = true)) {
                filteredEventCinema.add(eachMovie)
            }
        }
        for (eachMovie in comingSoon) {
            if (eachMovie.name.contains(search, ignoreCase = true)) {
                filteredComingSoon.add(eachMovie)
            }
        }
        setupViewPager(movieabViewpager, filteredNowShowing, filteredComingSoon, filteredEventCinema)

    }

    private fun fetchFilteredMovieList() {
        val cinemaList = ArrayList<String>()
        cinemaList.addAll(AppConstants.selectedCinmastoFilter)
        val genrs = AppConstants.selectedGenrestoFilter
        val language = AppConstants.selectedLanguagestoFilter
        val date = AppConstants.getString(AppConstants.KEY_FILTERDATE, mcontext)
        val request = FilteredMoviesReq(date,
                cinemaList, genrs, language, AppConstants.APP_PLATFORM, AppConstants.APP_VERSION)

        if (genrs.size < 1 && language.size < 1 && cinemaList.size < 1 && date.equals("",
                        ignoreCase = true)) {
            AppConstants.is_filterApplied = false
            fetchNonFilteredMovieListing()
        } else {
            if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
                UtilsDialog.showProgressDialog(mcontext, "")
                homeVM.SchedulesByFilters(request)
            }
        }
    }

    private fun fetchNonFilteredMovieListing() {
        if (AppConstants.is_filterApplied) {
            AppConstants.is_filterApplied = false
            fetchFilteredMovieList()
        } else {
            val cinema = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, mcontext)
            val genrs = AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, mcontext)
            val language = AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, mcontext)
            val stateCode = AppConstants.getString(AppConstants.KEY_STATE_CODE, mcontext)
            var prefs = Preference(cinema, language, genrs)
            val request = HomeRequest(AppConstants.APP_PLATFORM, AppConstants.APP_VERSION, prefs, stateCode)
            if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
                UtilsDialog.showProgressDialog(mcontext, "")
                homeVM.SchedulesByPreferences(request)
            }
        }
    }


    private fun homeobserver() {
        homeVM = ViewModelProviders.of(this).get(HomeVm::class.java)
        homeVM.getApiErrorData().observe(this, apiErrorObs)
        homeVM.getSchedulesByPreferences().observe(this, homeRespObs)
    }

    private var homeRespObs = object : Observer<HomeResponse> {

        override fun onChanged(t: HomeResponse?) {
            clMovieListing.visibility= View.VISIBLE
            UtilsDialog.hideProgress()
            if (t!!.DATA.movies != null) {
                if (t!!.DATA.movies.now_showing != null)
                    nowShowing = t!!.DATA!!.movies!!.now_showing as ArrayList<NowShowing>
                if (t!!.DATA.movies.coming_soon != null)
                    comingSoon = t!!.DATA!!.movies!!.coming_soon as ArrayList<ComingSoon>
                if (t!!.DATA.movies.event_cinema != null)
                    eventCinema = t!!.DATA!!.movies!!.event_cinema as ArrayList<EventCinema>

                Movies = t!!.DATA.movies
                if (t!!.DATA.movies.now_showing != null || t!!.DATA.movies.coming_soon != null || t!!.DATA.movies.event_cinema != null) {
                    LogUtils.i("nowshowing name",t!!.DATA.movies.now_showing.get(0).long_name)
                    setupViewPager(movieabViewpager, Movies)
                    etSearchByMve.visibility = View.VISIBLE
                    Vsearch.visibility = View.VISIBLE
                } else {
                    tvNoData.visibility = View.VISIBLE
                }
            }
        }
    }

    private var apiErrorObs = object : Observer<String> {
        override fun onChanged(t: String?) {

            alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {
                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
            UtilsDialog.hideProgress()
            LogUtils.d("Preference", " Error - $t!!")
        }
    }

    private fun setupViewPager(viewPager: ViewPager, t: Movies?) {
        viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewpagerAdapter?.addFragment(NowShowingFragmentML.newInstance((t!!.now_showing as List<NowShowing>
                )), "Now Showing")
        viewpagerAdapter?.addFragment((ComingSoonFragmentML.newInstance((t!!.coming_soon as List<ComingSoon>
                ))), "Coming Soon")
        viewpagerAdapter?.addFragment(EventCinemaFragmentML.newInstance((t!!.event_cinema as List<EventCinema>
                )), "Event Cinema")
        viewPager.adapter = viewpagerAdapter
    }

    private fun setupViewPager(viewPager: ViewPager, nowShowing: ArrayList<NowShowing>,
                               comingSoon: ArrayList<ComingSoon>, eventCinema: ArrayList<EventCinema>) {
        viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewpagerAdapter?.addFragment(NowShowingFragmentML.newInstance((nowShowing as ArrayList<NowShowing>
                )), "Now Showing")
        viewpagerAdapter?.addFragment((ComingSoonFragmentML.newInstance((comingSoon as ArrayList<ComingSoon>
                ))), "Coming Soon")
        viewpagerAdapter?.addFragment(EventCinemaFragmentML.newInstance((eventCinema as ArrayList<EventCinema>
                )), "Event Cinema")
        viewPager.adapter = viewpagerAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }


}
