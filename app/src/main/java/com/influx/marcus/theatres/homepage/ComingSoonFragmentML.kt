package com.influx.marcus.theatres.homepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.home.ComingSoon
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils

public class ComingSoonFragmentML : Fragment() {
    lateinit var mcontext: Context
    lateinit var tvNomovie: TextView

    companion object {
        lateinit var comingSoonItem: List<ComingSoon>
        fun newInstance(resp: List<ComingSoon>) = ComingSoonFragmentML().apply {
            comingSoonItem = resp
        }

    }

    private lateinit var recyclerView_movies: RecyclerView
    private val mAdapter: MoviesAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_comingsoon, container, false)
        tvNomovie = rootView.findViewById(R.id.tvNomovie)
        recyclerView_movies = rootView.findViewById(R.id.movies_recyclerview)
        recyclerView_movies.setNestedScrollingEnabled(false)
        mcontext = (this.activity as Context?)!!
        try {
            if (comingSoonItem.size > 0) {
                tvNomovie.visibility = View.GONE
                val mAdapter = MoviesAdapterML(comingSoonItem, mcontext)
                recyclerView_movies.setHasFixedSize(true)
                val mLayoutManager = GridLayoutManager(activity, 2)
                recyclerView_movies.setLayoutManager(mLayoutManager)
                recyclerView_movies.setAdapter(mAdapter)
                recyclerView_movies.addOnItemTouchListener(RecyclerTouchListener(activity,
                        RecyclerTouchListener.OnItemClickListener { view, position ->
                            val item = comingSoonItem.get(position)
                            AppConstants.putString(AppConstants.KEY_SHOWTIME, "home_showtime", mcontext)
                            AppConstants.putString(AppConstants.KEY_TMDBID, item.tMDBId, mcontext)
                            AppConstants.putString(AppConstants.KEY_MOVIECODE, item.movie_code, mcontext)
                            AppConstants.putString(AppConstants.KEY_TMDBID, item.tMDBId, mcontext)
                            AppConstants.putObject(AppConstants.KEY_MOVIEDETAILSOBJECT, item, mcontext)
                            AppConstants.putString(AppConstants.KEY_MOVIETYPE, "banner", mcontext)
                            AppConstants.putString(AppConstants.KEY_THEATREID, item.theatre_code, mcontext)

                            val intent = Intent(activity, ShowtimeActivity::class.java)
                            startActivity(intent)
                            activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

                        }
                ))
            } else {

                tvNomovie.visibility = View.VISIBLE

            }
        } catch (e: Exception) {
            LogUtils.d("ComingSoon", " coming soon crash - ${e.message}")
        }
        return rootView
    }

    public fun scrollToTop() {
        val layoutManager = recyclerView_movies
                .getLayoutManager() as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(0, 0)
    }
}