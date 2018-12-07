package com.influx.marcus.theatres.forgotpassword

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordReq
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordResp
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class ForgotPasswordScreen : AppCompatActivity()/*, CounterClass.CountdownTick */{

    lateinit var forgotPasswordVM: ForgotPasswordVM
    private lateinit var tvTimer: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        forgotPwdObserver()
        initViews()
    }

    private fun initViews() {
        if (AppConstants.getBoolean(AppConstants.KEY_FORGOTREWARDNO, this)) {
            tvTitle.setText(R.string.title_forgot_rewardno)
            tvForget.setText(R.string.email_detail)
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }

        btSendReq.setOnClickListener {
            validate()
        }
        tvTimer = findViewById(R.id.tvTime)
       // CounterClass.setListener(this)
    }


    private fun validate() {
        inputEmail.isErrorEnabled = false
        if (!UtilDialog.isValidEmail(etEmailid.text)) {
            inputEmail.isErrorEnabled = true
            inputEmail.error = "Please enter valid email"
        } else {
            val request = ForgotPasswordReq(etEmailid.text.toString(), AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(this@ForgotPasswordScreen)) {
                UtilsDialog.showProgressDialog(this@ForgotPasswordScreen, "")
                forgotPasswordVM.getForgotPasswordResponse(request)
            }
        }
    }


    private fun forgotPwdObserver() {
        forgotPasswordVM = ViewModelProviders.of(this).get(ForgotPasswordVM::class.java)
        forgotPasswordVM.getForgotPasswordData().observe(this, forgotObs)
        forgotPasswordVM.getApiErrorData().observe(this, errorObs)
    }

    private var forgotObs = object : Observer<ForgotPasswordResp> {
        override fun onChanged(t: ForgotPasswordResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                val i = Intent(this@ForgotPasswordScreen, PasswordResetSuccess::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            } else {
                alert(t.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            }
        }
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }

}
