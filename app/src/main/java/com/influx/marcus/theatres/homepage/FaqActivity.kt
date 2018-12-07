package com.influx.marcus.theatres.homepage

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_special_web.*

class FaqActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        initViews()


    }

    fun initViews() {
        tvTitle.setText("FAQs")
        val urlToLoad = AppConstants.getString(AppConstants.KEY_WEBVIEW_BASE_URL,
                this)+ AppConstants.faq
        webview.setWebViewClient(MyWebViewClient())
        webview.getSettings().setJavaScriptEnabled(true)
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.loadUrl(urlToLoad)
        ivBackToolbar.setOnClickListener {
           onBackPressed()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            UtilsDialog.showProgressDialog(this@FaqActivity, "")
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            UtilsDialog.hideProgress()
            Log.d("URL", "onPageFinished: : $url")
        }
    }
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}