package com.influx.marcus.theatres.homepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.signup.MagicalSignup
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_movierewards.*


class MovieRewardsActivity : AppCompatActivity() {
    lateinit var uiHelper: homeuihelper


    private val context: Context = this@MovieRewardsActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movierewards)
        initViews()
    }


    fun initViews() {
        if (AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, this).isNotBlank() ) {
            // do nothing view is already hidden
        } else {
            btJoinNow.visibility = View.VISIBLE
        }
        val url = AppConstants.getString(AppConstants.KEY_WEBVIEW_BASE_URL,
                this@MovieRewardsActivity) + "movie-rewards"
        UtilsDialog.showProgressDialog(context, "")
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {

                // Return the app name after finish loading
                if (progress == 100) {
                    UtilsDialog.hideProgress()
                }
            }
        }
        webview.webViewClient = MyWebViewClient()

        webview.settings.javaScriptEnabled = true
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webview.settings.loadsImagesAutomatically = true
        webview.loadUrl(url)
        ivBackToolbar.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)

        }
        btJoinNow.setOnClickListener {
            val i = Intent(this@MovieRewardsActivity, MagicalSignup::class.java)
            i.putExtra("sidemenu", "true")
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
    }


    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            UtilsDialog.showProgressDialog(this@MovieRewardsActivity, "")
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            UtilsDialog.hideProgress()
            Log.d("URL", "onPageFinished: : $url")
        }
    }
}
