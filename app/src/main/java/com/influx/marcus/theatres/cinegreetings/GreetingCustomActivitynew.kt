package com.influx.marcus.theatres.cinegreetings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.login.LoginScreen
import java.text.SimpleDateFormat
import java.util.*


class GreetingCustomActivitynew : AppCompatActivity() {

    // time in rfc 3339 format
    val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .format(Date())
    val screenId = "e6c0a4b9-d7d3-4491-8ad1-9f96407be6bf"
    val cheersUser = "INFLUX"
    val cheersToken = "ZuEfeEw83nRAGpY98n5mJZuCqcrcXRhW"
    private lateinit var ivBack: ImageView
    private lateinit var wvRender: WebView
    private lateinit var tvPreview: TextView

    private var tempId = "975aed4f-147a-417e-af33-6eb1f1579934"//you could have been
    private var anniversarryTempId = "6b5da87d-2f52-4633-a2c7-7899d61c7a4b" // redstring
    private var valentineTempId = "6bb1cea8-7dd4-4701-acac-2e18f5914d17" // cactus
    private var to = "recipentName"
    private var message = "Short Message"
    private var whoswishing = "Well wisher"
    private var eventName = ""
    private var eventimage: Int = 0

    private lateinit var etTo: EditText
    private lateinit var etMessage: EditText
    private lateinit var whoswishingEt: EditText
    private lateinit var tvEventLable: TextView
    lateinit var txtPay: TextView
    var templateRenderUrl: String = ""
    lateinit var imageRender: ImageView
    lateinit var portraitlayout: ConstraintLayout
    lateinit var webviewLandscapre: WebView
    var isPortait: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting_custom_p)
        findviewbyidp()
        if (intent != null) {
            if (intent.hasExtra("event_name")) {
                eventName = intent.extras.getString("event_name")
                eventimage = intent.extras.getInt("event_image", 0)
//                imageRender.setImageResource(eventimage)
            }
        }

         window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    fun findviewbyidp() {
        ivBack = findViewById(R.id.ivBackToolbar)
        ivBack.setOnClickListener { onBackPressed() }
        wvRender = findViewById(R.id.wvRender)
        tvPreview = findViewById(R.id.tvPreview)
        tvPreview.setOnClickListener { reloadPreview() }
        etTo = findViewById(R.id.etReceipent)
        etMessage = findViewById(R.id.etMessage)
        tvEventLable = findViewById(R.id.tvEventLabel)
        whoswishingEt = findViewById(R.id.etWisher)
        txtPay = findViewById(R.id.tvPay)
        imageRender = findViewById(R.id.imageRender)
        portraitlayout = findViewById(R.id.portrait_layout)
        webviewLandscapre = findViewById(R.id.lanscape)
        tvEventLable.text = eventName
        wvRender.visibility = View.GONE
        imageRender.visibility = View.VISIBLE
        var webSettings = wvRender.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        wvRender.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    UtilDialog.hideProgress()
                }
            }
        }
        txtPay.setOnClickListener({
            navigateToLogin()
        })
//        wvRender.loadUrl("https://staging.slydes.qubecinema.com/render/slyde?template_id=2f99cda9-e01e-4e55-b192-185e2755dbe1&to=recipentName&message=MessageText")

    }

    fun navigateToLogin() {
        if (etTo.text.toString().isEmpty()) {
            etTo.setError("Cannot be empty")
            return
        } else if (whoswishingEt.text.toString().isEmpty()) {
            whoswishingEt.setError("Cannot be Empty")
            return
        } else if (etMessage.text.toString().isEmpty()) {
            etMessage.setError("Cannot be Empty")
            return
        } else {
            isPortait = true
            to = etTo.text.toString()
            message = etMessage.text.toString()
            whoswishing = whoswishingEt.text.toString()
            seturl()
            wvRender.visibility = View.GONE
            wvRender.destroy()
        }


        var intent = Intent(this@GreetingCustomActivitynew, LoginScreen::class.java)
        startActivity(intent)
        finish()


    }

    fun seturl() {
        when (eventName) {
            "Anniversary" -> {
                templateRenderUrl = "https://staging.slydes.qubecinema.com/render/slyde?template_id=$anniversarryTempId&to=$to&message=$message&from=$whoswishing"
            }
            "Birthday" -> {
                templateRenderUrl = "https://staging.slydes.qubecinema.com/render/slyde?template_id=$tempId&to=$to&message=$message"
            }
            "Valentine's" -> {
                templateRenderUrl = "https://staging.slydes.qubecinema.com/render/slyde?template_id=$valentineTempId&to=$to&message=$message&from=$whoswishing"
            }
        }
    }

    private fun reloadPreview() {
        if (etTo.text.toString().isEmpty()) {
            etTo.setError("Cannot be empty")
            return
        } else if (etMessage.text.toString().isEmpty()) {
            etMessage.setError("Cannot be Empty")
            return
        } else {
            isPortait = true
            to = etTo.text.toString()
            message = etMessage.text.toString()
            whoswishing = whoswishingEt.text.toString()
            seturl()
        }

        UtilDialog.showProgressDialog(this, "")
        wvRender.visibility = View.VISIBLE
        imageRender.visibility = View.GONE
        wvRender.loadUrl(templateRenderUrl)
    }

    override fun onBackPressed() {

        if (webviewLandscapre.visibility == View.VISIBLE) {
            webviewLandscapre.loadUrl("")
            webviewLandscapre.visibility = View.GONE
            portraitlayout.visibility = View.VISIBLE
            wvRender.loadUrl(templateRenderUrl)
        } else if (portraitlayout.visibility == View.VISIBLE) {
            wvRender.loadUrl("")
            finish()
        }

    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        var orientation = display.rotation
        if (isPortait) {
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                // TODO: add logic for landscape mode here
//            val state = Bundle()
//            wvRender.saveState(state)
//
//            val intent = Intent(this@GreetingCustomActivity, CineGreetLandscape::class.java)
//            intent.putExtra("state", state)
//            intent.putExtra("url", templateRenderUrl)
//            startActivity(intent)
                if (wvRender.visibility == View.VISIBLE) {
                    portraitlayout.visibility = View.GONE
                    webviewLandscapre.visibility = View.VISIBLE
                    wvRender.loadUrl("")
                    setLandscapeWebview()
                }
            } else {
                portraitlayout.visibility = View.VISIBLE
                webviewLandscapre.visibility = View.GONE
                webviewLandscapre.loadUrl("")
                wvRender.loadUrl(templateRenderUrl)
            }
        }
    }

    fun setLandscapeWebview() {
        var webSettings = webviewLandscapre.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        UtilDialog.showProgressDialog(this@GreetingCustomActivitynew, "")
        webviewLandscapre.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    UtilDialog.hideProgress()
                }
            }
        }
        webviewLandscapre.loadUrl(templateRenderUrl)
    }
}