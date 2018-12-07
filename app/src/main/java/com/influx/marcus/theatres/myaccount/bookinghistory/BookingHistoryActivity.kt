package com.influx.marcus.theatres.myaccount.bookinghistory

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.BookingHistory
import com.influx.marcus.theatres.utils.AppConstants
import kotlinx.android.synthetic.main.activity_booking_history.*

class BookingHistoryActivity : AppCompatActivity() {


    private val context: Context = this@BookingHistoryActivity
    private lateinit var ivBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)
        ivBack = findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            onBackPressed()
        }
        val bookingHistoryData: BookingHistory? = AppConstants.getObject(AppConstants.KEY_BOOKING_HISTORY,
                context, BookingHistory::class.java as Class<Any>) as BookingHistory?
        val lastSeenData = bookingHistoryData!!.last_seen
        rvBookingHistory.setHasFixedSize(true)
        rvBookingHistory.setLayoutManager(LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false))
        val mAdapter = BookingHistoryAdapter(lastSeenData, context)
        rvBookingHistory.adapter = mAdapter
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
    }
}
