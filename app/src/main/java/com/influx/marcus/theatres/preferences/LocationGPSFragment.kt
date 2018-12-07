package com.influx.marcus.theatres.preferences


import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.*
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.Group
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsReq
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.Theatres
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.subval.Data
import com.influx.marcus.theatres.api.pref.CinemaDetail
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.fragment_location_g.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap


class LocationGPSFragment : Fragment(), SeekBar.OnSeekBarChangeListener {


    companion object {
        private var isEditMode = false
        private lateinit var uiHelper: ActivityUiHelper
        fun newInstance(editMode: Boolean, uiListener: ActivityUiHelper) = LocationGPSFragment().apply {
            isEditMode = editMode
            uiHelper = uiListener
        }
    }


    var stateCode = ""
    private lateinit var mcontext: Context
    private lateinit var prefVM: PreferenceVM
    private lateinit var spnLocation: Spinner
    private lateinit var seekbar: SeekBar
    private lateinit var tvLimit: TextView
    private lateinit var expandableListDataAdapter: ExpandableListAdapter
    private lateinit var lvExp: ExpandableListView
    private lateinit var etCityname: EditText
    private lateinit var prefData: PreferedLocsResp
    private lateinit var constrainLayoutPrt: ConstraintLayout
    private lateinit var constraintSet: ConstraintSet
    private lateinit var viewLineSeekbar: View
    private lateinit var grpSeekText: Group
    var selectedCinema = ArrayList<String>()
    private lateinit var rvSelectedCinema: RecyclerView
    lateinit var cityList: MutableList<String>
    lateinit var cinemasList: java.util.HashMap<String, MutableList<Data>>
    lateinit var totalCinemaList: HashMap<String, MutableList<Data>>
    lateinit var totalCityList: MutableList<String>
    private lateinit var llChooseManualFlow: LinearLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationManager: LocationManager? = null
    private var currentSearchRadius = 50
    private lateinit var horizontalCinemanameAdapter: SelectedCinemaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_location_g, container,
                false)
        getActivity()!!.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mcontext = this!!.activity!!
        initViews(rootView)
        registerObservers()
        fetchDataForCurrentLatlongs()
        //todo only incase required
