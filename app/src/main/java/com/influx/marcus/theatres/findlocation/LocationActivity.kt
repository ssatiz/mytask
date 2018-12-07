package com.influx.marcus.theatres.findlocation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.statelist.StateListResp
import com.influx.marcus.theatres.preferences.PreferenceActivity
import com.influx.marcus.theatres.preferences.PreferenceVM
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*
import java.util.regex.Pattern


class LocationActivity : AppCompatActivity() {

    private lateinit var prefVM: PreferenceVM
    var stateCode = ""
    private lateinit var context: Context
    var locationManager: LocationManager? = null
    private var isFindNowButton = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        context = this@LocationActivity
        initViews()
        registerObservers()
    }

    private fun registerObservers() {
        prefVM = ViewModelProviders.of(this).get(PreferenceVM::class.java)
        prefVM.getAllStateData().observe(this, stateListObs)
        prefVM.getApiErrorData().observe(this, apiErrorObs)
    }

    private fun initViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        btnFindnow.setOnClickListener {

            launch(UI) {
                try {
                    val result = askPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    isFindNowButton = true
                    if (UtilsDialog.isNetworkStatusAvialable(this@LocationActivity)) {
                        UtilsDialog.showProgressDialog(this@LocationActivity, "")
                        prefVM.fetchAllStates(AppConstants.AUTHORISATION_HEADER)
                    }
                } catch (e: PermissionException) {
                    e.denied.forEach {
                        isFindNowButton = false
                        if (UtilsDialog.isNetworkStatusAvialable(this@LocationActivity)) {
                            UtilsDialog.showProgressDialog(this@LocationActivity, "")
                            prefVM.fetchAllStates(AppConstants.AUTHORISATION_HEADER)
                        }
                    }
                    e.accepted.forEach {

                    }
                }
            }
        }

        tvGoback.setOnClickListener {
            isFindNowButton = false
            if (UtilsDialog.isNetworkStatusAvialable(this@LocationActivity)) {
                UtilsDialog.showProgressDialog(this@LocationActivity, "")
                prefVM.fetchAllStates(AppConstants.AUTHORISATION_HEADER)
            }
        }
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

            LogUtils.d("Findnow", " Error - $t!!")
        }
    }

    /**
     * response for general locations statelist
     */
    private var stateListObs = object : Observer<StateListResp> {
        override fun onChanged(t: StateListResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                AppConstants.putObject(AppConstants.KEY_STATELIST, t!!, context)
                if (isFindNowButton) {
                    startLocationProvider()
                } else {
                    navigateToPreferenceNonGps()
                }
            } else {
                alert( getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    /**
     * this will redirect user to preferences for non gps flow
     */
    private fun navigateToPreferenceNonGps() {
        val prefIntent = Intent(context, PreferenceActivity::class.java)
        prefIntent.putExtra("GPSFLOW", false)
        startActivity(prefIntent)
        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
        finish()
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
            LogUtils.d("fusedactivity", "security error fetch locatio - ${e.message}")
        }
    }

    private fun navigateToPreference(latitude: String, longitude: String, stateCode: String) {
        var prefIntent = Intent(context, PreferenceActivity::class.java)
        AppConstants.putString(AppConstants.KEY_STATE_CODE, stateCode, context)
        AppConstants.putString(AppConstants.KEY_LATITUDE, latitude, context)
        AppConstants.putString(AppConstants.KEY_LONGITUDE, longitude, context)
        prefIntent.putExtra("GPSFLOW", true)
        startActivity(prefIntent)
        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
        finish()
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
            LogUtils.d("FINDNOW", "Now usa gps location cant get state details")
            return null
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
    }


    private fun getStateAndOtherDetailsFromLocation(location: Location) {
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            stateCode = this!!.getUSStateCode(addresses.get(0))!!
            navigateToPreference(location.latitude.toString(),
                    location.longitude.toString(),
                    stateCode)
           /* addresses = geocoder.getFromLocation(42.032, -88.338, 1) //elgin lab cordinates
            stateCode = this!!.getUSStateCode(addresses.get(0))!!
            navigateToPreference("42.032",
                    "-88.338",
                    stateCode)*/
        } catch (e: Exception) {
            alert(getString(R.string.not_valid_location)) {
                positiveButton("OK") {
                    it.dismiss()
                    navigateToPreferenceNonGps()
                }
            }.show().setCancelable(false)
            LogUtils.d("LocationActivity", "find location exception ${e.message}")
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
