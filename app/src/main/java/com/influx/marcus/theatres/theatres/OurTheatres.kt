package com.influx.marcus.theatres.theatres

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.AllTheatre
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSResp
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.Preference
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSResp
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Theatre
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.customview.WrapContentHeightViewPager
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.homepage.RecyclerTouchListener
import com.influx.marcus.theatres.theatres.NonGPSLocs.AutocompleteCustomArrayAdapter
import com.influx.marcus.theatres.theatres.NonGPSLocs.CustomAutoCompleteTextChangedListener
import com.influx.marcus.theatres.theatres.NonGPSLocs.LocationListFragment
import com.influx.marcus.theatres.theatres.WithGPSLocs.WithAutocompleteCustomAdapter
import com.influx.marcus.theatres.theatres.WithGPSLocs.WithCuctomAutoCompleteTextChanged
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.toolbar_back_title.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class OurTheatres : AppCompatActivity() {


    lateinit var theatresVM: TheatresVM
    private var otgpsResp: OTGPSResp? = null
    private var otnongpsResp: OTNonGPSResp? = null

    private val context: Context = this
    private lateinit var rvPreferredLocationHorizontal: RecyclerView
    private lateinit var rvSearch: RecyclerView
    private lateinit var rvNearLocationsGPS: RecyclerView
    private lateinit var tabsStateVP: TabLayout
    private lateinit var adapterNearyou: NearYouAdapter
    private lateinit var clGPSLayout: ConstraintLayout
    private lateinit var clNonGPSLayout: ConstraintLayout
    private lateinit var clOurTheatres: ConstraintLayout
    private lateinit var vpStateLocations: WrapContentHeightViewPager
    private lateinit var nestedscrollview_ourtheatre: NestedScrollView
    private var backvalue = ""
    lateinit var etSearchTheatre: CustomAutoCompleteView
    var tvNoDataOutTheatre: TextView? = null

    var allTheatres: ArrayList<AllTheatre> = ArrayList<AllTheatre>()
    var preferenses: ArrayList<Preference> = ArrayList<Preference>()
    var nonallTheatres: ArrayList<com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.AllTheatre> = ArrayList<com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.AllTheatre>()
    var nonTheatre: ArrayList<Theatre> = ArrayList<Theatre>()

    var withallTheatres: ArrayList<AllTheatre> = ArrayList<AllTheatre>()

    lateinit var searchAdapter: AutocompleteCustomArrayAdapter
    lateinit var withsearchAdapter: WithAutocompleteCustomAdapter
    var locationTabFragments: MutableList<LocationListFragment> = ArrayList<LocationListFragment>()
    lateinit var locationtabAdapter: TabAdapter

    private val webApi: ApiInterface = RestClient.getApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_theatres)
        if (intent.hasExtra("back")) {
            backvalue = intent.getStringExtra("back")
        }
        theatreObserver()
        initViews()
        getTheatresListUpdateUi()
    }

    private fun getTheatresListUpdateUi() {
        val preferencesList = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context)
        val longitude = AppConstants.getString(AppConstants.KEY_LONGITUDE, context)
        val latitude = AppConstants.getString(AppConstants.KEY_LATITUDE, context)
        if (longitude.isNotBlank() && latitude.isNotBlank()) {
            clGPSLayout.visibility = View.VISIBLE
            clNonGPSLayout.visibility = View.GONE
            val req = OTGPSReq(
                    AppConstants.APP_PLATFORM,
                    AppConstants.APP_VERSION,
                    latitude,
                    longitude,
                    preferencesList
            )

            try {
                otgpsResp = AppConstants.getObject(AppConstants.KEY_OUTTHEATRES_OTGPS,
                        applicationContext, OTGPSResp::class.java as Class<Any>) as? OTGPSResp

                if (otgpsResp != null) {
                    otgpsRespObs.onChanged(otgpsResp)
                } else {
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        theatresVM.fetchOTGPSDetails(req)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            clGPSLayout.visibility = View.GONE
            clNonGPSLayout.visibility = View.VISIBLE
            val req = OTNonGPSReq(
                    AppConstants.APP_PLATFORM,
                    AppConstants.APP_VERSION,
                    preferencesList
            )

            otnongpsResp = AppConstants.getObject(AppConstants.KEY_OUTTHEATRES_OTNONGPS,
                    applicationContext, OTNonGPSResp::class.java as Class<Any>) as? OTNonGPSResp

            if (otnongpsResp != null) {
                otnongpsRespObs.onChanged(otnongpsResp)
            } else {
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    theatresVM.fetchOTNonGPSDetails(req)
                }
            }
        }
    }

    private fun initViews() {
        ivBackToolbar.setOnClickListener {
            onBackPressed()
        }

        rvPreferredLocationHorizontal = findViewById(R.id.rvHorizontalPreferredLocations)
        rvSearch = findViewById(R.id.rvSearch)
        rvPreferredLocationHorizontal.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)
        rvNearLocationsGPS = findViewById(R.id.rvTheatresListGPS)
        tabsStateVP = this.findViewById(R.id.tabStateLocs)
        clGPSLayout = findViewById(R.id.clTheatresNearYouGPS)
        clNonGPSLayout = findViewById(R.id.clTheatresLocationNonGps)
        vpStateLocations = findViewById(R.id.vpTheatreLocationNonGPS) as WrapContentHeightViewPager
        nestedscrollview_ourtheatre = findViewById(R.id.nestedscrollview_ourtheatre) as NestedScrollView
        etSearchTheatre = findViewById(R.id.etSearchLocations) as CustomAutoCompleteView
        clOurTheatres = findViewById(R.id.clOurTheatres) as ConstraintLayout
        tvNoDataOutTheatre = findViewById(R.id.tvNoDataOutTheatre) as TextView

        rvPreferredLocationHorizontal.setNestedScrollingEnabled(false)
        rvSearch.setNestedScrollingEnabled(false)
        rvNearLocationsGPS.setNestedScrollingEnabled(false)
        vpStateLocations.setNestedScrollingEnabled(false)

        etSearchTheatre.setCursorVisible(false)

        etSearchTheatre.setOnTouchListener { v, event ->
            etSearchTheatre.setCursorVisible(true)
            false
        }

        // set the custom ArrayAdapter, val theatresSHow: OurTheatres.fetchTheatreShowTime

    }

    fun displayMethod() {
        val longitude = AppConstants.getString(AppConstants.KEY_LONGITUDE, context)
        val latitude = AppConstants.getString(AppConstants.KEY_LATITUDE, context)

        if (longitude.isNotBlank() && latitude.isNotBlank()) {
            etSearchTheatre.addTextChangedListener(WithCuctomAutoCompleteTextChanged(this))
            //withsearchAdapter = WithAutocompleteCustomAdapter(this, R.layout.adapter_theatre_search, allTheatres)
            //etSearchTheatre.setAdapter(withsearchAdapter)
            withsearchAdapter = WithAutocompleteCustomAdapter(this, allTheatres)
            val mLayoutManager = LinearLayoutManager(applicationContext)
            rvSearch.setLayoutManager(mLayoutManager)
            rvSearch.setItemAnimator(DefaultItemAnimator())
            rvSearch.getRecycledViewPool().clear()
            rvSearch.adapter = withsearchAdapter

            /*rvSearch.addOnItemTouchListener(RecyclerTouchListener(context,
                    RecyclerTouchListener.OnItemClickListener { view, position ->
                        val i = Intent(context, TheatreShowTimeActivity::class.java)
                        AppConstants.putString(AppConstants.KEY_THEATRECODE, allTheatres.get(position).code, context!!)
                        AppConstants.putString(AppConstants.KEY_THEATREIMAGE, allTheatres.get(position).image_url, context!!)
                        AppConstants.putString(AppConstants.KEY_THEATRENAME, allTheatres.get(position).name, context!!)
                        context.startActivity(i)
                    }
            ))*/


        } else {
            etSearchTheatre.addTextChangedListener(CustomAutoCompleteTextChangedListener(this))
            searchAdapter = AutocompleteCustomArrayAdapter(this, nonTheatre)
            val mLayoutManager = LinearLayoutManager(applicationContext)
            rvSearch.setLayoutManager(mLayoutManager)
            rvSearch.setItemAnimator(DefaultItemAnimator())
            rvSearch.getRecycledViewPool().clear()
            rvSearch.setAdapter(searchAdapter)

            /*rvSearch.addOnItemTouchListener(RecyclerTouchListener(context,
                    RecyclerTouchListener.OnItemClickListener { view, position ->

                        val myTheatre: ArrayList<Theatre> = ArrayList<Theatre>()
                        for (j in 0 until nonallTheatres.size) {
                            for (eachTheatre in nonallTheatres.get(j).theatres) {
                                myTheatre.add(eachTheatre)
                            }
                        }

                        val i = Intent(context, TheatreShowTimeActivity::class.java)
                        AppConstants.putString(AppConstants.KEY_THEATRECODE, myTheatre.get(position).code, context!!)
                        AppConstants.putString(AppConstants.KEY_THEATREIMAGE, myTheatre.get(position).image_url, context!!)
                        AppConstants.putString(AppConstants.KEY_THEATREIMAGE, "", context!!)
                        AppConstants.putString(AppConstants.KEY_THEATRENAME, myTheatre.get(position).name, context!!)
                        context.startActivity(i)
                    }
            ))*/
        }
    }

    private fun theatreObserver() {
        theatresVM = ViewModelProviders.of(this).get(TheatresVM::class.java)
        theatresVM.getApiErrorData().observe(this, errorObs)
        theatresVM.getOTGPSDetails().observe(this, otgpsRespObs)
        theatresVM.getOTNonGPSDetails().observe(this, otnongpsRespObs)

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

    private var otgpsRespObs = object : Observer<OTGPSResp> {
        override fun onChanged(t: OTGPSResp?) {
            if (t!!.STATUS) {
                populateGpsPreferredLocations(t.DATA.preferences)
                populateGpsNearbyRecyclerview(t.DATA.allTheatres)

                allTheatres = t.DATA.allTheatres as ArrayList<AllTheatre>
                //preferenses = t.DATA.preferences as ArrayList<Preference>

                displayMethod()
                UtilsDialog.hideProgress()
            } else {
                UtilsDialog.hideProgress()
                alert(getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            }
        }
    }

    private fun populateGpsNearbyRecyclerview(allTheatres: List<AllTheatre>) {

        adapterNearyou = NearYouAdapter(context, allTheatres)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rvNearLocationsGPS.setLayoutManager(mLayoutManager)
        rvNearLocationsGPS.setItemAnimator(DefaultItemAnimator())
        rvNearLocationsGPS.getRecycledViewPool().clear()
        rvNearLocationsGPS.setAdapter(adapterNearyou)

        rvNearLocationsGPS.addOnItemTouchListener(RecyclerTouchListener(context,
                RecyclerTouchListener.OnItemClickListener { view, position ->
                    AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "theatrelist", context)
                    AppConstants.putString(AppConstants.KEY_THEATRECODE, allTheatres.get(position).code, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATREID, allTheatres.get(position).code, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATREIMAGE, allTheatres.get(position).image_url, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATRENAME, allTheatres.get(position).name, context!!)

                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        val req = TheatreShowtimeRequest(
                                allTheatres.get(position).code,
                                AppConstants.APP_VERSION,
                                AppConstants.APP_PLATFORM)
                        getTheatreDetailsList(req)
                    }
                }
        ))
    }

    private fun populateGpsPreferredLocations(preferences: List<Preference>) {
        val prefLocsAdapter = PreferredLocsAdapter(context, preferences)
        rvPreferredLocationHorizontal.adapter = prefLocsAdapter

        rvPreferredLocationHorizontal.addOnItemTouchListener(RecyclerTouchListener(context,
                RecyclerTouchListener.OnItemClickListener { view, position ->
                    AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "theatrelist", context)
                    AppConstants.putString(AppConstants.KEY_THEATRECODE, preferences.get(position).theatre_code, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATREIMAGE, preferences.get(position).image_url, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATRENAME, preferences.get(position).name, context!!)
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        val req = TheatreShowtimeRequest(
                                preferences.get(position).theatre_code,
                                AppConstants.APP_VERSION,
                                AppConstants.APP_PLATFORM)
                        getTheatreDetailsList(req)
                    }
                }
        ))

    }

    private var otnongpsRespObs = object : Observer<OTNonGPSResp> {
        override fun onChanged(t: OTNonGPSResp?) {
            if (t!!.STATUS) {
                populateNongpsPreferred(t.DATA.preferences)
                populateNongpsLocationViewPager(t.DATA.allTheatres)

                nonallTheatres = t.DATA.allTheatres as ArrayList<com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.AllTheatre>

                for (i in 0 until nonallTheatres.size) {
                    nonTheatre = t.DATA.allTheatres.get(i).theatres as ArrayList<Theatre>
                }

                displayMethod()
                //nonpreferenses = t.DATA.preferences as ArrayList<com.ncrcinema.movietime.marcus.api.ApiModels.OurTheatresNonGPS.Preference>
                UtilsDialog.hideProgress()

            } else {
                UtilsDialog.hideProgress()
                alert(getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    private fun populateNongpsLocationViewPager(allTheatres: List<com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.AllTheatre>) {

        if (allTheatres.size > 0) {
            locationTabFragments.clear()
        }

        for (eachTab in allTheatres) {
            val fragment = LocationListFragment.newInstance(eachTab.state,
                    eachTab.theatres, theatresSHow)
            val bundle = Bundle()
            bundle.putSerializable("theatrelist", eachTab.theatres as Serializable?)
            fragment.setArguments(bundle)
            locationTabFragments.add(fragment)
        }
        //LogUtils.d("OURTHEATRES", "Size of fragments ${locationTabFragments.size}")
        locationtabAdapter = TabAdapter(supportFragmentManager, locationTabFragments)
        vpStateLocations.setAdapter(locationtabAdapter)
        vpStateLocations.offscreenPageLimit = locationTabFragments.size
        tabsStateVP.setupWithViewPager(vpStateLocations);
        tabsStateVP.setTabGravity(TabLayout.GRAVITY_FILL)

        vpStateLocations.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsStateVP))
        tabsStateVP.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vpStateLocations.currentItem = tab.position
                vpStateLocations.reMeasureCurrentPage(tab.position)
                nestedscrollview_ourtheatre.scrollTo(0, 0)
                //locationtabAdapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

    }

    class TabAdapter(fm: FragmentManager?, locslistFrags: MutableList<LocationListFragment>) : FragmentPagerAdapter(fm) {

        //private var locationTabFragments = locslistFrags as ArrayList<LocationListFragment>
        private val locationTabFragments = locslistFrags as ArrayList<LocationListFragment>

        override fun getItem(position: Int): Fragment {
            return locationTabFragments!![position]
        }

        override fun getCount(): Int {
            return locationTabFragments!!.size
            //return if (locationTabFragments.size == 0) 0 else locationTabFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return locationTabFragments!!.get(position).getTabname()
        }
    }

    /*private fun setupViewPager(viewPager: ViewPager) {
        val viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.adapter = viewpagerAdapter
    }*/

    private fun populateNongpsPreferred(
            preferences: List<com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Preference>) {
        val prefLocsAdapter = PreferredLocsAdapter(context, preferences)
        rvPreferredLocationHorizontal.adapter = prefLocsAdapter

        rvPreferredLocationHorizontal.addOnItemTouchListener(RecyclerTouchListener(context,
                RecyclerTouchListener.OnItemClickListener { view, position ->

                    AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "theatrelist", context)
                    AppConstants.putString(AppConstants.KEY_THEATRECODE, preferences.get(position).theatre_code, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATREIMAGE, preferences.get(position).image_url, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATRENAME, preferences.get(position).name, context!!)

                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        val req = TheatreShowtimeRequest(
                                preferences.get(position).theatre_code,
                                AppConstants.APP_VERSION,
                                AppConstants.APP_PLATFORM)
                        getTheatreDetailsList(req)
                    }
                }
        ))
    }


    /**
     * api call to fetch theatreshowtime
     */
    fun getTheatreDetailsList(req: TheatreShowtimeRequest) {
        UtilsDialog.showProgressDialog(context, "")
        val theatreListCall: Call<TheatreShowtimeResp> = webApi.getTheatreDetails(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        theatreListCall.enqueue(object : Callback<TheatreShowtimeResp> {
            override fun onFailure(call: Call<TheatreShowtimeResp>, t: Throwable) {
                UtilsDialog.hideProgress()

            }

            override fun onResponse(call: Call<TheatreShowtimeResp>,
                                    response: Response<TheatreShowtimeResp>) {
                UtilsDialog.hideProgress()
                if (response.isSuccessful) {
                        if (response.body()!!.STATUS) {
                            val theatreShowtimeResponse = response.body()
                            AppConstants.putObject(AppConstants.KEY_THEATRESHOWTIMEOBJ, theatreShowtimeResponse!!, context)
                                val i = Intent(context, TheatreShowTimeActivity::class.java)
                                AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "theatrelist", context)
                                context.startActivity(i)
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                        } else {
                            alert(response.body()!!.DATA.message, "Marcus Theatres") {
                                okButton {
                                    it.dismiss()
                                }
                            }.show()
                        }

                } else {
                    alert("No Showtimes Available", "Marcus Theatres") {
                        okButton {
                            it.dismiss()
                        }
                    }.show()
                }
            }
        })
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(getWindowToken(), 0)
    }

    override fun onBackPressed() {
        if (rvSearch.visibility == View.VISIBLE) {
            rvSearch.visibility = View.GONE
            clOurTheatres.visibility = View.VISIBLE
        } else {
            if (backvalue.equals("home")) {
                val homeIntent = Intent(context, HomeActivity::class.java)
                startActivity(homeIntent)
                finish()
                overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
            } else {
                super.onBackPressed()
                overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
            }
        }
        //etSearchTheatre.hideKeyboard()
    }

    var theatresSHow = object : OurTheatres.fetchTheatreShowTime {

        override fun fetchTheareShowtimeByCode(req: TheatreShowtimeRequest) {
            getTheatreDetailsList(req)
        }
    }

    interface fetchTheatreShowTime {
        fun fetchTheareShowtimeByCode(req: TheatreShowtimeRequest)
    }
}