//        if (isEditMode) {
//            fetchCurrentLocationAndPopulate()
//        } else {
//            setupStateListSpinnerAndcallForPrefrences()
//        }
        return rootView
    }


    private fun registerObservers() {
        prefVM = ViewModelProviders.of(this).get(PreferenceVM::class.java)
        prefVM.getPreferedLocationData().observe(this, preferedRespObs)
        prefVM.getApiErrorData().observe(this, apiErrorObs)
    }

    private fun initViews(rootView: View) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        spnLocation = rootView.findViewById(R.id.spnLocation)
        spnLocation.visibility = View.GONE
        seekbar = rootView.findViewById(R.id.seekbar)
        seekbar.setOnSeekBarChangeListener(this)
        lvExp = rootView.findViewById(R.id.lvExp)
        tvLimit = rootView.findViewById(R.id.tvLimitTxt)
        rvSelectedCinema = rootView.findViewById(R.id.rvSelectedCinema)
        rvSelectedCinema.setHasFixedSize(true)
        rvSelectedCinema.setLayoutManager(LinearLayoutManager(mcontext,
                LinearLayoutManager.HORIZONTAL, false))
        horizontalCinemanameAdapter = SelectedCinemaAdapter(selectedCinema, mcontext, selectedCinemaListener)
        if (selectedCinema.size > 0) {
            rvSelectedCinema.visibility = View.VISIBLE
        } else {
            rvSelectedCinema.visibility = View.GONE
        }
        rvSelectedCinema.adapter = horizontalCinemanameAdapter

        etCityname = rootView.findViewById(R.id.etCityname)
        etCityname.setImeOptions(EditorInfo.IME_ACTION_DONE)
        etCityname.setSingleLine()
        etCityname.setPressed(true);
        llChooseManualFlow = rootView.findViewById(R.id.llChooseLocManually)
        llChooseManualFlow.setOnClickListener {
            if (locationManager != null) {
                locationManager!!.removeUpdates(locationListener)
            }
            uiHelper.gotoManualFlow()
        }
        etCityname.setOnTouchListener(View.OnTouchListener { v, event ->
            etCityname.setFocusableInTouchMode(true)

            false
        })

        viewLineSeekbar = rootView.findViewById(R.id.viewLineSeekbar)
        constrainLayoutPrt = rootView.findViewById(R.id.scrollLayoutParent)
        grpSeekText = rootView.findViewById(R.id.grpTextSeekBar)
        etCityname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.length == 0) {
                    etCityname.setTextSize(11f)
                } else {
                    etCityname.setTextSize(16f)
                }
                filterByText(cs.toString())
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int,
                                           arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })

    }

    private fun filterByText(search: String) {

        val cityList: MutableList<String> = prepareListOfCities(prefData!!.DATA.theatres,
                currentSearchRadius, search)
        val cinemasList: HashMap<String, MutableList<Data>> =
                prepareCinemasList(prefData!!.DATA.theatres, currentSearchRadius, search)


        val posOfPrefItems: ArrayList<Int> = ArrayList()
        cityList.forEachIndexed { grpIndex, s ->
            cinemasList[s]!!.forEachIndexed { index, data ->
                if (data.code in AppConstants.cinemaList) {
                    data.isPrefered = true
                    posOfPrefItems.add(grpIndex)
                } else {
                    data.isPrefered = false
                }
            }
        }

        expandableListDataAdapter = ExpandableListAdapter(mcontext, cityList, cinemasList,
                uiHelper, expandableListlistener)
        // setting list adapter

        lvExp!!.setAdapter(expandableListDataAdapter)
        if (expandableListDataAdapter.groupCount != 0) {
            tvNoLocationFound.visibility = View.GONE
        } else {
            tvNoLocationFound.visibility = View.VISIBLE
        }
        for (position in posOfPrefItems) {
            lvExp.expandGroup(position)
        }

    }

    private fun fetchDataForCurrentLatlongs() {
        if (AppConstants.longitude.isNotBlank()) {
            UtilsDialog.showProgressDialog(mcontext, "")
            val req = PreferedLocsReq(
                    AppConstants.AUTHORISATION_HEADER,
                    AppConstants.state,
                    AppConstants.latitute,
                    AppConstants.longitude
            )
            prefVM.fetchPreferedLocationData(req)
        } else {

            launch(UI) {
                try {
                    val result = askPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    startLocationProvider()
                } catch (e: PermissionException) {
                    e.denied.forEach {
                        if (locationManager != null) {
                            locationManager!!.removeUpdates(locationListener)
                        }
                        uiHelper.gotoManualFlow()
                    }
                    e.accepted.forEach {

                    }
                }
            }
        }
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (progress < 1) {
            tvLimit.setText("1 mile ")
        } else if (progress == seekbar.max - 1) {
            tvLimit.setText(seekbar.max.toString() + " mile")
        } else {
            tvLimit.setText("${progress} mile ")
        }
        filterByMiles(progress)
        currentSearchRadius = progress
    }

    private fun filterByMiles(progress: Int) {
        try {
            var progressRound = progress + 1

            val cityList: MutableList<String> = prepareListOfCities(prefData!!.DATA.theatres,
                    progressRound)
            val cinemasList: HashMap<String, MutableList<Data>> = prepareCinemasList(prefData!!.DATA.theatres,
                    progressRound)

            val posOfPrefItems: ArrayList<Int> = ArrayList()
            cityList.forEachIndexed { grpIndex, s ->
                cinemasList[s]!!.forEachIndexed { index, data ->
                    if (data.code in AppConstants.cinemaList) {
                        data.isPrefered = true
                        posOfPrefItems.add(grpIndex)
                    } else {
                        data.isPrefered = false
                    }
                }
            }

            expandableListDataAdapter = ExpandableListAdapter(mcontext, cityList, cinemasList,
                    uiHelper, expandableListlistener)
            // setting list adapter

            lvExp!!.setAdapter(expandableListDataAdapter)
            if (expandableListDataAdapter.groupCount != 0) {
                tvNoLocationFound.visibility = View.GONE
            } else {
                tvNoLocationFound.visibility = View.VISIBLE
            }
            for (position in posOfPrefItems) {
                lvExp.expandGroup(position)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private var apiErrorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
            LogUtils.d("LocationGPS", " Error - $t!!")
        }
    }

    private var preferedRespObs = object : Observer<PreferedLocsResp> {
        override fun onChanged(t: PreferedLocsResp?) {
            (activity as PreferenceActivity).preferedLocsResp = t
            if (t!!.STATUS) {
                updateUiForLocations(t)
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

    private fun updateUiForLocations(prefLocations: PreferedLocsResp?) {
        //update seekbar logic here

        constraintSet = ConstraintSet()
        constraintSet.clone(constrainLayoutPrt)
        prefData = prefLocations!!
        val maxSeek = prefLocations!!.DATA.theatres.cities.last().data.last().miles
        if (maxSeek < 1) {// hide for non miles data
            constraintSet.connect(viewLineSeekbar.id, ConstraintSet.TOP, spnLocation.id, ConstraintSet.BOTTOM, 10)
            constraintSet.applyTo(constrainLayoutPrt)
            grpSeekText.visibility = View.GONE
        }
        try {
            var maxvalue = prefLocations!!.DATA!!.default_radius.toFloat()
            seekbar.max = maxvalue.toInt()
            var progress = prefLocations!!.DATA!!.current_radius.toFloat()
            seekbar.progress = progress.toInt()
            tvLimit.setText("${seekbar.progress} mile")
        } catch (e: Exception) {
            e.printStackTrace()
        }


        cityList = prepareListOfCities(prefLocations!!.DATA.theatres, 50)
        totalCityList = cityList
        cinemasList = prepareCinemasList(prefLocations!!.DATA.theatres, 50)
        totalCinemaList = cinemasList
        //etCityname.setText("")
        val posOfPrefItems: ArrayList<Int> = ArrayList()
        cityList.forEachIndexed { grpIndex, s ->
            cinemasList[s]!!.forEachIndexed { index, data ->
                if (data.code in AppConstants.cinemaList) {
                    data.isPrefered = true
                    posOfPrefItems.add(grpIndex)
                } else {
                    data.isPrefered = false
                }
            }
        }

        for (eachPrefLocation in AppConstants.localCinemadata) {
            addItemToHorizontalrv(eachPrefLocation.cinemaName)
        }


        expandableListDataAdapter = ExpandableListAdapter(mcontext, cityList, cinemasList,
                uiHelper, expandableListlistener)
        // setting list adapter

        lvExp!!.setAdapter(expandableListDataAdapter)
        if (expandableListDataAdapter.groupCount != 0) {
            tvNoLocationFound.visibility = View.GONE
        } else {
            tvNoLocationFound.visibility = View.VISIBLE
        }
        for (position in posOfPrefItems) {
            lvExp.expandGroup(position)
        }
        UtilsDialog.hideProgress()


        lvExp.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id ->
            false
        })

        lvExp.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            false
        })
        filterByMiles(50)
    }

    /**
     * prepare data for filter text/miles and adapter
     */

    private fun prepareCinemasList(theatres: Theatres): HashMap<String, MutableList<Data>> {
        val cinemasList = HashMap<String, MutableList<Data>>()
        for (eachCity in theatres.cities) {
            val cinemasListPerCity = ArrayList<Data>()
            for (eachCinema in eachCity.data) {
                cinemasListPerCity.add(eachCinema)
            }
            cinemasList.put(eachCity.cityname, cinemasListPerCity)
        }
        return cinemasList
    }

    private fun prepareCinemasList(theatres: Theatres, searchMiles: Int): HashMap<String, MutableList<Data>> {
        val cinemasList = HashMap<String, MutableList<Data>>()
        for (eachCity in theatres.cities) {
            val cinemasListPerCity = ArrayList<Data>()
            for (eachCinema in eachCity.data) {
                if (eachCinema.miles < searchMiles) {
                    cinemasListPerCity.add(eachCinema)
                }
            }
            cinemasList.put(eachCity.cityname, cinemasListPerCity)
        }
        return cinemasList
    }

    private fun prepareCinemasList(theatres: Theatres, search: String): HashMap<String, MutableList<Data>> {
        val cinemasList = java.util.HashMap<String, MutableList<Data>>()
        for (eachCity in theatres.cities) {
            val cinemasListPerCity = ArrayList<Data>()
            for (eachloc in eachCity.data) {
                if (eachloc.full_address.contains(search, ignoreCase = true) && eachloc.miles < 50) {
                    cinemasListPerCity.add(eachloc)
                }
            }
            cinemasList.put(eachCity.cityname, cinemasListPerCity)
        }
        return cinemasList
    }


    private fun prepareCinemasList(theatres: Theatres, radius: Int, search: String):
            HashMap<String, MutableList<Data>> {
        val cinemasList = java.util.HashMap<String, MutableList<Data>>()
        for (eachCity in theatres.cities) {
            val cinemasListPerCity = ArrayList<Data>()
            for (eachloc in eachCity.data) {
                if (eachloc.full_address.contains(search, ignoreCase = true) &&
                        eachloc.miles < radius) {
                    cinemasListPerCity.add(eachloc)
                }
            }
            cinemasList.put(eachCity.cityname, cinemasListPerCity)
        }
        return cinemasList
    }


    private fun prepareListOfCities(theatres: Theatres): MutableList<String> {
        val cityList = java.util.ArrayList<String>()
        for (eachCity in theatres.cities) {
            cityList.add(eachCity.cityname)
        }
        return cityList
    }

    private fun prepareListOfCities(theatres: Theatres, searchMiles: Int): MutableList<String> {
        val cityList = java.util.ArrayList<String>()
        for (eachCity in theatres.cities) {
            var isNeeded = false
            for (eachLocation in eachCity.data) {
                if (eachLocation.miles < searchMiles) {
                    isNeeded = true
                }
            }
            if (isNeeded) cityList.add(eachCity.cityname)
        }
        return cityList
    }

    private fun prepareListOfCities(theatres: Theatres, search: String): MutableList<String> {
        val cityList = java.util.ArrayList<String>()
        for (eachCity in theatres.cities) {
            for (eachLoc in eachCity.data) {
                if (eachLoc.full_address.contains(search, ignoreCase = true)) {
                    cityList.add(eachCity.cityname)
                }
            }
        }
        return cityList
    }

    private fun prepareListOfCities(theatres: Theatres, radius: Int, search: String): MutableList<String> {
        val cityList = java.util.ArrayList<String>()
        for (eachCity in theatres.cities) {
            for (eachLoc in eachCity.data) {
                if (eachLoc.full_address.contains(search, ignoreCase = true) &&
                        eachLoc.miles < radius) {
                    if (eachCity.cityname in cityList) {
                        //do nothing city already present
                    } else {
                        cityList.add(eachCity.cityname)
                    }
                }
            }
        }
        return cityList
    }


    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    //this will be called when cinema is been selected
    var expandableListlistener: ExpandableListAdapter.ExpandableListListener = object : ExpandableListAdapter.ExpandableListListener {
        override fun onlyAddToHorizontalRv(cinemaname: String) {
            addItemToHorizontalrv(cinemaname)
        }

        override fun select(v: View?, name: String, prefer: Boolean, code: String) {
            if (prefer == true) {
                /**
                 * here we will set data in rvSelectedCinema
                 */

                addItemToHorizontalrv(name)
            } else {
                /**
                 * here we will set data in rvSelectedCinema
                 */
                removeItemfromHorizontalrv(name)
            }
        }

        override fun unselect() {

        }
    }

    /**
     * this will be called when item is clicked in top horizontal recyclerview
     */
    private var selectedCinemaListener = object : SelectedCinemaAdapter.SelectedCinemaListener {

        override fun select(v: View, isSelect: Boolean, preferedCinema: String) {
            removeItemfromHorizontalrv(preferedCinema)
            val cityList: MutableList<String> = prepareListOfCities(prefData!!.DATA.theatres,
                    currentSearchRadius)
            val cinemasList: HashMap<String, MutableList<Data>> =
                    prepareCinemasList(prefData!!.DATA.theatres, currentSearchRadius)


            val posOfPrefItems: ArrayList<Int> = ArrayList()
            cityList.forEachIndexed { grpIndex, s ->
                cinemasList[s]!!.forEachIndexed { index, data ->
                    if (data.code in AppConstants.cinemaList) {
                        data.isPrefered = true
                        posOfPrefItems.add(grpIndex)
                    } else {
                        data.isPrefered = false
                    }
                }
            }

            for (eachcity in cityList) {
                cinemasList.forEach { t, u ->
                    if (eachcity.equals(t)) {
                        for (eachLocation in u) {
                            if (eachLocation.name.equals(preferedCinema)) {
                                eachLocation.isPrefered = false
                                if (eachLocation.code in AppConstants.cinemaList) AppConstants.cinemaList.remove(eachLocation.code)
                                if (AppConstants.cinemaList.size == 0) {
                                    uiHelper.disableSaveButton()
                                } else {
                                    uiHelper.enableSaveButton()
                                }
                            }
                            // make here prefered or unprefered as per requirement
                        }
                    }
                }
            }

            lateinit var loctobeRemoved: CinemaDetail
            for (eachPrefLocation in AppConstants.localCinemadata) {
                if (eachPrefLocation.cinemaName.equals(preferedCinema)) {
                    loctobeRemoved = eachPrefLocation
                }
            }
            try {
                AppConstants.localCinemadata.remove(loctobeRemoved)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            if (AppConstants.localCinemadata.size == 0) {
                uiHelper.disableSaveButton()
            } else {
                uiHelper.enableSaveButton()
            }


            /**
             * here we will set data to populate cinemas/city in lvExp
             */
            expandableListDataAdapter = ExpandableListAdapter(mcontext, cityList, cinemasList,
                    uiHelper, expandableListlistener)
            // setting list adapter

            lvExp!!.setAdapter(expandableListDataAdapter)
            if (expandableListDataAdapter.groupCount != 0) {
                tvNoLocationFound.visibility = View.GONE
            } else {
                tvNoLocationFound.visibility = View.VISIBLE
            }
            for (position in posOfPrefItems) {
                lvExp.expandGroup(position)
            }
            // new adapter variable for rv
            // set adapter to rv
        }

        override fun unselect() {

        }
    }

    fun removeItemfromHorizontalrv(cinemaname: String) {
        selectedCinema.remove(cinemaname)
        if (selectedCinema.size < 1) {
            rvSelectedCinema.visibility = View.GONE
        }
        horizontalCinemanameAdapter.notifyDataSetChanged()
    }

    fun addItemToHorizontalrv(cinemaname: String) {
        if (cinemaname in selectedCinema) {
            //do nothing
        } else {
            selectedCinema.add(cinemaname)
            horizontalCinemanameAdapter.notifyDataSetChanged()
        }
        rvSelectedCinema.visibility = View.VISIBLE
        uiHelper.enableSaveButton()
    }

    private fun startLocationProvider() {
        try {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        if (it != null) {
                            getStateAndOtherDetailsFromLocation(it)
                        } else {
                            if (locationManager != null) {
                                locationManager!!.removeUpdates(locationListener)
                            }
                            uiHelper.gotoManualFlow()
                        }
                    }

        } catch (e: SecurityException) {
            if (locationManager != null) {
                locationManager!!.removeUpdates(locationListener)
            }
            uiHelper.gotoManualFlow()
            LogUtils.d("fusedactivity", "security error fetch locatio - ${e.message}")
        }
    }

    private fun getStateAndOtherDetailsFromLocation(location: Location) {
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            //addresses = geocoder.getFromLocation(42.032, -88.338, 1)
            stateCode = this!!.getUSStateCode(addresses.get(0))!!
            AppConstants.putString(AppConstants.KEY_STATE_CODE, stateCode, mcontext)
            AppConstants.putString(AppConstants.KEY_LATITUDE, location.latitude.toString(), mcontext)
            AppConstants.putString(AppConstants.KEY_LONGITUDE, location.longitude.toString(), mcontext)
            AppConstants.state = stateCode
            AppConstants.latitute = location.latitude.toString()
            AppConstants.longitude = location.longitude.toString()
            fetchDataForCurrentLatlongs()
        } catch (e: Exception) {
            mcontext.alert(getString(R.string.not_valid_location)) {
                positiveButton("OK") {
                    it.dismiss()
                    uiHelper.gotoManualFlow()
                }
            }.show().setCancelable(false)
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
            LogUtils.d("LocationActivity", "location activity crash - ${e.message}")
            return null
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

    override fun onStop() {
        super.onStop()
        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
    }


}
