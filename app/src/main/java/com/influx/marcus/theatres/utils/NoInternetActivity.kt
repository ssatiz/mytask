package com.influx.marcus.theatres.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.splash.SplashScreen
import kotlinx.android.synthetic.main.activity_no_internet.*

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
        tvTryAgain.setOnClickListener {
            val rAnim = RotateAnimation(0f, 359f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rAnim.duration = 700
            ivRefresh.startAnimation(rAnim)
            isNetworkStatusAvialable(this@NoInternetActivity)
        }
        ivRefresh.setOnClickListener {
            val rAnim = RotateAnimation(0f, 359f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rAnim.duration = 700
            ivRefresh.startAnimation(rAnim)
            isNetworkStatusAvialable(this@NoInternetActivity)
        }
    }

    fun isNetworkStatusAvialable(context: Context): Boolean {
        if (!isNetworkAvailable(context)) {
            return false
        } else {
            ivRefresh.clearAnimation()
            if (AppConstants.isSplash) {
                val intent = Intent(context, SplashScreen::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            } else {
                this.finish()
            }
            return true
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}

