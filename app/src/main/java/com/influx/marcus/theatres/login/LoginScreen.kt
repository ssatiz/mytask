package com.influx.marcus.theatres.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.login.FacebookRequest
import com.influx.marcus.theatres.api.ApiModels.login.GoogleRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginResponse
import com.influx.marcus.theatres.api.pref.CinemaDetail
import com.influx.marcus.theatres.api.pref.LocalCinemaData
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.forgotpassword.ForgotPasswordScreen
import com.influx.marcus.theatres.guestcheckout.Guest
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.signup.MagicalSignup
import com.influx.marcus.theatres.signup.SignUp
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.*
import kotlinx.android.synthetic.main.activity_login_screen.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList


class LoginScreen : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, FacebookHelper.OnFbSignInListener {

    override fun OnFbSignInComplete(graphResponse: GraphResponse?, error: String?) {
    }

    private lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var GoogleSignIn: View
    lateinit var FacebookSignIn: View
    lateinit var fbLoginManager: LoginManager
    private val RC_SIGN_IN = 7
    lateinit var txtInName: TextInputLayout
    lateinit var txtInPass: TextInputLayout
    lateinit var editName: EditText
    lateinit var editPass: EditText
    lateinit var btnLogin: Button
    lateinit var btnFb: Button
    lateinit var btnGoogle: ImageView
    lateinit var txtForgotPass: TextView
    lateinit var txtNewUser: TextView
    lateinit var tvClickhere: TextView
    lateinit var txtContinue: TextView
    private lateinit var ivBackBtn: ImageView
    lateinit var context: Context
    lateinit var loginRepo: LoginRepo
    lateinit var loginVM: LoginVM
    private lateinit var mAccessToken: AccessToken
    lateinit var tvTimer: TextView
    lateinit var tvJoinnow: TextView
    private lateinit var callbackManager: CallbackManager
    var fbid = ""
    var fname = ""
    var lname = ""
    var email = ""
    var sociallogin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        context = this@LoginScreen
        loginObservers()
        AppConstants.putBoolean(AppConstants.KEY_NEWUSER, false, context)
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this@LoginScreen)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        callbackManager = CallbackManager.Factory.create()
        fbLoginManager = com.facebook.login.LoginManager.getInstance()
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        initView()
    }

    fun initView() {
        tvTimer = findViewById(R.id.tvTime)
        txtInName = findViewById(R.id.inputName)
        txtInPass = findViewById(R.id.inputPass)
        editName = findViewById(R.id.editName)
        editPass = findViewById(R.id.editPass)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPass = findViewById(R.id.tvForget)
        txtNewUser = findViewById(R.id.tvNewUser)
        tvClickhere = findViewById(R.id.tvClickhere)
        txtContinue = findViewById(R.id.tvContinue)
        GoogleSignIn = findViewById(R.id.btnGoogle)
        ivBackBtn = findViewById(R.id.ivBack)
        tvJoinnow = findViewById(R.id.tvJoinnow)
        tvClickhere.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        tvJoinnow.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        tvSignup.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        tvGuest.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

        if (!AppConstants.getString(AppConstants.KEY_GUEST_USER, context).equals("")) {
            AppConstants.putString(AppConstants.KEY_GUEST_USER, "", context)
            AppConstants.putString(AppConstants.KEY_USERID, "", context)
            AppConstants.putString(AppConstants.KEY_EMAIL, "", context)
        }

        if (AppConstants.getBoolean(AppConstants.KEY_BOOKINGFLOW, context)) {
            ivLoginReward.visibility = View.GONE
            tvJoinnow.visibility = View.GONE
            tvNotamember.visibility = View.GONE
            tvContinue.visibility = View.VISIBLE
            tvGuest.visibility = View.VISIBLE
            AppConstants.isFromNavDraw = false
        } else {
            tvContinue.visibility = View.GONE
            tvGuest.visibility = View.GONE
        }

        tvJoinnow.setOnClickListener {
            val i = Intent(context, MagicalSignup::class.java)
            i.putExtra("sidemenu", "false")
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        tvGuest.setOnClickListener {
            val i = Intent(context, Guest::class.java)
            context.startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        ivBackBtn.setOnClickListener {
            onBackPressed()
        }
        tvSignup.setOnClickListener {
            AppConstants.putString(AppConstants.KEY_SOCIAL, "", context)
            AppConstants.putString(AppConstants.KEY_SOCIALID, "", context)
            val i = Intent(this@LoginScreen, com.influx.marcus.theatres.signup.SignUp::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        FacebookSignIn = findViewById(R.id.btnFb)
        btnLogin.setOnClickListener({ validate() })

        txtForgotPass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                AppConstants.putBoolean(AppConstants.KEY_FORGOTREWARDNO, false, context)
                val i = Intent(context, ForgotPasswordScreen::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            }
        })


        FacebookSignIn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                sociallogin = "facebook"
                LoginManager.getInstance().logInWithReadPermissions(this@LoginScreen, Arrays.asList("public_profile"));
                fblogin()
            }
        })

        tvClickhere.setOnClickListener {
            val i = Intent(context, RewardLogin::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        tvJoinnow.setOnClickListener {
            val i = Intent(context, MagicalSignup::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        fbLoginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
            }

            override fun onCancel() {
            }

            override fun onError(exception: FacebookException) {
            }
        })


        GoogleSignIn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                sociallogin = "google"
                mGoogleApiClient!!.connect();
                signIn()

            }
        })
        //  CounterClass.setListener(this)
    }

    fun validate() {

        inputName.isErrorEnabled = false
        inputPass.isErrorEnabled = false
        if (editName.text.isEmpty()) {
            inputName.isErrorEnabled = true
            inputName.error = "Please enter email"
        } else if (!UtilDialog.isValidEmail(editName.text)) {
            inputName.isErrorEnabled = true
            inputName.error = "Please enter valid email"
        } else if (editPass.text.isEmpty()) {
            inputPass.isErrorEnabled = true
            inputPass.error = "Please enter password"
        } else if (editPass.text.length < 6) {
            inputPass.isErrorEnabled = true
            inputPass.error = "Password must contain atleast 6 letters"
        } else {
            val request = LoginRequest(editName.text.toString(), editPass.text.toString(), AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                loginVM.getloginResponse(request)
            }
        }
    }

    private fun loginObservers() {
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        loginVM.getLoginRespData().observe(this, loginObss)
        loginVM.getApiErrorData().observe(this, errorObs)
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                positiveButton("OK") { dialog ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private var loginObss = object : Observer<LoginResponse> {
        override fun onChanged(t: LoginResponse?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                AppConstants.putBoolean(AppConstants.KEY_NEWUSER, t.DATA.new_user, context)
                AppConstants.putString(AppConstants.KEY_GUEST_USER, "", context)
                if (t.DATA.new_user) {
                    doAsync {
                        AppConstants.putString(AppConstants.KEY_LASTNAME, lname, context)
                        AppConstants.putString(AppConstants.KEY_EMAIL, email, context)
                        AppConstants.putObject(AppConstants.KEY_USERDATAOBJ, t.DATA, context)
                        AppConstants.putString(AppConstants.KEY_SOCIAL, sociallogin, context)
                        uiThread {
                            val i = Intent(context, SignUp::class.java)
                            startActivity(i)
                            finish()
                            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                        }
                    }
                } else {

                    doAsync {
                        val userId = t.DATA.userid
                        AppConstants.putString(AppConstants.KEY_USERID, userId, context)
                        AppConstants.putObject(AppConstants.KEY_USERDATAOBJ, t.DATA, context)
                        AppConstants.putString(AppConstants.KEY_EMAIL, t.DATA.email, context)
                        AppConstants.putString(AppConstants.KEY_FIRSTNAME, t.DATA.firstname, context)

                        if (t.DATA.preference!!.cinemas != null) {
                            AppConstants.cinemaList.clear()
                            AppConstants.cinemaList.addAll(t.DATA.preference.cinemas)
                            AppConstants.putStringList(AppConstants.KEY_PREFEREDCINEMA,
                                    AppConstants.cinemaList, context)
                        }

                        AppConstants.localCinemadata.clear()
                        val cinemaNames: ArrayList<String> = ArrayList()
                        if (t.DATA.preference!!.cinemas_name != null) {
                            cinemaNames.addAll(t.DATA.preference!!.cinemas_name)
                        }
                        cinemaNames.forEachIndexed { index, cinemaName ->
                            AppConstants.localCinemadata.add(
                                    CinemaDetail(cinemaName,
                                            AppConstants.cinemaList.get(index))
                            )
                        }

                        val localCinemaDetail = LocalCinemaData(AppConstants.localCinemadata)
                        AppConstants.putObject(AppConstants.KEY_PREFEREDCINEMALISTOBJ,
                                localCinemaDetail, context)


                        if (t.DATA.preference!!.languages != null) {
                            AppConstants.languageList.clear()
                            AppConstants.languageList.addAll(t.DATA.preference!!.languages)
                            AppConstants.putStringList(AppConstants.KEY_PREFEREDLANGUAGE,
                                    AppConstants.languageList, context)
                        }
                        if (t.DATA.preference!!.genres != null) {
                            AppConstants.genreList.clear()
                            AppConstants.genreList.addAll(t.DATA.preference!!.genres)
                            AppConstants.putStringList(AppConstants.KEY_PREFEREDGENRE,
                                    AppConstants.genreList, context)
                        }


                        if (t.DATA.loyalty_no != null)
                            if (t.DATA.loyalty_no.size > 0) {
                                AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, t.DATA.loyalty_no.get(0).cart_no, context)
                            }

                        uiThread {
                            if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
                                val i = Intent(context, HomeActivity::class.java)
                                startActivity(i)
                                finish()
                                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

                            } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Blockseat") {
                                CommonApi.blockThisSeat(context, userId)
                            } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "UnreserveBlockseat") {
                                CommonApi.unReservedSeatLock(context, userId)
                            }
                        }
                    }
                }
            } else {
                alert(t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }



    fun fblogin() {
        LoginManager.getInstance().registerCallback(callbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {


                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
                    try {
                        if (`object`.has("first_name")) {
                            fname = `object`.getString("first_name")
                        }
                        if (`object`.has("last_name")) {
                            lname = `object`.getString("last_name")
                        }
                        if (`object`.has("email")) {
                            email = `object`.getString("email")

                        }
                        if (`object`.has("gender")) {

                        }

                        if (`object`.has("id")) {
                            fbid = `object`.getString("id")
                            AppConstants.putString(AppConstants.KEY_SOCIALID, fbid, context)
                        }
                        var preference = FacebookRequest.Preference(
                                AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context),
                                AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context),
                                AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context))

                        val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, context)
                        val request = FacebookRequest(fbid, email, fname, lname, stateId, preference,
                                AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                        if (UtilsDialog.isNetworkStatusAvialable(context)) {
                            UtilsDialog.showProgressDialog(context, "")
                            loginVM.getFacebookResponse(request)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()

            }

            override fun onCancel() {
                // do nothing on cancel
            }

            override fun onError(error: FacebookException) {
                alert(getString(R.string.ohinternalservererror), "Marcus Theatres") {

                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()
            }
        })
    }

    private fun signIn() {
        mGoogleApiClient!!.clearDefaultAccountAndReconnect()
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun handleSignInResult(result: GoogleSignInResult) {
        LogUtils.e("handleSignInResult", "handleSignInResult:" + result.isSuccess)
        LogUtils.d("LoginGoogle", "Google signin result ${result.signInAccount.toString()}")
        LogUtils.d("Login Google", " Signin error - ${result.status.toString()}")
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount
            try {
                LogUtils.e("name", "display name: " + acct!!.displayName!!)
                fname = acct!!.displayName.toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            email = acct!!.email.toString()
            val googleid = acct!!.id.toString()
            AppConstants.putString(AppConstants.KEY_SOCIALID, email, context)
            val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, context)
            var preference = GoogleRequest.Preference(
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context))
            val request = GoogleRequest(email, fname, "", stateId, preference,
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                loginVM.getGoogleResponse(request)
            }

        } else {
            // show unauthenticated ui
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        } else {
            if (callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
            super.onBackPressed()
            overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)

        } else {
            alert("Do you want to cancel your booking", "Marcus Theatres")
            {
                positiveButton("YES") { dialog ->
                    val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME, context)
                    when (fromdata) {
                        "theatre_showtime" -> {
                            val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)
                            dialog.dismiss()
                        }
                        "movie_showtime" -> {
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)
                            dialog.dismiss()
                        }
                        else -> {
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)
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

    public override fun onStart() {
        super.onStart()
        val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            Log.d("handleSignInResult", "Got cached sign-in")
            val result = opr.get()
            handleSignInResult(result)
        } else {
            opr.setResultCallback { googleSignInResult ->
                handleSignInResult(googleSignInResult)
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        alert("Unable to get Response",
                getString(R.string.marcus_theatre_title)) {
            positiveButton("OK") {
                it.dismiss()
            }
        }.show()
        Log.d("handleSignInResult", "onConnectionFailed:$connectionResult")
    }

    override fun onPause() {
        super.onPause()
    }

}
