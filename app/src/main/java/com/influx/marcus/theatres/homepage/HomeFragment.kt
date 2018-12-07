package com.influx.marcus.theatres.homepage

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.*
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.home.Preference
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.commonview.WrapContentViewPagerMyAccount
import com.influx.marcus.theatres.movielisting.MovieListingActivity
import com.influx.marcus.theatres.preferences.PreferenceActivity
import com.influx.marcus.theatres.utils.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.relex.circleindicator.CircleIndicator
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


class HomeFragment : Fragment() {

    lateinit var viewpager: ViewPagerCustomDuration
    lateinit var tabViewpager: WrapContentViewPagerMyAccount
    lateinit var indicatorr: CircleIndicator
    lateinit var adapter: BannerPagerAdapter
    lateinit var tabLayout: TabLayout
    lateinit var ivFavourite: ImageView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var scrollView: NestedScrollView
    private var viewpagerAdapter: ViewPagerAdapter? = null
    private lateinit var homeVM: HomeVm
    lateinit var mcontext: Context
    lateinit var bannersItem: MutableList<Banner>
    var currentPage = 0
    private val KEY_GPSFLOW = "GPSFLOW"
    private val KEY_EDITMODE = "EDITMODE"
    private val localdevie = null
    var locationManager: LocationManager? = null
    private var stateCode = ""
    private var isFindNowButton = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var ivMenu: ImageView
    private lateinit var ivSearch: ImageView
    private lateinit var homeLineView: View
    private lateinit var homeRequest: HomeRequest

    private val webApi = RestClient.getApiClient()

    companion object {
        fun newInstance(): HomeFragment {
            var fragment = HomeFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.frag_home, container, false)
        try {
            initViews(rootView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rootView
    }

    fun initViews(rootView: View) {
        mcontext = (this.activity as Context?)!!
        UtilsDialog.showProgressDialog(mcontext, "")
        homeLineView = rootView.findViewById(R.id.homeView)
        viewpager = rootView.findViewById(R.id.viewpager)
        ivMenu = rootView.findViewById(R.id.ivOptions)
        indicatorr = rootView.findViewById(R.id.indicator)
        ivFavourite = rootView.findViewById(R.id.ivFavourite)
        scrollView = rootView.findViewById(R.id.scrollView)
        indicatorr.setViewPager(viewpager)
        tabViewpager = rootView.findViewById(R.id.tabviewpager)
        tabViewpager.offscreenPageLimit = 3
        ivSearch = rootView.findViewById(R.id.ivSearch)
        ivSearch.setOnClickListener {
            val movieListIntent = Intent(context, MovieListingActivity::class.java)
            movieListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context!!.startActivity(movieListIntent)
            activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
        tabViewpager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                tabViewpager.reMeasureCurrentPage(tabViewpager.currentItem)
                scrollView.scrollTo(0, 0)
            }
        })
        tabLayout = rootView.findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(tabViewpager)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mcontext)
        ivMenu.setOnClickListener {
            (mcontext as HomeActivity).toggleNavigationDrawer()
        }
        ivFavourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                UtilsDialog.hideProgress()
                navigateWithCredentials()
            }
        })
        homeobserver()
        fetchHomscreenPosters()
        //fetchhomedetails()
    }


/*
    private fun fetchhomedetails() {

        try {
            val homeCachedData = (AppConstants.getObject(AppConstants.KEY_HOMEPAGEDATA,
                    mcontext, HomeResponse::class.java as Class<Any>) as HomeResponse)
            if (homeCachedData != null) {
                homeRespObs.onChanged(homeCachedData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.d("HOME", "fetch home data again")
            // no catch found load via api
            val cinema = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, mcontext)
            val genrs = AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, mcontext)
            val language = AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, mcontext)
            val stateCode = AppConstants.getString(AppConstants.KEY_STATE_CODE, mcontext)
            val prefs = Preference(cinema, language, genrs)
            val request = HomeRequest(AppConstants.APP_PLATFORM, AppConstants.APP_VERSION, prefs, stateCode)
            if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
                UtilsDialog.showProgressDialog(mcontext, "")
                homeVM.SchedulesByPreferences(request)
            }
        }
    }
*/


    private fun homeobserver() {
        homeVM = ViewModelProviders.of(this).get(HomeVm::class.java)
        homeVM.getApiErrorData().observe(this, apiErrorObs)
        //homeVM.getSchedulesByPreferences().observe(this, homeRespObs)
    }

