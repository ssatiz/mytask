package com.influx.marcus.theatres.homepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.showtime.ShowtimeActivity

class MovieDetailActivity : AppCompatActivity() {
    lateinit var imgBack: ImageView
    lateinit var btnBookNow: Button
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_detail_layout)
        context = this@MovieDetailActivity
        initView()
    }

    fun initView() {
        imgBack = findViewById(R.id.imgBack)
        btnBookNow = findViewById(R.id.btbooknow)
        imgBack.setOnClickListener({
            finish()
            overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)
        })

        btnBookNow.setOnClickListener({ navigateToShowTime() })
    }

    fun navigateToShowTime() {
        val intent = Intent(context, ShowtimeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

    }

}
