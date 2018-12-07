package com.influx.marcus.theatres.preferences


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
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
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsReq
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.Theatres
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.subval.Data
import com.influx.marcus.theatres.api.ApiModels.statelist.StateListResp
import com.influx.marcus.theatres.api.pref.CinemaDetail
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.OnItemClickListener
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.fragment_location_nong.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast


/**
 * A simple [Fragment] subclass.
 *
 */
class LocationNONGPSFragment : Fragment() {

    companion object {
        private var isEditMode = false
        private lateinit var uiHelper: ActivityUiHelper
        fun newInstance(editMode: Boolean, uiListener: ActivityUiHelper) =
                LocationNONGPSFragment().apply {
                    isEditMode = editMode
                    uiHelper = uiListener
                }
    }

    private lateinit var mcontext: Context
    private lateinit var prefVM: PreferenceVM
    private lateinit var spnLocation: Spinner
    private lateinit var etCityname: EditText
    private lateinit var prefData: PreferedLocsResp
    private lateinit var expandableListDataAdapter: ExpandableListAdapter
    private lateinit var lvExp: ExpandableListView
    private lateinit var rvSelectedCinema: RecyclerView
    private lateinit var rvStatelist: RecyclerView
    private lateinit var stateAdapter: StateAdapter
    private var stateListResp: StateListResp? = null
    private lateinit var tvChooseLoc: TextView
    var selectedCinema = ArrayList<String>()
    lateinit var totalCityList: MutableList<String>
    lateinit var cityList: MutableList<String>
    lateinit var totalCinemaList: HashMap<String, MutableList<Data>>
    lateinit var cinemasList: HashMap<String, MutableList<Data>>
    private var isStateListVisible = false
    private lateinit var llAutodetectLocation: LinearLayout
    private lateinit var horizontalCinemanameAdapter: SelectedCinemaAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_location_nong, container,
                false)
        mcontext = this!!.activity!!
        getActivity()!!.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initViews(rootView)
        registerObservers()
        setupStatelistAndPreferedData()
        if (isEditMode || AppConstants.cinemaList.size > 0) {
            fetchDataForState(AppConstants.state)
            tvChooseLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_loc_nongps,
                    0, R.drawable.drawable_down_nongps, 0);
            rvStatelist.visibility = View.GONE
        } else {
            tvChooseLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_loc_nongps,
                    0, R.drawable.drawable_up_nongps, 0);
            rvStatelist.visibility = View.VISIBLE
            etCityname.visibility = View.GONE
        }
        return rootView
    }

    private fun setupStatelistAndPreferedData() {
        //UtilsDialog.showProgressDialog(mcontext, "")
        stateListResp = AppConstants.getObject(AppConstants.KEY_STATELIST,
                mcontext, StateListResp::class.java as Class<Any>) as StateListResp?
        stateAdapter = StateAdapter(stateListResp!!, mcontext, rvStateClick)
        rvStatelist.adapter = stateAdapter
    }


    private fun fetchDataForState(selectedStateCode: String) {
        if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
            UtilsDialog.showProgressDialog(mcontext, "")
            val req: PreferedLocsReq = PreferedLocsReq(
                    AppConstants.AUTHORISATION_HEADER,
                    selectedStateCode,
                    "",
                    ""
            )
            prefVM.fetchPreferedLocationData(req)
            AppConstants.state = selectedStateCode
            AppConstants.latitute = ""
            AppConstants.longitude = ""
            setupStatelistAndPreferedData()
        }
    }


    private fun registerObservers() {
        prefVM = ViewModelProviders.of(this).get(PreferenceVM::class.java)
        prefVM.getPreferedLocationData().observe(this, preferedRespObs)
        prefVM.getApiErrorData().observe(this, apiErrorObs)
    }

    private fun initViews(rootView: View) {
        tvChooseLoc = rootView.findViewById(R.id.etSearchLocations)
        llAutodetectLocation = rootView.findViewById(R.id.llAutodetectLocation)
        llAutodetectLocation.setOnClickListener {
            uiHelper.gotoAutoDetectFlow()
        }
        tvChooseLoc.setOnClickListener {
            if (isStateListVisible) {
                isStateListVisible = false
                tvChooseLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_loc_nongps,
                        0, R.drawable.drawable_down_nongps, 0);
                rvStatelist.visibility = View.GONE
                etCityname.visibility = View.VISIBLE
            } else {
                isStateListVisible = true
                tvChooseLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_loc_nongps,
                        0, R.drawable.drawable_up_nongps, 0);
                rvStatelist.visibility = View.VISIBLE
                tvNoLocationFound.visibility=View.GONE
                etCityname.setText("")
                etCityname.visibility = View.GONE
            }
        }
        etCityname = rootView.findViewById(R.id.etCityname)
        etCityname.visibility = View.GONE
        lvExp = rootView.findViewById(R.id.lvExp)
        rvSelectedCinema = rootView.findViewById(R.id.rvSelectedCinema)
        rvSelectedCinema.setHasFixedSize(true)
        rvSelectedCinema.setLayoutManager(LinearLayoutManager(mcontext,
                LinearLayoutManager.HORIZONTAL, false))
        horizontalCinemanameAdapter = SelectedCinemaAdapter(selectedCinema, mcontext, selectedCinemaListener)
        rvSelectedCinema.adapter = horizontalCinemanameAdapter
        rvStatelist = rootView.findViewById(R.id.rvStatelist)
        rvStatelist.setHasFixedSize(true)
        rvStatelist.setLayoutManager(LinearLayoutManager(mcontext,
                LinearLayoutManager.VERTICAL, false))
        etCityname.setImeOptions(EditorInfo.IME_ACTION_DONE)
        etCityname.setSingleLine()
        etCityname.setPressed(true)
        etCityname.setOnTouchListener(View.OnTouchListener { v, event ->
            etCityname.setFocusableInTouchMode(true)

            false
        })

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

    private var apiErrorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert( getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }

    private var preferedRespObs = object : Observer<PreferedLocsResp> {
        override fun onChanged(t: PreferedLocsResp?) {
            (activity as PreferenceActivity).preferedLocsResp = t
            if (t!!.STATUS) {
                updateUiForLocations(t)
            } else {
                UtilsDialog.hideProgress()
                alert(t!!.DATA.message, getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
               // t.DATA.message
            }
        }
    }

    /**
     *this is onclick listener for recyclerview
     */
    private var rvStateClick = object : OnItemClickListener {
        override fun onItemClick(position: Int?) {
            tvChooseLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_loc_nongps,
                    0, R.drawable.drawable_down_nongps, 0);
            rvStatelist.visibility = View.GONE
            isStateListVisible = false
            val selectedStateData = stateListResp!!.DATA.get(position!!)
            etSearchLocations.text = selectedStateData.state
            fetchDataForState(selectedStateData.scode)
        }
    }


    private fun updateUiForLocations(prefLocations: PreferedLocsResp?) {
        val selectedStateData = stateListResp!!.DATA
        val stateCode = AppConstants.state
        for (eachState in selectedStateData) {
            if (eachState.scode.equals(stateCode)) {
                tvChooseLoc.setText(eachState.state)
            }
        }
        prefData = prefLocations!!
        etCityname.visibility = View.VISIBLE
        cityList = prepareListOfCities(prefLocations!!.DATA.theatres)
        totalCityList = cityList
        cinemasList = prepareCinemasList(prefLocations!!.DATA.theatres)
        totalCinemaList = cinemasList
        etCityname.setText("")
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

        lvExp.visibility = View.VISIBLE
        etCityname.visibility = View.VISIBLE

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
        UtilsDialog.hideProgress()
/*        alert("We are unable to find any theaters around your location.Please set your location manually",
                "Marcus Theatres") {
            positiveButton("OK") {
                it.dismiss()

            }
        }.show().setCancelable(false)*/
        lvExp.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id ->

            false
        })

        lvExp.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            false
        })
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


    private fun prepareCinemasList(theatres: Theatres, search: String): HashMap<String, MutableList<Data>> {
        val cinemasList = HashMap<String, MutableList<Data>>()
        for (eachCity in theatres.cities) {
            val cinemasListPerCity = ArrayList<Data>()
            for (eachloc in eachCity.data) {
                if (eachloc.full_address.contains(search, ignoreCase = true)) {
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

    private fun prepareListOfCities(theatres: Theatres, search: String): MutableList<String> {

        val cityList = java.util.ArrayList<String>()
        for (eachCity in theatres.cities) {
            for (eachLoc in eachCity.data) {
                if (eachLoc.full_address.contains(search, ignoreCase = true)) {
                    if (eachCity.cityname in cityList) {

                    } else {
                        cityList.add(eachCity.cityname)
                    }
                }
            }
        }
        return cityList
    }

    private fun filterByText(search: String) {
        try {

            val cityList: MutableList<String> = prepareListOfCities(prefData!!.DATA.theatres, search)
            val cinemasList: HashMap<String, MutableList<Data>> =
                    prepareCinemasList(prefData!!.DATA.theatres, search)

            val posOfPrefItems: java.util.ArrayList<Int> = java.util.ArrayList()
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
            //val cinemasList: HashMap<String, MutableList<Data>> = prepareCinemasList(prefData!!.DATA.theatres, search)
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


    //this will be called when cinema is been selected
    var expandableListlistener: ExpandableListAdapter.ExpandableListListener = object : ExpandableListAdapter.ExpandableListListener {
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


        override fun onlyAddToHorizontalRv(cinemaname: String) {

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

            val posOfPrefItems: ArrayList<Int> = ArrayList()
            cityList.forEachIndexed { grpIndex, s ->
                cinemasList[s]!!.forEachIndexed { index, data ->
                    if (data.code in AppConstants.cinemaList) {
                        posOfPrefItems.add(grpIndex)
                    } else {// do nothing

                    }
                }
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
        horizontalCinemanameAdapter.notifyDataSetChanged()
    }

    fun addItemToHorizontalrv(cinemaname: String) {
        if (cinemaname in selectedCinema) {
            //do nothing
        } else {
            selectedCinema.add(cinemaname)
            horizontalCinemanameAdapter.notifyDataSetChanged()
        }
        uiHelper.enableSaveButton()
    }
}
