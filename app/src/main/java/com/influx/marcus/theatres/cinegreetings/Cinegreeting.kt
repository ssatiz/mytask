package com.influx.marcus.theatres.cinegreetings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.common.AppStorage
import com.influx.marcus.theatres.login.LoginScreen


class Cinegreeting : AppCompatActivity() {

    lateinit var ivBackArr: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var context: Context
    lateinit var tvSkip: TextView

    private lateinit var rvGreetings: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinegreeting)
        context = this@Cinegreeting
        initViews()
    }

    fun initViews() {
        ivBackArr = findViewById(R.id.ivBackToolbar)
        tvSkip = findViewById(R.id.tvSkip)

        // vertical and cycle layout
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        val layoutManLL = LinearLayoutManager(this,OrientationHelper.HORIZONTAL,false)
        recyclerView = findViewById(R.id.rvGreetingsBanner)
        recyclerView.layoutManager = layoutManager
        //recyclerView.layoutManager = layoutManLL
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = GreetingsAdapter(this)
        //recyclerView.addOnScrollListener(CenterScrollListener())
        recyclerView.scrollToPosition(1)
        ivBackArr.setOnClickListener({ navigateBack() })
        tvSkip.setOnClickListener({ navigateToFnb() })
    }

    fun navigateBack() {
        AppStorage.putBoolean(AppStorage.ISFOODADD, false, context)
        finish()
    }

    fun navigateToFnb() {
        startActivity(Intent(context, LoginScreen::class.java))
        finish()
    }

    override fun onBackPressed() {
        navigateBack()
    }
}
