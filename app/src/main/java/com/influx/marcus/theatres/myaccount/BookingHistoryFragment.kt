package com.influx.marcus.theatres.myaccount

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.BookingHistory
import com.influx.marcus.theatres.myaccount.bookinghistory.BookingHistoryActivity
import com.influx.marcus.theatres.utils.AppConstants


class BookingHistoryFragment : Fragment() {
    lateinit var mcontext: Context

    companion object {
        lateinit var bookingHistory: BookingHistory
        fun newInstance(resp: BookingHistory) = BookingHistoryFragment().apply {
            bookingHistory = resp
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_booking_history, container, false)

        initViews(rootView)
        return rootView

    }

    @SuppressLint("ResourceAsColor")
    fun initViews(rootView: View) {
        mcontext = (this.activity as Context?)!!
        val rvComingUp = rootView.findViewById(R.id.rvComingUp) as RecyclerView
        val rvLastSeen = rootView.findViewById(R.id.rvLastSeen) as RecyclerView
        val tvComingUp = rootView.findViewById(R.id.tvComingUp) as TextView
        val tvLastSeen = rootView.findViewById(R.id.tvLastSeen) as TextView
        val btViewMore = rootView.findViewById<Button>(R.id.btViewMore)
        val tvNotFound = rootView.findViewById<TextView>(R.id.tvNotFound)
        if (bookingHistory.last_seen.size == 0) {
            tvNotFound.text = "Booking History Not Found"
        }
        if (bookingHistory.coming_up.size == 0) {
            tvNotFound.text = "No Upcoming Movies Found"
        }
        tvLastSeen.setOnClickListener {
            rvComingUp.visibility = View.GONE
            rvLastSeen.visibility = View.VISIBLE
            rvLastSeen.setNestedScrollingEnabled(false);
            tvLastSeen.setTextColor(getResources().getColor(R.color.white_light))
            tvComingUp.setTextColor(getResources().getColor(R.color.grey_xlight))
            if (bookingHistory.last_seen.size != 0) {
                rvLastSeen.setHasFixedSize(true)
                AppConstants.putObject(AppConstants.KEY_BOOKING_HISTORY, bookingHistory, mcontext)
                rvLastSeen.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
                val mAdapter = LastSeenAdapter(bookingHistory.last_seen, mcontext)
                rvLastSeen.adapter = mAdapter
                btViewMore.visibility = View.VISIBLE
                tvNotFound.visibility = View.GONE
            } else {
                tvNotFound.visibility = View.VISIBLE
                tvNotFound.text = "Booking History Not Found"
                rvComingUp.visibility = View.GONE
                btViewMore.visibility = View.GONE

            }
        }
        btViewMore.setOnClickListener {
            val i = Intent(context, BookingHistoryActivity::class.java)
            startActivity(i)
        }
        tvComingUp.setOnClickListener {
            rvLastSeen.visibility = View.GONE
            btViewMore.visibility = View.GONE
            rvComingUp.visibility = View.VISIBLE
            rvComingUp.setNestedScrollingEnabled(false);
            tvComingUp.setTextColor(getResources().getColor(R.color.white_light))
            tvLastSeen.setTextColor(getResources().getColor(R.color.grey_xlight))

            if (bookingHistory.coming_up.size != 0) {
                rvComingUp.setHasFixedSize(true)
                rvComingUp.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
                val mAdapter = ComingUpAdapter(bookingHistory.coming_up, mcontext)
                rvComingUp.adapter = mAdapter
                tvNotFound.visibility = View.GONE
            } else {
                tvNotFound.visibility = View.VISIBLE
                tvNotFound.text = "No Upcoming Movies Found"
                rvComingUp.visibility = View.GONE
            }
        }

        if (bookingHistory.coming_up.size != 0) {
            rvComingUp.setHasFixedSize(true)
            rvComingUp.setNestedScrollingEnabled(false);
            rvComingUp.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
            val mAdapter = ComingUpAdapter(bookingHistory.coming_up, mcontext)
            rvComingUp.adapter = mAdapter
            btViewMore.visibility = View.GONE
            tvNotFound.visibility = View.GONE
        } else {
            tvNotFound.visibility = View.VISIBLE
            rvComingUp.visibility = View.GONE
        }
    }
}
