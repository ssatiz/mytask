package com.influx.marcus.theatres.specials

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_special_web.*

class SpecialWebActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_web)
        initViews()


    }

    fun initViews() {
        val url = intent.extras.getString("weburl")
        val title = intent.extras.getString("title")
        val image = intent.extras.getString("image")
        Picasso.with(this).load(image).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE).into(ivBanner)

        tvTitle.setText(title)
        webview.setWebViewClient(MyWebViewClient())
        webview.getSettings().setJavaScriptEnabled(true)
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.loadUrl(url)
        ivBackToolbar.setOnClickListener {
            onBackPressed()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            UtilsDialog.showProgressDialog(this@SpecialWebActivity, "")
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
        val i = Intent(this@SpecialWebActivity, SpecialsActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}
