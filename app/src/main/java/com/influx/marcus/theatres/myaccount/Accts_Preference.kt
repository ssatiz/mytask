package com.influx.marcus.theatres.myaccount


import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.Preference
import com.influx.marcus.theatres.preferences.PreferenceActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_accts_preference.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.apmem.tools.layouts.FlowLayout
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.toast
import java.util.*
import java.util.regex.Pattern

class Accts_Preference : Fragment() {


    private lateinit var mcontext: Context
    private var font: Typeface? = null
    private lateinit var flLocation: FlowLayout
    private lateinit var flGenre: FlowLayout
    private lateinit var flLanguage: FlowLayout
    lateinit var tvGenreLabel: TextView
    lateinit var tvLanguageLabel: TextView
    private lateinit var tvEditPref: TextView
    val tvSize = 12
    private lateinit var tvLayoutParams: LinearLayout.LayoutParams
    private var isFindNowButton = false
    private val KEY_GPSFLOW = "GPSFLOW"
    private val KEY_EDITMODE = "EDITMODE"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var stateCode = ""

    companion object {
        lateinit var preferencesData: Preference
        fun newInstance(myPrefData: Preference) = Accts_Preference().apply {
            preferencesData = myPrefData
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_accts_preference, container, false)
        mcontext = activity as Context
        // set api data in local storage
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mcontext)
        tvEditPref = rootView.findViewById(R.id.tvEditPreferences)
        flLocation = rootView.findViewById(R.id.flLocation)
        flGenre = rootView.findViewById(R.id.flGenre)
        flLanguage = rootView.findViewById(R.id.flLanguage)
        tvLanguageLabel = rootView.findViewById(R.id.tvLanguageLabel)
        tvGenreLabel = rootView.findViewById(R.id.tvGenreLabel)
        font = ResourcesCompat.getFont(mcontext, R.font.gotham_book)
        tvLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tvEditPref.setOnClickListener {
            navigateWithCredentials()
        }
        val langsList = ArrayList<String>()
        preferencesData.languages.forEachIndexed { index, eachItem ->
            langsList.add(eachItem)
        }
        if (langsList != null) {
            if (langsList.size > 0) {
                tvLanguageLabel.visibility = View.VISIBLE
                populateDataIntoLayout(flLanguage, langsList)
            } else {
                tvLanguageLabel.visibility = View.GONE

            }
        }

        val genreList = ArrayList<String>()
        preferencesData.genres.forEachIndexed { index, eachItem ->
            genreList.add(eachItem)
        }
        if (genreList != null) {
            if (genreList.size > 0) {
                tvGenreLabel.visibility = View.VISIBLE
                populateDataIntoLayout(flGenre, genreList)
            } else {
                tvGenreLabel.visibility = View.GONE

            }
        }

        val cinemasList = ArrayList<String>()
        preferencesData.cinemas.forEachIndexed { index, eachItem ->
            cinemasList.add(eachItem)
        }

        if (cinemasList != null) {
            if (cinemasList.size > 0) {
                populateDataIntoLayout(flLocation, cinemasList)
            } else {
                tvLocationLabel.visibility = View.GONE

            }
        }

        return rootView
    }

    private fun populateDataIntoLayout(layout: FlowLayout?, locationList: ArrayList<String>) {

        locationList.forEachIndexed { index, eachItem ->

            val parent = LinearLayout(activity)
            val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
            parent.layoutParams = parLP
            parLP.setMargins(12, 12, 12, 12)
            parLP.gravity = Gravity.CENTER_VERTICAL
            parent.orientation = LinearLayout.HORIZONTAL
            parent.setPadding(20, 30, 20, 30)
            parent.setBackgroundResource(R.drawable.black_prefbackground)


            val tvGenre = buildLabel(eachItem)
            tvGenre.setTextColor(Color.WHITE)
            tvGenre.setTypeface(font)
            tvGenre.setTextSize(tvSize.toFloat())
            tvGenre.setSingleLine(true)
            tvGenre.setId(index + 1)

            val ivFav = ImageView(activity)
            val ivLP = LinearLayout.LayoutParams(50, 50)
            ivFav.layoutParams = ivLP
            ivLP.setMargins(16, 0, 0, 0)
            ivLP.gravity = Gravity.CENTER
            ivFav.setImageResource(R.drawable.redfilledheart)
            parent.addView(tvGenre)
            parent.addView(ivFav)
            layout!!.addView(parent)
        }
    }


    private fun buildLabel(text: String): TextView {
        val textView = TextView(mcontext)
        textView.layoutParams = tvLayoutParams
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        textView.text = text.toUpperCase()
        textView.setTextColor(Color.WHITE)
        textView.setSingleLine(true)
        return textView
    }


    fun navigateWithCredentials() {

        val longitude = AppConstants.getString(AppConstants.KEY_LONGITUDE, mcontext)
        AppConstants.putString(AppConstants.KEY_FLOWFROMMODIFY, "modify", mcontext)
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
        val prefIntent = Intent(mcontext, PreferenceActivity::class.java)
        prefIntent.putExtra("GPSFLOW", false)
        prefIntent.putExtra(KEY_EDITMODE, true)
        startActivity(prefIntent)
        getActivity()!!.finish();
        activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

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
        getActivity()!!.finish();
        activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)


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

}