//    private var homeRespObs = object : Observer<HomeResponse> {
//        override fun onChanged(t: HomeResponse?) {
//            //updateUiBasedOnHomeData(t)
//        }
//    }

    /*private fun updateUiBasedOnHomeData(t: HomeResponse?) {
        if (t!!.STATUS) {
            try {
                // top banner images
                doAsync {
                    bannersItem = t.DATA.banners as MutableList<Banner>
                    val bannerAdapter = BannerPagerAdapter(activity, bannersItem)
                    // setup viewpager adapter
                    viewpagerAdapter = ViewPagerAdapter(childFragmentManager)
                    viewpagerAdapter?.addFragment(NowShowingFragment.newInstance((t.DATA.movies.now_showing
                            as List<NowShowing>
                            )), "Now Showing")
                    viewpagerAdapter?.addFragment((ComingSoonFragment.newInstance((t.DATA.movies.coming_soon
                            as List<ComingSoon>
                            ))), "Coming Soon")
                    viewpagerAdapter?.addFragment(EventCinemaFragment.newInstance((t.DATA.movies.event_cinema
                            as List<EventCinema>
                            )), "Event Cinemas")
                    uiThread {
                        viewpager.adapter = bannerAdapter
                        indicatorr.setViewPager(viewpager)
                        bannerautoscroll(bannersItem.size)
                        tabViewpager.adapter = viewpagerAdapter
                        homeLineView.visibility = View.VISIBLE
                        hideLoaderAfterAsec()
                    }
                }
                // bottom movies
                //setupViewPager(tabViewpager, t.DATA.movies)
            } catch (e: Exception) {
                UtilsDialog.hideProgress()
                LogUtils.d("HomeBanner", " banners error ${e.message}")
            }
            AppConstants.putObject(AppConstants.KEY_HOMEPAGEDATA, t, mcontext)
        } else {
            UtilsDialog.hideProgress()
            // toast(t.DATA.message)
        }
    }*/


    private fun updateUiHomePosterData(t: HomeMoviePosterResp?) {
        if (t!!.STATUS) {
            try {
                // top banner images
                doAsync {
                    bannersItem = t.DATA.banners as MutableList<Banner>
                    val bannerAdapter = BannerPagerAdapter(activity, bannersItem)
                    // setup viewpager adapter
                    viewpagerAdapter = ViewPagerAdapter(childFragmentManager)
                    viewpagerAdapter?.addFragment(NowShowingFragment.newInstance((t.DATA.movies.now_showing
                            as List<NowShowing>
                            )), "Now Showing")
                    viewpagerAdapter?.addFragment((ComingSoonFragment.newInstance((t.DATA.movies.coming_soon
                            as List<ComingSoon>
                            ))), "Coming Soon")
                    viewpagerAdapter?.addFragment(EventCinemaFragment.newInstance((t.DATA.movies.event_cinema
                            as List<EventCinema>
                            )), "Event Cinema")
                    uiThread {
                        viewpager.adapter = bannerAdapter
                        indicatorr.setViewPager(viewpager)
                        bannerautoscroll(bannersItem.size)
                        tabViewpager.adapter = viewpagerAdapter
                        homeLineView.visibility = View.VISIBLE
                        hideLoaderAfterAsec()
                    }
                }
                // bottom movies
                //setupViewPager(tabViewpager, t.DATA.movies)
            } catch (e: Exception) {
                UtilsDialog.hideProgress()
                LogUtils.d("HomeBanner", " banners error ${e.message}")
            }
            AppConstants.putObject(AppConstants.KEY_HOMEPAGEDATA, t, mcontext)
        } else {

            UtilsDialog.hideProgress()
            // toast(t.DATA.message)
        }
    }


    private fun hideLoaderAfterAsec() {
        Handler().postDelayed({
            UtilsDialog.hideProgress()
        }, 1000)
    }

    private var apiErrorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            LogUtils.d("Preference", " Error - $t!!")
        }
    }


    private val locationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            getStateAndOtherDetailsFromLocation(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /***
     *static latlong when comes from home pref icon
     */
    private fun getStateAndOtherDetailsFromLocation(location: Location) {
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(mcontext, Locale.getDefault())
            LogUtils.d("HomeFrag", "Lat long are ${location.latitude} and ${location.longitude}")
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            //addresses = geocoder.getFromLocation(42.032, -88.338, 1)
            stateCode = this!!.getUSStateCode(addresses.get(0))!!
            navigateToPreference(location.latitude.toString(),
                    location.longitude.toString(),
                    stateCode)
        } catch (e: Exception) {
            navigateToPreferenceNonGps()
            LogUtils.d("LocationActivity", "find location exception ${e.message}")
        }
    }


    private fun getUSStateCode(USAddress: Address): String? {
        try {
            var fullAddress = ""
            for (j in 0..USAddress.maxAddressLineIndex)
                if (USAddress.getAddressLine(j) != null)
                    fullAddress = fullAddress + " " + USAddress.getAddressLine(j)

            var stateCode: String? = null
            val pattern = Pattern.compile(" [A-Z]{2} ")
            val helper = fullAddress.toUpperCase().substring(0, fullAddress.toUpperCase().indexOf("USA"))
            val matcher = pattern.matcher(helper)
            while (matcher.find())
                stateCode = matcher.group().trim()
            return stateCode
        } catch (e: Exception) {
            LogUtils.d("HomeFragment", "location activity crash - ${e.message}")
            return null
        }
    }

    fun navigateWithCredentials() {

        val longitude = AppConstants.getString(AppConstants.KEY_LONGITUDE, mcontext)
        if (longitude.isBlank()) {
            isFindNowButton = false
            navigateToPreferenceNonGps()
        } else {
            launch(UI) {
                try {
                    val result = askPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    isFindNowButton = true
                    startLocationProvider()
                } catch (e: PermissionException) {
                    e.denied.forEach {
                        isFindNowButton = false
                        navigateToPreferenceNonGps()
                    }
                    e.accepted.forEach {

                    }
                }
            }
        }
    }


    private fun startLocationProvider() {

        try {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        if (it != null) {
                            getStateAndOtherDetailsFromLocation(it)
                        } else {
                            navigateToPreferenceNonGps()
                        }
                    }
        } catch (e: SecurityException) {
            navigateToPreferenceNonGps()
            LogUtils.d("fusedactivity", "security error fetch location - ${e.message}")
        }

    }


    private fun navigateToPreferenceNonGps() {
        var prefIntent = Intent(mcontext, PreferenceActivity::class.java)
        prefIntent.putExtra("GPSFLOW", false)
        prefIntent.putExtra(KEY_EDITMODE, true)
        startActivity(prefIntent)
        activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
    }

    private fun navigateToPreference(latitude: String, longitude: String, stateCode: String) {
        val prefIntent = Intent(mcontext, PreferenceActivity::class.java)
        AppConstants.putString(AppConstants.KEY_STATE_CODE, stateCode, mcontext)
        AppConstants.putString(AppConstants.KEY_LATITUDE, latitude, mcontext)
        AppConstants.putString(AppConstants.KEY_LONGITUDE, longitude, mcontext)
        if (AppConstants.getString(AppConstants.KEY_LATITUDE, mcontext).isNotBlank()) {
            prefIntent.putExtra(KEY_GPSFLOW, true)
        }
        prefIntent.putExtra(KEY_EDITMODE, true)
        prefIntent.putExtra("GPSFLOW", true)
        startActivity(prefIntent)
       activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

    }

    private fun updateHomeData() {
        val homeServiceIntent = Intent(context, FetchHomeService::class.java)
        mcontext.startService(homeServiceIntent)
        activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

    }


    /**
     * This method is to fetchmovie data and show posters in home screen
     */
    private fun fetchHomscreenPosters() {
        UtilsDialog.showProgressDialog(mcontext, "")
        val cinema = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, mcontext)
        val genrs = AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, mcontext)
        val language = AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, mcontext)
        val stateCode = AppConstants.getString(AppConstants.KEY_STATE_CODE, mcontext)
        val prefs = Preference(cinema, language, genrs)
        val request = HomeRequest(AppConstants.APP_PLATFORM, AppConstants.APP_VERSION,
                prefs, stateCode)

        val homeCall: Call<HomeMoviePosterResp> = webApi.getHomeScreenMoviesdata(
                AppConstants.AUTHORISATION_HEADER, request
        )
        homeCall.enqueue(object : Callback<HomeMoviePosterResp> {
            override fun onFailure(call: Call<HomeMoviePosterResp>, t: Throwable) {
                UtilsDialog.hideProgress()
            }

            override fun onResponse(call: Call<HomeMoviePosterResp>, response: Response<HomeMoviePosterResp>) {
                if (response.isSuccessful) {
                    updateUiHomePosterData(response.body())
                } else {
                    UtilsDialog.hideProgress()
                    alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                        positiveButton("OK") { dialog ->
                            dialog.dismiss()
                        }
                    }.show()
                }
            }
        })

    }

    private fun bannerautoscroll(size: Int) {
        val handler = Handler()

        val Update = Runnable {
            if (currentPage === size - 1) {
                currentPage = 0
            }
            viewpager.setCurrentItem(currentPage++, true)
        }

        val timer = Timer() // This will create a new Thread
        timer.schedule(object : TimerTask() { // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, 1500, 4000)
    }


}
