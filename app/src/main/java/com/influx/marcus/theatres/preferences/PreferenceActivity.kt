package com.influx.marcus.theatres.preferences


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.UserPreferences
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.updatePref.Preference
import com.influx.marcus.theatres.api.ApiModels.updatePref.UpdatePrefReq
import com.influx.marcus.theatres.api.pref.LocalCinemaData
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.myaccount.MyAccountScreen
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CommonApi
import com.influx.marcus.theatres.utils.LogUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.textColor


class PreferenceActivity : AppCompatActivity(),
        ExpandableListAdapter.ExpandableListAdapterListener,
        ActivityUiHelper {


    private lateinit var context: Context
    private lateinit var tvSkip: TextView
    private lateinit var btnSave: Button
    private lateinit var tvLocationTab: TextView
    private lateinit var tvPreferenceTab: TextView
    private lateinit var grpNav: Group
    private lateinit var prefs: UserPreferences
    private var isGPSFLOW = false
    private var isEditMode = false
    private val KEY_GPSFLOW = "GPSFLOW"
    private val KEY_EDITMODE = "EDITMODE"
    var preferedLocsResp: PreferedLocsResp? = null
    private lateinit var parentLayout: ScrollView
    private lateinit var llTabs: LinearLayout
    private lateinit var viewLineTabs: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        context = this@PreferenceActivity
        if (intent.hasExtra(KEY_GPSFLOW)) {
            isGPSFLOW = intent.getBooleanExtra(KEY_GPSFLOW, false)
        }
        if (intent.hasExtra(KEY_EDITMODE)) {
            isEditMode = intent.getBooleanExtra(KEY_EDITMODE, false)
        }
        initViews()
    }

    private fun initViews() {
        parentLayout = findViewById(R.id.scrollLayoutParent)
        parentLayout.viewTreeObserver.addOnGlobalLayoutListener(viewTreeObserver)
        llTabs = findViewById(R.id.llTabs)
        viewLineTabs = findViewById(R.id.viewLineTabs)
        grpNav = findViewById(R.id.gprNavigationTabs)
        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            navigateToHomeIfValid()
        }
        tvSkip = findViewById(R.id.tvSkip)
        tvSkip.setOnClickListener {
            skipToHome()
        }
        if (isEditMode) {
            tvSkip.visibility = View.VISIBLE
        }
        tvLocationTab = findViewById(R.id.tvLocationTab)
        tvPreferenceTab = findViewById(R.id.tvPreferenceTab)

        tvLocationTab.setOnClickListener {
            if (isGPSFLOW) {
                populateLocationGPSFragment()
            } else {
                populateLocationNOGPSFragment()
            }
        }
        setColorForTextView(tvLocationTab, R.color.marcus_red)
        updateVariableForPreferences()
        if (isGPSFLOW) {
            populateLocationGPSFragment()
        } else {
            populateLocationNOGPSFragment()
        }
    }

    private fun skipToHome() {

        if (AppConstants.getString(AppConstants.KEY_FLOWFROMMODIFY, context).equals("modify")) {
            AppConstants.putString(AppConstants.KEY_FLOWFROMMODIFY, "", context)
            AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "preference", context)
            val myAccount = Intent(context, MyAccountScreen::class.java)
            myAccount.putExtra("modify",true)
            context.startActivity(myAccount)
            (context as Activity).finish()
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        } else {
            val homeIntent = Intent(context, HomeActivity::class.java)
            context.startActivity(homeIntent)
            (context as Activity).finish()
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
    }

    private fun updateVariableForPreferences() {

        AppConstants.cinemaList = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA,
                context)

        val localCinemaData: LocalCinemaData? = AppConstants.getObject(AppConstants.KEY_PREFEREDCINEMALISTOBJ,
                context, LocalCinemaData::class.java as Class<Any>) as LocalCinemaData?

        if (localCinemaData != null) {
            AppConstants.localCinemadata.clear()
            AppConstants.localCinemadata.addAll(localCinemaData.cinemas)
        }

        AppConstants.genreList = AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE,
                context)
        AppConstants.languageList = AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE,
                context)
        AppConstants.state = AppConstants.getString(AppConstants.KEY_STATE_CODE,
                context)
        AppConstants.latitute = AppConstants.getString(AppConstants.KEY_LATITUDE,
                context)
        AppConstants.longitude = AppConstants.getString(AppConstants.KEY_LONGITUDE,
                context)

    }

    private fun navigateToHomeIfValid() {
        val curFragment = supportFragmentManager.findFragmentById(R.id.containerFrame)
        if (curFragment is LocationGPSFragment) {
            btnSave.setText("Done")
            tvPreferenceTab.performClick()
            AppConstants.cinemaList.clear()

            for (eachCinema in AppConstants.localCinemadata) {
                AppConstants.cinemaList.add(eachCinema.cinemaCode)
            }

            AppConstants.putStringList(AppConstants.KEY_PREFEREDCINEMA,
                    AppConstants.cinemaList, context)

            val localCinemaDetail = LocalCinemaData(AppConstants.localCinemadata)
            AppConstants.putObject(AppConstants.KEY_PREFEREDCINEMALISTOBJ,
                    localCinemaDetail, context)

            AppConstants.putString(AppConstants.KEY_STATE_CODE,
                    AppConstants.state, context)
            AppConstants.putString(AppConstants.KEY_LONGITUDE,
                    AppConstants.longitude, context)
            AppConstants.putString(AppConstants.KEY_LATITUDE,
                    AppConstants.latitute, context)
            tvSkip.visibility = View.VISIBLE

        } else if (curFragment is LocationNONGPSFragment) {
            btnSave.setText("Done")
            tvPreferenceTab.performClick()
            AppConstants.cinemaList.clear()
            for (eachCinema in AppConstants.localCinemadata) {
                AppConstants.cinemaList.add(eachCinema.cinemaCode)
            }

            AppConstants.putStringList(AppConstants.KEY_PREFEREDCINEMA,
                    AppConstants.cinemaList, context)

            val localCinemaDetail = LocalCinemaData(AppConstants.localCinemadata)
            AppConstants.putObject(AppConstants.KEY_PREFEREDCINEMALISTOBJ,
                    localCinemaDetail, context)

            AppConstants.putString(AppConstants.KEY_STATE_CODE,
                    AppConstants.state, context)
            AppConstants.putString(AppConstants.KEY_LONGITUDE,
                    AppConstants.longitude, context)
            AppConstants.putString(AppConstants.KEY_LATITUDE,
                    AppConstants.latitute, context)
            tvSkip.visibility = View.VISIBLE
        } else {
            AppConstants.putStringList(AppConstants.KEY_PREFEREDLANGUAGE,
                    AppConstants.languageList, context)
            AppConstants.putStringList(AppConstants.KEY_PREFEREDGENRE,
                    AppConstants.genreList, context)

            AppConstants.cinemaList.clear()
            for (eachCinema in AppConstants.localCinemadata) {
                AppConstants.cinemaList.add(eachCinema.cinemaCode)
            }

            AppConstants.putStringList(AppConstants.KEY_PREFEREDCINEMA,
                    AppConstants.cinemaList, context)

            val localCinemaDetail = LocalCinemaData(AppConstants.localCinemadata)
            AppConstants.putObject(AppConstants.KEY_PREFEREDCINEMALISTOBJ,
                    localCinemaDetail, context)


            AppConstants.putString(AppConstants.KEY_STATE_CODE,
                    AppConstants.state, context)
            AppConstants.putString(AppConstants.KEY_LONGITUDE,
                    AppConstants.longitude, context)
            AppConstants.putString(AppConstants.KEY_LATITUDE,
                    AppConstants.latitute, context)


            if (isEditMode) {
                if (AppConstants.genreList.size < 1 && AppConstants.languageList.size < 1) {
                    try {
                        alert("Do you want to proceed without choosing any preferences",
                                "Marcus Theatres") {
                            positiveButton("Yes") {
                                if (AppConstants.getString(AppConstants.KEY_USERID, context).isNotBlank()) {
                                    val userid = AppConstants.getString(AppConstants.KEY_USERID, context)
                                    var prefs = UpdatePrefReq(
                                            userid,
                                            Preference(
                                                    AppConstants.cinemaList,
                                                    AppConstants.genreList,
                                                    AppConstants.languageList
                                            ),AppConstants.state,
                                            AppConstants.APP_VERSION,
                                            AppConstants.APP_PLATFORM
                                    )
                                    CommonApi.updateUserPreferences(context,
                                            prefs)
                                }else{
                                    AppConstants.isFromNavDraw=false
                                    val homeIntent = Intent(context, HomeActivity::class.java)
                                    startActivity(homeIntent)
                                    finish()
                                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                                }
                                it.dismiss()

                            }
                            negativeButton("No") {
                                it.dismiss()
                            }
                        }.show().setCancelable(false)
                    } catch (e: Exception) {
                        LogUtils.d("Prefrences", "while showing popup ${e.message}")
                    }
                } else {
                    if (AppConstants.getString(AppConstants.KEY_USERID, context).isNotBlank()) {
                        val userid = AppConstants.getString(AppConstants.KEY_USERID, context)
                        var prefs = UpdatePrefReq(
                                userid,
                                Preference(
                                        AppConstants.cinemaList,
                                        AppConstants.genreList,
                                        AppConstants.languageList
                                ),AppConstants.state,
                                AppConstants.APP_VERSION,
                                AppConstants.APP_PLATFORM
                        )
                        CommonApi.updateUserPreferences(context,
                                prefs)
                    }else{
                        AppConstants.isFromNavDraw=false
                        val homeIntent = Intent(context, HomeActivity::class.java)
                        startActivity(homeIntent)
                        finish()
                        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                    }
                }
            } else {
                if (AppConstants.getString(AppConstants.KEY_USERID, context).isNotBlank()) {
                    val userid = AppConstants.getString(AppConstants.KEY_USERID, context)
                    var prefs = UpdatePrefReq(
                            userid,
                            Preference(
                                    AppConstants.cinemaList,
                                    AppConstants.genreList,
                                    AppConstants.languageList
                            ),AppConstants.state,
                            AppConstants.APP_VERSION,
                            AppConstants.APP_PLATFORM
                    )
                    CommonApi.updateUserPreferences(context,
                            prefs)
                }else{
                    AppConstants.isFromNavDraw=false
                    val homeIntent = Intent(context, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                }
            }
        }
    }

    private fun populatePreferencesFragment() {
        btnSave.setText("Done")
        setColorForTextView(tvLocationTab, R.color.white)
        setColorForTextView(tvPreferenceTab, R.color.marcus_red)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrame, LangGenreFragment.newInstance(preferedLocsResp!!), "langGenre")
                .commit()
    }

    private fun populateLocationNOGPSFragment() {
        btnSave.setText("Next")
        setColorForTextView(tvLocationTab, R.color.marcus_red)
        setColorForTextView(tvPreferenceTab, R.color.white)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrame, LocationNONGPSFragment.newInstance(isEditMode, this), "locationNonGps")
                .commit()

    }

    private fun populateLocationGPSFragment() {
        btnSave.setText("Next")
        setColorForTextView(tvLocationTab, R.color.marcus_red)
        setColorForTextView(tvPreferenceTab, R.color.white)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrame, LocationGPSFragment.newInstance(isEditMode, this), "locationGps")
                .commit()
    }


    fun setColorForTextView(tv: TextView, colorInt: Int) {
        tv.textColor = resources.getColor(colorInt)
        for (drawable in tv.compoundDrawables) {
            if (drawable != null) drawable.setColorFilter(resources.getColor(colorInt), PorterDuff.Mode.SRC_IN)
        }
    }

    override fun expand(v: View, select: Boolean) {
        //todo interface implement
    }

    override fun enableSaveButton() {
        btnSave.visibility = View.VISIBLE
        tvPreferenceTab.isClickable = true
        tvPreferenceTab.setOnClickListener {
            populatePreferencesFragment()
        }
    }

    override fun disableSaveButton() {
        btnSave.visibility = View.GONE
        tvPreferenceTab.isClickable = false
        tvPreferenceTab.setOnClickListener {
            alert("Please select your preferred cinema",
                    "Marcus Theatres") {
                positiveButton("OK") {
                    it.dismiss()

                }
            }.show().setCancelable(false)
        }
        tvPreferenceTab.isClickable = true
    }

    override fun enableSaveOnly() {
        btnSave.visibility = View.VISIBLE
    }

    override fun disableSaveOnly() {
        btnSave.visibility = View.GONE
    }

    override fun enableSkipOnly() {
        tvSkip.visibility = View.VISIBLE
    }

    override fun disableSkipOnly() {
        tvSkip.visibility = View.GONE
    }

    override fun gotoManualFlow() {

        AppConstants.cinemaList.clear()
        AppConstants.localCinemadata.clear()
        updateVariableForPreferences()
        isGPSFLOW = false
        populateLocationNOGPSFragment()

    }

    override fun gotoAutoDetectFlow() {
        AppConstants.cinemaList.clear()
        AppConstants.localCinemadata.clear()
        updateVariableForPreferences()
        isGPSFLOW = true
        populateLocationGPSFragment()
    }

    override fun hideNavTabs() {
        llTabs.visibility = View.GONE
        viewLineTabs.visibility = View.GONE
    }

    override fun showNavTabs() {
        llTabs.visibility = View.VISIBLE
        viewLineTabs.visibility = View.VISIBLE
    }

    var viewTreeObserver: ViewTreeObserver.OnGlobalLayoutListener =
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val heightDiff = parentLayout.getRootView().getHeight() - parentLayout.getHeight()
                    val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).getTop()
                    if (heightDiff <= 400) {
                        onHideKeyboard()
                    } else {
                        val keyboardHeight = heightDiff - contentViewTop
                        onShowKeyboard(keyboardHeight)
                    }
                }
            }

    /**
     * when keyboard is shown
     */
    private fun onShowKeyboard(keyboardHeight: Any) {
        hideNavTabs()
    }

    /**
     * when keyboard is hidden
     */
    private fun onHideKeyboard() {
        showNavTabs()
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}

interface ActivityUiHelper {
    fun enableSaveButton()
    fun disableSaveButton()
    fun enableSaveOnly()
    fun disableSaveOnly()
    fun enableSkipOnly()
    fun disableSkipOnly()
    fun gotoManualFlow()
    fun gotoAutoDetectFlow()
    fun hideNavTabs()
    fun showNavTabs()
}


