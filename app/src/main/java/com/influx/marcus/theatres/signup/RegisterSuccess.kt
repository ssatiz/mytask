package com.influx.marcus.theatres.signup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.myaccount.MyAccountScreen
import com.influx.marcus.theatres.utils.AppConstants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mmregister_success.*
import org.jetbrains.anko.toast

class RegisterSuccess : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mmregister_success)

        if (intent.extras != null) {
            var success_msg = intent.extras.getString("success_msg")
            var success_img = intent.extras.getString("success_img")
            if (success_img.isNotBlank()) {
                Picasso.with(this@RegisterSuccess)
                        .load(success_img)
                        .into(ivMail, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                loader.visibility = View.GONE
                                ivMail.visibility = View.VISIBLE
                            }

                            override fun onError() {
                            }
                        })
            }
            if (success_msg.isNotBlank()) {
                success_msg = success_msg.replace("\\n", "\n")
                tvtext.text = success_msg
            }
        }
        /*if(AppConstants.getString(AppConstants.KEY_MMRRESP,this@RegisterSuccess).isNotBlank()
                &&AppConstants.getString(AppConstants.KEY_MMRRESP,this@RegisterSuccess).isNotEmpty()){
           var resp =  AppConstants.getString(AppConstants.KEY_MMRRESP,this@RegisterSuccess)
            resp =  resp.replace("\\n", "\n")
            tvtext.text = AppConstants.getString(AppConstants.KEY_MMRRESP,this@RegisterSuccess)
        }*/

        btContinue.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (AppConstants.getString(AppConstants.KEY_ISMMR, this@RegisterSuccess).equals("mmr")) {
                    AppConstants.putString(AppConstants.KEY_ISMMR, "", this@RegisterSuccess)
                    AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "magical", this@RegisterSuccess)
                    val homeIntent = Intent(this@RegisterSuccess, MyAccountScreen::class.java)
                    startActivity(homeIntent)
                    finish()
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                } else {
                    val homeIntent = Intent(this@RegisterSuccess, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentLogin = Intent(this@RegisterSuccess, HomeActivity::class.java)
        startActivity(intentLogin)
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)    }
}
