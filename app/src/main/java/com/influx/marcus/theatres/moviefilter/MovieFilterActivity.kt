package com.influx.marcus.theatres.moviefilter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.filter.Cinema
import com.influx.marcus.theatres.api.ApiModels.filter.DATA
import com.influx.marcus.theatres.api.ApiModels.filter.FilterReq
import com.influx.marcus.theatres.api.ApiModels.filter.FilterResp
import com.influx.marcus.theatres.homepage.ViewPagerAdapter
import com.influx.marcus.theatres.movielisting.MovieListingActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_movie_filter.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast

class MovieFilterActivity : AppCompatActivity(), filterUiHelper {

    private var viewpagerAdapter: ViewPagerAdapter? = null
    private lateinit var filterVM: FilterVM
    var location: ArrayList<Cinema> = ArrayList<Cinema>()
    var genre: ArrayList<String> = ArrayList()
    var language: ArrayList<String> = ArrayList()
    var emptyArrayList: ArrayList<String> = ArrayList()
    lateinit var mcontext: Context
    private lateinit var dateInstance: clearDate
    private lateinit var locationInstance: clearLocationData
    private lateinit var languageInstance: clearLanguage
    private lateinit var genreInstance: clearGenre


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_filter)
        mcontext = this@MovieFilterActivity
        filterObservers()
        initViews()
    }

    fun initViews() {
        filterTabs.setupWithViewPager(filterTabViewpager)
        filterTabViewpager.offscreenPageLimit = 4
        fetchfilterdetails()
        btSave.setOnClickListener {
            AppConstants.is_filterApplied = true
            val i = Intent(mcontext, MovieListingActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        tvReset.setOnClickListener {
            alert("Are you sure you want to reset filters?", "Marcus Theatres")
            {
                positiveButton("YES") { dialog ->
                    disableResetButton()
                    AppConstants.is_filterApplied = false
                    btSave.visibility = View.INVISIBLE
                    clearDataInAllFragments()
                }
                negativeButton("NO") { dialog ->
                    dialog.dismiss()
                }
            }.show().setCancelable(false)

        }
        ivBackToolbar.setOnClickListener {
            val i = Intent(mcontext, MovieListingActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
        }

    }

    private fun clearDataInAllFragments() {
        AppConstants.selectedGenrestoFilter.clear()
        AppConstants.selectedLanguagestoFilter.clear()
        AppConstants.selectedCinmastoFilter.clear()
        AppConstants.putString(AppConstants.KEY_FILTERDATE, "", mcontext)
        AppConstants.putStringList(AppConstants.KEY_FILTERGENRE, emptyArrayList, GenreFragment.mcontext)
        AppConstants.putStringList(AppConstants.KEY_FILTERLANGUAGE, emptyArrayList, GenreFragment.mcontext)
        AppConstants.putString(AppConstants.KEY_FILTERLOCDATE, "", mcontext)
        dateInstance.clearData()
        genreInstance.clearData()
        languageInstance.clearData()
        locationInstance.clearData()
    }


    private fun fetchfilterdetails() {
        val cinema = AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, mcontext)
        val request = FilterReq(cinema, AppConstants.getString(AppConstants.KEY_LATITUDE, mcontext),
                AppConstants.getString(AppConstants.KEY_LONGITUDE, mcontext), AppConstants.APP_PLATFORM, AppConstants.APP_VERSION)
        if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
            UtilsDialog.showProgressDialog(mcontext, "")
            filterVM.filterRequest(request)
        }
    }

    private fun filterObservers() {
        filterVM = ViewModelProviders.of(this).get(FilterVM::class.java)
        filterVM.getfilterData().observe(this, filterObs)
        filterVM.getApiErrorDetails().observe(this, errorObs)
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            clFilter.visibility = View.VISIBLE
            alert(getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        }
    }

    private var filterObs = object : Observer<FilterResp> {
        override fun onChanged(t: FilterResp?) {
            UtilsDialog.hideProgress()
            clFilter.visibility = View.VISIBLE
            if (t!!.STATUS) {
                location = t!!.DATA!!.cinemas as ArrayList<Cinema>
                for (each in t!!.DATA.genres) {
                    genre.add(each)
                }
                for (each in t!!.DATA.languages) {
                    language.add(each)
                }
                setupViewPager(filterTabViewpager, t!!.DATA)


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

    private fun setupViewPager(viewPager: ViewPager, data: DATA) {
        viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val dateFragement = DateFragment.newInstance(data.show_dates, this)
        val locationFragmet = LocationFragment.newInstance(data.cinemas, this)
        val genreFragment = GenreFragment.newInstance(data.genres, this)
        val languageFragment = LanguageFragment.newInstance(data.languages, this)
        dateInstance = dateFragement
        locationInstance = locationFragmet
        genreInstance = genreFragment
        languageInstance = languageFragment
        viewpagerAdapter?.addFragment(dateFragement, "Date")
        viewpagerAdapter?.addFragment(locationFragmet, "Location")
        viewpagerAdapter?.addFragment(genreFragment, "Genre")
        viewpagerAdapter?.addFragment(languageFragment, "Language")
        viewPager.adapter = viewpagerAdapter
    }

    override fun enableSavebutton() {
        btSave.visibility = View.VISIBLE

    }

    override fun disableSaveButton() {
        if(AppConstants.selectedGenrestoFilter.size>0|| AppConstants.selectedLanguagestoFilter.size>0||
                AppConstants.selectedCinmastoFilter.size>0|| AppConstants.getString(AppConstants.KEY_FILTERLOCDATE, mcontext).isNotEmpty()){
            btSave.visibility = View.VISIBLE
        }else{
        btSave.visibility = View.INVISIBLE}

    }

    override fun enableResetButton() {

        ivReset.visibility = View.VISIBLE
        tvReset.visibility = View.VISIBLE

    }

    override fun disableResetButton() {
        if(AppConstants.selectedGenrestoFilter.size>0|| AppConstants.selectedLanguagestoFilter.size>0||
                AppConstants.selectedCinmastoFilter.size>0|| AppConstants.getString(AppConstants.KEY_FILTERLOCDATE, mcontext).isNotEmpty()){
            ivReset.visibility = View.VISIBLE
            tvReset.visibility = View.VISIBLE
        }else{
        ivReset.visibility = View.INVISIBLE
        tvReset.visibility = View.INVISIBLE}
    }

}

interface filterUiHelper {
    fun enableSavebutton()
    fun disableSaveButton()
    fun enableResetButton()
    fun disableResetButton()
}