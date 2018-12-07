package com.influx.marcus.theatres.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.login.LoginResponse
import com.influx.marcus.theatres.api.ApiModels.rewardlogin.Preference
import com.influx.marcus.theatres.api.ApiModels.rewardlogin.RewardLoginRequest
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.*
import kotlinx.android.synthetic.main.activity_reward_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class RewardLogin : AppCompatActivity(){
    lateinit var loginVM: LoginVM
    private lateinit var context: Context
    private lateinit var ivBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_login)
        context = this@RewardLogin
        rewardLoginObservers()
        initViews()
    }

    private fun initViews() {
        ivBack = findViewById<ImageView>(R.id.ivBack) as ImageView
        ivBack.setOnClickListener {
           onBackPressed()
        }

        tvclick.setOnClickListener {
            //  AppConstants.putBoolean(AppConstants.KEY_FORGOTREWARDNO, true, this@RewardLogin)
            //  val i = Intent(this@RewardLogin, ForgotPasswordScreen::class.java)
            //  startActivity(i)
        }
        btnSignIn.setOnClickListener {
            validate()
        }
       // CounterClass.setListener(this)
    }

    fun validate() {

        inputRewardNo.isErrorEnabled = false
        if (etRewardNo.text.isEmpty()) {
            inputRewardNo.isErrorEnabled = true
            inputRewardNo.error = "'Please enter the reward card number"
        }
        inputlastname.isErrorEnabled = false
        if (etLastName.text.isEmpty()) {
            inputlastname.isErrorEnabled = true
            inputlastname.error = "Please enter email/last name"
        } else {
            var preference = Preference(
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, this@RewardLogin),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, this@RewardLogin),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, this@RewardLogin))
            val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, this@RewardLogin)
            val request = RewardLoginRequest(etRewardNo.text.toString(), etLastName.text.toString(), preference, stateId,
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(this@RewardLogin)) {
                UtilsDialog.showProgressDialog(this@RewardLogin, "")
                loginVM.getReviewLoginResponse(request)
            }
        }
    }

    private fun rewardLoginObservers() {
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        loginVM.getLoginRespData().observe(this, loginObs)
        loginVM.getApiErrorData().observe(this, errorObs)
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

    private var loginObs = object : Observer<LoginResponse> {
        override fun onChanged(t: LoginResponse?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                val userId = t.DATA.userid
                AppConstants.putString(AppConstants.KEY_USERID, userId, this@RewardLogin)
                AppConstants.putObject(AppConstants.KEY_USERDATAOBJ,t.DATA,context)
                if(t.DATA.loyalty_no!=null)
                    if (t.DATA.loyalty_no.size>0) {
                        AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, t.DATA.loyalty_no.get(0).cart_no, context)
                    }
                if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
                    val i = Intent(context, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Blockseat") {
                    CommonApi.blockThisSeat(context, userId)
                }
                else if(AppConstants.getString(AppConstants.KEY_FROM, context) == "UnreserveBlockseat"){
                    CommonApi.unReservedSeatLock(context, userId)
                }
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

    override fun onBackPressed() {
        if (AppConstants.getString(AppConstants.KEY_FROM, this) == "Navigation") {
            super.onBackPressed()
        } else {
            alert("Do you want to cancel your booking", "Marcus Theatres")
            {
                positiveButton("YES") { dialog ->
                    val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME,context)
                    when(fromdata){
                        "theatre_showtime"->{
                            val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            dialog.dismiss()
                        }
                        "movie_showtime"->{
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            dialog.dismiss()
                        }else->{
                        val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                        intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intentShowtime)
                        finish()
                        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                        dialog.dismiss()
                        }
                    }
                }
                negativeButton("NO") { dialog ->
                    dialog.dismiss()
                }
            }.show().setCancelable(false)
        }
    }
}
