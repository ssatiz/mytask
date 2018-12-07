package com.influx.marcus.theatres.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionReq
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionResp
import com.influx.marcus.theatres.findlocation.LocationActivity
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maintanance.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class MaintananceActivity : AppCompatActivity() {
    var maintananceUrl = ""
    var context = this@MaintananceActivity
    lateinit var splashVM: SplashVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintanance)
        registerObserver()
        tvTryAgain.setOnClickListener {
            checkmethod()
        }
        try {
            if (intent.extras.get("maintananceurl") != null) {
                maintananceUrl = intent.extras.getString("maintananceurl")
                Picasso.with(context).load(maintananceUrl).into(ivMaintananceLogo, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        ivMaintananceLogo.visibility = View.VISIBLE
                    }

                    override fun onError() {
                    }
                })
            }
        } catch (e: Exception) {
            Log.i("maintanance Page Error", e.toString())
        }
    }
    fun checkmethod() {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
          UtilsDialog.showProgressDialog(context,"")
            val deviceName = AppConstants.getString(AppConstants.KEY_DEVICENAME, context)
            val userId = AppConstants.getString(AppConstants.KEY_USERID, context)
            var versionReq = VersionReq(
                    AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM,
                    deviceName,
                    userId)
            splashVM.fetchVersionData(AppConstants.AUTHORISATION_HEADER, versionReq)
        } else {
        }
    }
    private fun registerObserver() {
        splashVM = ViewModelProviders.of(this).get(SplashVM::class.java)
        splashVM.getErrorDetails().observe(this, errorObs)
        splashVM.getVersionDetails().observe(this, versionDetailObs)
    }

    var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            LogUtils.d("Splash", "error is $t")
            alert( getString(R.string.ohinternalservererror) ,
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }

    var versionDetailObs = object : Observer<VersionResp> {
        override fun onChanged(t: VersionResp?) {
            AppConstants.putString(AppConstants.KEY_WEBVIEW_BASE_URL, t!!.DATA.web_view_url, context)
            AppConstants.putString(AppConstants.KEY_API_BASEURL, t!!.DATA.live_webservice_url, context)
            updateUi(t)
        }

    }

    private fun updateUi(resp: VersionResp?) {
        if (resp!!.STATUS) {//true
            if (resp.DATA.maintenance == "0") {
                when (resp.DATA.force) {
                    "0" -> {
                        //do nothing current version is latest
                        if (AppConstants.getString(AppConstants.KEY_STATE_CODE, this).isNotBlank() &&
                                AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, this).isNotEmpty()) {
                            val mainIntent = Intent(this, HomeActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                        } else {
                            val mainIntent = Intent(this, LocationActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                        }
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
                                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.influx.marcus.theatres")
                                    setPackage("com.influx.marcus.theatres")
                                }
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            negativeButton("Later") { dialog ->
                                dialog.dismiss()
                                if (AppConstants.getString(AppConstants.KEY_STATE_CODE, context).isNotBlank() &&
                                        AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context).isNotEmpty()) {
                                    val mainIntent = Intent(context, HomeActivity::class.java)
                                    startActivity(mainIntent)
                                    finish()
                                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                                } else {
                                    val mainIntent = Intent(context, LocationActivity::class.java)
                                    startActivity(mainIntent)
                                    finish()
                                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                                }
                            }

                        }.show().setCancelable(false)

                    }
                }
            } else {
                val i = Intent(context, MaintananceActivity::class.java)
                if(resp.DATA.maintenance_image_url.isNotEmpty())
                    i.putExtra("maintananceurl",resp.DATA.maintenance_image_url)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            }
        } else {

        }
    }
}
