package com.influx.marcus.theatres.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.login.FacebookRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginResponse
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.utils.*
import kotlinx.android.synthetic.main.activity_emailscreen.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class EnterEmailScreen : AppCompatActivity(){

    lateinit var loginVM: LoginVM
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emailscreen)
        context = this@EnterEmailScreen
        loginObservers()
        initView()
    }

    private fun initView() {
        val ivBack = findViewById<ImageView>(R.id.ivBack) as ImageView
        ivBack.setOnClickListener {
            onBackPressed()
        }
        btSendReq.setOnClickListener { 
            validate() 
        }
    }

    fun validate() {
        inputEMail.isErrorEnabled = false
        if (!UtilDialog.isValidEmail(etEmailid.text)) {
            inputEMail.isErrorEnabled = true
            inputEMail.error = "Please enter email"
        } else {

            var preference = FacebookRequest.Preference(
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context))
            val fbid = intent.extras.get("fbid") as String
            val fname = intent.extras.get("fname") as String
            val lname = intent.extras.get("lname") as String
            val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, context)
            val request = FacebookRequest(fbid, etEmailid.text.toString(), fname, lname, stateId, preference,
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                loginVM.getFacebookResponse(request)
            }
        }
    }

    private fun loginObservers() {
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        loginVM.getLoginRespData().observe(this, emailObs)
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

    private var emailObs = object : Observer<LoginResponse> {
        override fun onChanged(t: LoginResponse?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                val userId = t.DATA.userid
                AppConstants.putString(AppConstants.KEY_USERID, userId, context)
                AppConstants.putObject(AppConstants.KEY_USERDATAOBJ,t.DATA,context)
                if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
                    val i = Intent(context, HomeActivity::class.java)
                    startActivity(i)
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Blockseat") {
                    CommonApi.blockThisSeat(context, userId)
                }else if(AppConstants.getString(AppConstants.KEY_FROM, context) == "UnreserveBlockseat"){
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

    /*override fun onTick(secondsLeft: Long) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        tvTimeCount.text = CounterClass.getFormatedTime()
        LogUtils.d("Timertick", " $minutes - $seconds - $secondsLeft")
        if (secondsLeft < 1) {
            // show timeout
            tvTimeCount.text = "00:00 LEFT"
            tvTimeCount.clearAnimation()
            if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
            if (!isFinishing) {
                alert("Your session has timed out", "Marcus Theatres")
                {
                    positiveButton("OK") { dialog ->
                        startActivity(Intent(context, ShowtimeActivity::class.java))
                        finish()
                        dialog.dismiss()
                    }
                }.show().setCancelable(false)
            }

        } else if (secondsLeft.toInt() == 90) {
            val v: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(500)
            }
        } else if (secondsLeft < 90) {
            blinkTimerText()
        }
    }

    var isTimerBlinking = false
    private fun blinkTimerText() {
        if (!isTimerBlinking) {
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 150 //You can manage the time of the blink with this parameter
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            tvTimeCount.startAnimation(anim)
            isTimerBlinking = true
        }
    }*/
}
