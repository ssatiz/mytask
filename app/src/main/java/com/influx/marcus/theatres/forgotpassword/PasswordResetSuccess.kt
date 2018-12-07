package com.influx.marcus.theatres.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.login.LoginScreen
import kotlinx.android.synthetic.main.activity_password_reset_success.*

class PasswordResetSuccess : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset_success)

        ivClose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intentLogin = Intent(this@PasswordResetSuccess, LoginScreen::class.java)
                startActivity(intentLogin)
                finish()
                overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentLogin = Intent(this@PasswordResetSuccess, LoginScreen::class.java)
        startActivity(intentLogin)
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}
