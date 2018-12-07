package com.influx.marcus.theatres.homepage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSResp
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSResp
import com.influx.marcus.theatres.api.ApiModels.filter.FilteredMoviesReq
import com.influx.marcus.theatres.api.ApiModels.home.HomeResponse
import com.influx.marcus.theatres.api.ApiModels.myaccount.MyAccountResp
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateResponse
import com.influx.marcus.theatres.api.ApiModels.specials.SpecialResp
import com.influx.marcus.theatres.forgotpassword.SpecialsVM
import com.influx.marcus.theatres.login.LoginScreen
import com.influx.marcus.theatres.login.QRLoginnew
import com.influx.marcus.theatres.movielisting.MovieListingActivity
import com.influx.marcus.theatres.myaccount.MyAccountScreen
import com.influx.marcus.theatres.myaccount.MyAccountVM
import com.influx.marcus.theatres.specials.SpecialsActivity
import com.influx.marcus.theatres.theatres.OurTheatres
import com.influx.marcus.theatres.theatres.TheatresVM
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.lang.reflect.Field
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, homeuihelper {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var context: Context
    private val KEY_GPSFLOW = "GPSFLOW"
    private val KEY_EDITMODE = "EDITMODE"
    lateinit var tvMovies: TextView
    lateinit var ivClose: ImageView
    lateinit var tvTheatres: TextView
    lateinit var tvMyAcoount: TextView
    lateinit var tvMagical: TextView
    lateinit var tvGiftCards: TextView
    lateinit var tvExperience: TextView
    lateinit var tvEvents: TextView
    lateinit var tvTerms: TextView
    lateinit var tvContact: TextView
    lateinit var tvPrivacy: TextView
    lateinit var tvAboutus: TextView
    lateinit var tvSpecial: TextView
    lateinit var tvFaq: TextView
    lateinit var tvMore: TextView
    lateinit var tvSignOut: TextView
    lateinit var ivBack: ImageView
    lateinit var btJoinNow: TextView
    lateinit var tvBonus: TextView
    lateinit var tvBack: ImageView
    lateinit var tvRegister: TextView
    var isJoin: Boolean = false
    lateinit var contentabout: ConstraintLayout
    lateinit var content: ConstraintLayout
    var locationManager: LocationManager? = null
    private var stateCode = ""
    private var isFindNowButton = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var myAccountVM: MyAccountVM
    lateinit var specialsVM: SpecialsVM
    lateinit var theatresVM: TheatresVM
    lateinit var homeVM: HomeVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // setSupportActionBar(toolbar)
        context = this@HomeActivity
        try {
            initViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        SetShiftMode(bottomNavigationView, false, false)
        nav_view.setNavigationItemSelectedListener(this)
        val header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null)
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val hView = navigationView.getHeaderView(0)
        tvMovies = hView.findViewById(R.id.tvMovies)
        ivClose = hView.findViewById(R.id.ivClose)
        tvSpecial = hView.findViewById(R.id.tvSpecial)
        tvTheatres = hView.findViewById(R.id.tvTheatres)
        tvMyAcoount = hView.findViewById(R.id.tvMyAcoount)
        tvMagical = hView.findViewById(R.id.tvMagical)
        tvGiftCards = hView.findViewById(R.id.tvGiftCards)
        tvExperience = hView.findViewById(R.id.tvExperience)
        tvEvents = hView.findViewById(R.id.tvEvents)
        tvAboutus = hView.findViewById(R.id.tvAboutus)
        tvPrivacy = hView.findViewById(R.id.tvPrivacy)
        tvFaq = hView.findViewById(R.id.tvFaq)
        tvContact = hView.findViewById(R.id.tvContact)
        tvTerms = hView.findViewById(R.id.tvTerms)
        tvMore = hView.findViewById(R.id.tvMore)
        tvSignOut = hView.findViewById(R.id.tvSignOut)
        ivBack = hView.findViewById(R.id.ivBackimg)
        contentabout = hView.findViewById(R.id.contentabout)
        content = hView.findViewById(R.id.content)
        btJoinNow = hView.findViewById(R.id.btJoinNow)
        tvBonus = hView.findViewById(R.id.tvBonus)
        tvBack = hView.findViewById(R.id.tvBack)
        tvRegister = hView.findViewById(R.id.tvRegister)

        loadHomeFragment()
        fetchMyAccountData()
        fetchSpecialData()
        fetchOurTheatreData()
        fetchMovieListData()

        AppConstants.putString(AppConstants.KEY_FILTERDATE, "", context)
        AppConstants.selectedGenrestoFilter.clear()
        AppConstants.selectedLanguagestoFilter.clear()
        AppConstants.selectedCinmastoFilter.clear()
        AppConstants.putString(AppConstants.KEY_FILTERLOCDATE, "", context)
        if (AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, this).isNotBlank()) {
            btJoinNow.setText("VIEW BENEFITS")
            tvBonus.setText("MARCUS REWARDS \nMEMBER")
            tvRegister.visibility = View.GONE

        } else {
            btJoinNow.setText("JOIN NOW")
            tvBonus.setText("50 BONUS POINTS")
            tvRegister.visibility = View.VISIBLE
        }

        if (AppConstants.isFromNavDraw) {
            drawer_layout.openDrawer(GravityCompat.END)
        }

        if (AppConstants.getString(AppConstants.KEY_USERID, this).isNotBlank() &&
                AppConstants.getString(AppConstants.KEY_USERID, this).isNotEmpty()) {
            tvSignOut.visibility = View.VISIBLE
        }
        if (!AppConstants.getString(AppConstants.KEY_GUEST_USER, context).equals("")) {
            tvSignOut.visibility = View.GONE
        }
        bottomNavigationView.getMenu().getItem(0).setCheckable(false)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            bottomNavigationView.getMenu().getItem(0).setCheckable(true)
            when (item.itemId) {
                R.id.profile -> {
                    profile()
                }
                R.id.action_movies -> {
                    /*val intent = Intent(this@HomeActivity, MovieListingActivity::class.java)
                    startActivity(intent)*/

                    fetchMovieList()
                }
                R.id.specials -> {
                    /* val intent = Intent(this@HomeActivity, SpecialsActivity::class.java)
                     startActivity(intent)*/
                    fetchSpecial()
                }
                R.id.ourtheatres -> {
                    /* val intent = Intent(this@HomeActivity, OurTheatres::class.java)
                     startActivity(intent)*/
                    fetchOurTheatre()
                }
            }
            true
        }
        tvAboutus.setOnClickListener {
            val i = Intent(context, AboutusActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
        tvFaq.setOnClickListener {
            val i = Intent(context, FaqActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
        tvPrivacy.setOnClickListener {
            val i = Intent(context, PrivacyPolicy::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
        tvContact.setOnClickListener {
            val i = Intent(context, ContactusActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
        tvTerms.setOnClickListener {
            val i = Intent(context, TermsandConditions::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        tvBack.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.END)
        }
        btJoinNow.setOnClickListener {
            //   drawer_layout.closeDrawer(GravityCompat.END)
            AppConstants.isFromNavDraw = true
            val i = Intent(context, MovieRewardsActivity::class.java)
            i.putExtra("sidemenu", "true")
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        tvSpecial.setOnClickListener {
            AppConstants.isFromNavDraw = true
            //  drawer_layout.closeDrawer(GravityCompat.END)
            fetchSpecial()
        }
        tvMovies.setOnClickListener {
            AppConstants.isFromNavDraw = true
            // drawer_layout.closeDrawer(GravityCompat.END)
            fetchMovieList()
        }
        tvMagical.setOnClickListener {
            AppConstants.isFromNavDraw = true
            /* val i = Intent(context, MyAccountScreen::class.java)
             startActivity(i)*/
            AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "magical", context)
            profile()
        }
        tvGiftCards.setOnClickListener {
            AppConstants.isFromNavDraw = true
            /*   val i = Intent(context, MyAccountScreen::class.java)
               startActivity(i)*/
            AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "gift", context)
            profile()
        }

        tvMore.setOnClickListener {
            contentabout.visibility = View.VISIBLE
            content.visibility = View.GONE
        }

        tvMyAcoount.setOnClickListener {
            AppConstants.isFromNavDraw = true
            // drawer_layout.closeDrawer(GravityCompat.END)
            AppConstants.isFromMyAccount = true
            profile()
        }

        tvTheatres.setOnClickListener {
            AppConstants.isFromNavDraw = true
            /*val i = Intent(this@HomeActivity, OurTheatres::class.java)
            startActivity(i)*/
            //  drawer_layout.closeDrawer(GravityCompat.END)
            fetchOurTheatre()
        }
        tvSignOut.setOnClickListener {
            AppConstants.isFromNavDraw = true
            alert("Are you sure you want to Sign out?", "Marcus Theatres")
            {
                positiveButton("YES") { dialog ->
                    AppConstants.putString(AppConstants.KEY_USERID, "", this@HomeActivity)
                    AppConstants.putObject(AppConstants.KEY_USERDATAOBJ, "", context)
                    btJoinNow.setText("JOIN NOW")
                    try {
                        if (AccessToken.getCurrentAccessToken() == null) {
                            // do nothing already logged out
                        }
                        GraphRequest(AccessToken.getCurrentAccessToken(),
                                "/me/permissions/", null, HttpMethod.DELETE,
                                GraphRequest.Callback {
                                    LoginManager.getInstance().logOut()
                                }).executeAsync()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                    tvSignOut.visibility = View.GONE
                    content.visibility = View.VISIBLE
                    contentabout.visibility = View.GONE
                    drawer_layout.closeDrawer(GravityCompat.END)
                    AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, "", context)
                    alert("Signed out Successfully", "Marcus Theatres") {

                        positiveButton("OK") { dialog ->
                            dialog.dismiss()
                        }
                    }.show()



                }
                negativeButton("NO") { dialog ->
                    dialog.dismiss()
                }
            }.show().setCancelable(false)

        }

        ivBack.setOnClickListener {
            contentabout.visibility = View.GONE
            content.visibility = View.VISIBLE
        }
    }

    fun profile() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
        if (AppConstants.getString(AppConstants.KEY_USERID, this).isNotBlank() &&
                AppConstants.getStringList(AppConstants.KEY_USERID, this).isNotEmpty() &&
                AppConstants.getString(AppConstants.KEY_GUEST_USER, context).equals("")) {
            tvSignOut.visibility = View.VISIBLE
            val i = Intent(context, MyAccountScreen::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        } else {
            AppConstants.putString(AppConstants.KEY_FROM, "Navigation", this)
            AppConstants.putBoolean(AppConstants.KEY_BOOKINGFLOW, false, this)
            val i = Intent(context, LoginScreen::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
    }

    private fun fetchSpecial() {
        if (UtilsDialog.isNetworkStatusAvialable(this@HomeActivity)) {
            UtilsDialog.showProgressDialog(this@HomeActivity, "")
            specialsVM.getSpecialsResponse()
        }
    }

    private fun fetchFilteredMovieList() {
        val cinemaList = ArrayList<String>()
        cinemaList.addAll(AppConstants.selectedCinmastoFilter)
        val genrs = AppConstants.selectedGenrestoFilter
        val language = AppConstants.selectedLanguagestoFilter
        val date = AppConstants.getString(AppConstants.KEY_FILTERDATE, context)
        val request = FilteredMoviesReq(date,
                cinemaList, genrs, language, AppConstants.APP_PLATFORM, AppConstants.APP_VERSION)

        if (genrs.size < 1 && language.size < 1 && cinemaList.size < 1 && date.equals("",
                        ignoreCase = true)) {
            AppConstants.is_filterApplied = false
            fetchMovieList()
        } else {
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                homeVM.SchedulesByFilters(request)
            }
        }
    }

    private fun fetchMovieList() {
        val intent = Intent(this@HomeActivity, MovieListingActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
    }

    private fun fetchOurTheatre() {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")

            val preferencesList = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context)
            val longitude = AppConstants.getString(AppConstants.KEY_LONGITUDE, context)
            val latitude = AppConstants.getString(AppConstants.KEY_LATITUDE, context)
            if (longitude.isNotBlank() && latitude.isNotBlank()) {
                val req = OTGPSReq(
                        AppConstants.APP_PLATFORM,
                        AppConstants.APP_VERSION,
                        latitude,
                        longitude,
                        preferencesList
                )
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    theatresVM.fetchOTGPSDetails(req)
                }
            } else {
                val req = OTNonGPSReq(
                        AppConstants.APP_PLATFORM,
                        AppConstants.APP_VERSION,
                        preferencesList
                )
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    theatresVM.fetchOTNonGPSDetails(req)
                }
            }
        }
    }

    private fun loadHomeFragment() {
        val fragment = HomeFragment.newInstance()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.addToBackStack(null)
        ft.commit()

    }

    private fun fetchMyAccountData() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getMyAccountData().observe(this, myAccountObs)
        myAccountVM.getUpdateAccountData().observe(this, updateAccountObs)
        myAccountVM.getApiErrorData().observe(this, errorObs)
    }

    private fun fetchSpecialData() {
        specialsVM = ViewModelProviders.of(this).get(SpecialsVM::class.java)
        specialsVM.getSpecialsData().observe(this, specialObs)
        specialsVM.getApiErrorData().observe(this, errorObs)
    }

    private fun fetchOurTheatreData() {
        theatresVM = ViewModelProviders.of(this).get(TheatresVM::class.java)
        theatresVM.getApiErrorData().observe(this, errorObs)
        theatresVM.getOTGPSDetails().observe(this, otgpsRespObs)
        theatresVM.getOTNonGPSDetails().observe(this, otnongpsRespObs)
    }

    private fun fetchMovieListData() {
        homeVM = ViewModelProviders.of(this).get(HomeVm::class.java)
        homeVM.getApiErrorData().observe(this, errorObs)
        homeVM.getSchedulesByPreferences().observe(this, homeRespObs)
    }

    private var myAccountObs = object : Observer<MyAccountResp> {
        override fun onChanged(t: MyAccountResp?) {

            //UtilsDialog.hideProgress()

            if (t!!.STATUS == true) {
                AppConstants.putObject(AppConstants.KEY_MYACCOUNT, t, context)
                val i = Intent(context, MyAccountScreen::class.java)
                i.putExtra("modify", true)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

            } else {
                alert(t!!.DATA.message, "Marcus Theatres") {

                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()
                UtilsDialog.hideProgress()
                //todo save data and proceed to seatlayout activity
            }
        }
    }

    private var updateAccountObs = object : Observer<UpdateResponse> {
        override fun onChanged(t: UpdateResponse?) {
            UtilsDialog.hideProgress()
            drawer_layout.closeDrawer(GravityCompat.END)
            alert("Updated Successfully", "Marcus Theatres") {

                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    private var specialObs = object : Observer<SpecialResp> {

        override fun onChanged(t: SpecialResp?) {

            if (t!!.STATUS == true) {
                AppConstants.putObject(AppConstants.KEY_SPECIAL, t, context)
                val i = Intent(context, SpecialsActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

            } else {
                alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()
                UtilsDialog.hideProgress()
                //toast(t.STATUS.toString())
            }
        }
    }

    private var otgpsRespObs = object : Observer<OTGPSResp> {
        override fun onChanged(t: OTGPSResp?) {
            if (t!!.STATUS) {

                AppConstants.putObject(AppConstants.KEY_OUTTHEATRES_OTGPS, t, context)
                val i = Intent(context, OurTheatres::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

            } else {
                UtilsDialog.hideProgress()
                alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {
                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }

    private var otnongpsRespObs = object : Observer<OTNonGPSResp> {
        override fun onChanged(t: OTNonGPSResp?) {
            if (t!!.STATUS) {
                AppConstants.putObject(AppConstants.KEY_OUTTHEATRES_OTNONGPS, t, context)
                val i = Intent(context, OurTheatres::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)


            } else {
                UtilsDialog.hideProgress()

                alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {
                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }

    private var homeRespObs = object : Observer<HomeResponse> {

        override fun onChanged(t: HomeResponse?) {

            if (t!!.DATA.movies != null) {
                AppConstants.putObject(AppConstants.KEY_MOVIELIST, t, context)
                val intent = Intent(this@HomeActivity, MovieListingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

            } else {
                UtilsDialog.hideProgress()
            }
        }
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            LogUtils.i("response", t!!)
            alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    private fun loadProfileFragment() {

//        val fragment = QRLogin.newInstance()
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.container, fragment)
//        ft.addToBackStack(null)
//        ft.commit()
        startActivity(Intent(this@HomeActivity, QRLoginnew::class.java))
    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            if (doubleBackToExitPressedOnce) {
                finish()
                overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)

                return
            }

            this.doubleBackToExitPressedOnce = true

            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    fun toggleNavigationDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            if (AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, this).isNotBlank()) {

                btJoinNow.setText("VIEW BENEFITS")
                tvBonus.setText("MARCUS REWARDS \nMEMBER")
                tvRegister.visibility = View.GONE

            } else {
                btJoinNow.setText("JOIN NOW")
                tvBonus.setText("50 BONUS POINTS")
                tvRegister.visibility = View.VISIBLE
            }
            drawer_layout.openDrawer(GravityCompat.END)
            AppConstants.isFromNavDraw = false

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            //   R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        /* when (item.itemId) {
             R.id.nav_camera -> {
                 // Handle the camera action
             }
             R.id.nav_gallery -> {

             }
             R.id.nav_slideshow -> {

             }
             R.id.nav_manage -> {

             }
             R.id.nav_share -> {

             }
             R.id.nav_send -> {

             }
         }*/

        drawer_layout.closeDrawer(GravityCompat.END)
        if (AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, this).isNotBlank()) {

            btJoinNow.setText("VIEW BENEFITS")
            tvBonus.setText("MARCUS REWARDS \nMEMBER")
            tvRegister.visibility = View.GONE

        } else {
            btJoinNow.setText("JOIN NOW")
            tvBonus.setText("50 BONUS POINTS")
            tvRegister.visibility = View.VISIBLE
        }
        return true
    }

    fun SetShiftMode(bottomNavigationView: BottomNavigationView, enableShiftMode: Boolean, enableItemShiftMode: Boolean) {
        try {
            var menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
            if (menuView == null) {
              //  Toast.makeText(context, "Unable to find BottomNavigationMenuView", Toast.LENGTH_SHORT).show()
                return
            }

            var shiftMode: Field = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftMode.isAccessible = true
            shiftMode.setBoolean(menuView, enableShiftMode)
            shiftMode.isAccessible = false

            for (i in 0 until menuView.childCount) {
                var item = menuView.getChildAt(i) as BottomNavigationItemView;
                if (item == null)
                    continue
                item.setShiftingMode(enableItemShiftMode)
                item.setChecked(item.itemData.isChecked)
            }
            menuView.updateMenuView()
        } catch (ex: Exception) {
        }
    }

    override fun onStart() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mGoogleApiClient.connect()

        super.onStart()
    }

    override fun joinnow() {
        btJoinNow.setText("JOIN NOW")
        tvBonus.setText("50 BONUS POINTS")
        tvRegister.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, this).isNotBlank()) {
            btJoinNow.setText("VIEW BENEFITS")
            tvBonus.setText("MARCUS REWARDS \nMEMBER")
            tvRegister.visibility = View.GONE
        } else {
            btJoinNow.setText("JOIN NOW")
            tvBonus.setText("50 BONUS POINTS")
            tvRegister.visibility = View.VISIBLE
        }
        bottomNavigationView.getMenu().getItem(0).setCheckable(false)
        bottomNavigationView.getMenu().getItem(1).setCheckable(false)
        bottomNavigationView.getMenu().getItem(2).setCheckable(false)
        bottomNavigationView.getMenu().getItem(3).setCheckable(false)
    }

    override fun viewBenefits() {
        btJoinNow.setText("VIEW BENEFITS")
        tvBonus.setText("MARCUS REWARDS \nMEMBER")
        tvRegister.visibility = View.GONE
    }

}



interface homeuihelper {
    fun joinnow()
    fun viewBenefits()
}