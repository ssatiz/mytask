package com.influx.marcus.theatres.splash


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.cunoraz.gifview.library.GifView
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardReq
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.versioncheck.DATA
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionReq
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionResp
import com.influx.marcus.theatres.findlocation.LocationActivity
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CommonApi
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast

class SplashScreen : AppCompatActivity() {

    lateinit var context: Context
    lateinit var loader: AVLoadingIndicatorView
    lateinit var splashVM: SplashVM
    lateinit var tvCopy: TextView
    private lateinit var countDownTime: CountDownTimer
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var gib: GifView
    private lateinit var homeRequest: HomeRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        context = this@SplashScreen
        Fabric.with(context, Crashlytics())
        registerObserver()
        AppConstants.isSplash = true
        initViews()
        gib.play()
        Handler().postDelayed(Runnable {
            checkmethod()
        }, 1000)

        Handler().postDelayed({
            gib.pause()
        }, 2000)

    }

    private fun fetchHomeData() {
        if (AppConstants.getString(AppConstants.KEY_STATE_CODE, this).isNotBlank() &&
                AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, this).isNotEmpty()) {
            CommonApi.fetchHomeDataAndStoreLocally(context, homeRequest)
        }//else do nothing
    }

    private fun initViews() {
        loader = findViewById(R.id.loader)
        tvCopy = findViewById(R.id.tvCopy)
        gib = findViewById(R.id.ivMarcusLogo)
        if (!AppConstants.getString(AppConstants.KEY_GUEST_USER, context).equals("")) {
            AppConstants.putString(AppConstants.KEY_GUEST_USER, "", context)
            AppConstants.putString(AppConstants.KEY_USERID, "", context)
            AppConstants.putString(AppConstants.KEY_EMAIL, "", context)
        }

    }

    private fun registerObserver() {
        splashVM = ViewModelProviders.of(this).get(SplashVM::class.java)
        splashVM.getErrorDetails().observe(this, errorObs)
        splashVM.getVersionDetails().observe(this, versionDetailObs)
    }

    var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            gib.visibility = View.GONE
            loader.visibility = View.GONE
            tvCopy.visibility = View.GONE
            LogUtils.d("Splash", "error is $t")
            alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    var versionDetailObs = object : Observer<VersionResp> {
        override fun onChanged(t: VersionResp?) {
            gib.visibility = View.GONE
            AppConstants.putString(AppConstants.KEY_WEBVIEW_BASE_URL, t!!.DATA.web_view_url, context)
            AppConstants.putString(AppConstants.KEY_API_BASEURL, t!!.DATA.live_webservice_url, context)

            updateUi(t)
        }

    }

    private fun updateUi(resp: VersionResp?) {
        gib.visibility = View.GONE
        if (resp!!.STATUS) {//true
            tvCopy.visibility = View.GONE
            if (resp.DATA.maintenance == "0") {
                when (resp.DATA.force) {
                    "0" -> {
                        //do nothing current version is latest
                        dynamicSplash(resp.DATA)
                    }

                    "1" -> {
                        alert("An update for this app is available. Please download to enjoy the newest additions.", "Marcus Theatres")
                        {
                            positiveButton("Update Now") { dialog ->
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(
                                            "https://play.google.com/store/apps/details?id=com.influx.marcus.theatres")
                                    setPackage("com.android.vending")
                                }
                                startActivity(intent)
                                dialog.dismiss()

                            }

                        }.show().setCancelable(false)
                        //do nothing current version is latest
                    }

                    "2" -> {
                        //do nothing current version is latest
                        alert("An update for this app is available. Please download to enjoy the newest additions.", "Marcus Theatres")
                        {
                            positiveButton("Update Now") { dialog ->
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(
                                            "https://play.google.com/store/apps/details?id=com.influx.marcus.theatres")
                                    setPackage("com.influx.marcus.theatres")
                                }
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            negativeButton("Later") { dialog ->
                                dialog.dismiss()
                                dynamicSplash(resp.DATA)
                            }

                        }.show().setCancelable(false)

                    }

                    else -> {
                        alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                            positiveButton("OK") { dialog ->
                                dialog.dismiss()
                            }
                        }.show()
                    }

                }
            } else {
                 AppConstants.isSplash = false
                val i = Intent(context, MaintananceActivity::class.java)
                if(resp.DATA.maintenance_image_url.isNotEmpty())
                i.putExtra("maintananceurl",resp.DATA.maintenance_image_url)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

            }

        } else {// false means maintainance
            try {
                alert(resp!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            btnSkip.visibility = View.VISIBLE
            btnSkip.text = "Retry"
            ivDynaSplash.visibility = View.VISIBLE
            if (resp.DATA.maintenance_image_url.isNotBlank()) {
                Picasso.with(context).load(resp.DATA.maintenance_image_url)

                        .into(ivDynaSplash, object : Callback {
                            override fun onSuccess() {
                                UtilsDialog.hideProgress()
                                tvCopy.visibility = View.GONE
                            }

                            override fun onError() {
                                UtilsDialog.hideProgress()
                                tvCopy.visibility = View.GONE
                            }
                        })
            } else {
                UtilsDialog.hideProgress()
                tvCopy.visibility = View.GONE
            }
            btnSkip.setOnClickListener {
                checkmethod()
            }
        }
    }

    private fun showUpdatePopup(data: DATA) {
        when (data.force) {
            "1" -> {
                alert("Marcus Theatres", "Your app is out dated please update " +
                        "for better experience") {
                    okButton {
                        dynamicSplash(data)
                        it.dismiss()
                    }
                }.show()
            }
            "2" -> {
                alert("Marcus Theatres", "Your app is out dated please update" +
                        " for better experience") {
                    okButton {
                        dynamicSplash(data)
                        it.dismiss()
                    }
                    cancelButton {
                        dynamicSplash(data)
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    fun checkmethod() {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            loader.visibility = View.VISIBLE
            val deviceName = AppConstants.getString(AppConstants.KEY_DEVICENAME, context)
            val userId = AppConstants.getString(AppConstants.KEY_USERID, context)
            var versionReq = VersionReq(
                    AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM,
                    deviceName,
                    userId)
            splashVM.fetchVersionData(AppConstants.AUTHORISATION_HEADER, versionReq)
        } else {
            gib.visibility = View.GONE
        }
    }

    private fun dynamicSplash(data: DATA) {
        gib.visibility = View.GONE
        if (!data.splash.image_url.equals("", ignoreCase = true) && data.splash.image_url != null) {//show dynamic splash
            ivDynaSplash.visibility = View.VISIBLE
            Picasso.with(context).load(data.splash.image_url).into(ivDynaSplash, object : Callback {
                override fun onSuccess() {
                    loader.visibility = View.GONE
                    UtilsDialog.hideProgress()
                    btnSkip.visibility = View.VISIBLE
                    btnSkip.text = "Skip"
                    tvCopy.visibility = View.GONE
                    btnSkip.setOnClickListener {
                        countDownTime.cancel()
                        gotoLocations()
                    }
                    startAndShowTimerToNavigate(data.splash.seconds)
                }

                override fun onError() {
                    UtilsDialog.hideProgress()
                    loader.visibility = View.GONE
                    btnSkip.visibility = View.VISIBLE
                    btnSkip.text = "Skip"
                    tvCopy.visibility = View.GONE
                    btnSkip.setOnClickListener {
                        countDownTime.cancel()
                        gotoLocations()
                    }
                    startAndShowTimerToNavigate(data.splash.seconds)
                }
            })

        } else {//navigate to locations
            loader.visibility = View.GONE
            tvCopy.visibility = View.GONE
            UtilsDialog.hideProgress()
            gotoLocations()
        }
    }

    private fun startAndShowTimerToNavigate(seconds: String) {
        tvTimer.visibility = View.VISIBLE
        val timeOf = seconds.toInt()
        val timeInMil: Long = (timeOf * 1000).toLong()
        countDownTime = object : CountDownTimer(timeInMil, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "" + (millisUntilFinished / 1000)
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                tvTimer.setText("0")
                gotoLocations()
            }
        }.start()
    }

    /**
     * todo verify flow of user who doesnt want to share location and passintent accordingly
     */
    private fun gotoLocations() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
        if (AppConstants.getString(AppConstants.KEY_STATE_CODE, this).isNotBlank() &&
                AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, this).isNotEmpty()) {
            AppConstants.isSplash = false
            val mainIntent = Intent(this, HomeActivity::class.java)
            startActivity(mainIntent)
            finish()
        } else {
            AppConstants.isSplash = false
            val mainIntent = Intent(this, LocationActivity::class.java)
            startActivity(mainIntent)
            finish()
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
}
