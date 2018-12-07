package com.influx.marcus.theatres.cinegreetings

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import com.influx.marcus.theatres.R
import android.view.WindowManager
import android.webkit.WebChromeClient
import com.influx.marcus.theatres.commonview.UtilDialog


/**
 * Created by Kavitha on 11-04-2018.
 */
class CineGreetLandscape : AppCompatActivity() {
    lateinit var webview: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cine_landscape)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        try {
            webview = findViewById(R.id.wvRender)
            var webSettings = webview.getSettings()
            webSettings.setJavaScriptEnabled(true)
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webview.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        UtilDialog.hideProgress()
                    }
                }
            }
            webview.loadUrl(intent.getStringExtra("url"))
            webview.restoreState(intent.getParcelableExtra("state"))


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var orientation = newConfig?.orientation

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
            // TODO: add logic for landscape mode here
            onBackPressed()

        }


    }
}