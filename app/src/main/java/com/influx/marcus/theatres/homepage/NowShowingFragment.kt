package com.influx.marcus.theatres.homepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import android.support.v7.widget.LinearLayoutManager
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.NowShowing


/**
 * Created by influx on 30-03-2018.
 */
public class NowShowingFragment : Fragment() {
    lateinit var mcontext: Context
    lateinit var tvNomovie: TextView

    companion object {
        lateinit var nowShowingItem: List<NowShowing>

        fun newInstance(resp: List<NowShowing>) = NowShowingFragment().apply {
            nowShowingItem = resp
        }

    }

    private lateinit var recyclerView_movies: RecyclerView
    private lateinit var mAdapter: MoviesAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mcontext = (this.activity as Context?)!!
        val rootView = inflater!!.inflate(R.layout.fragment_nowshowing, container,
                false)
        recyclerView_movies = rootView.findViewById(R.id.movies_recyclerview)
        tvNomovie = rootView.findViewById(R.id.tvNomovie)
        recyclerView_movies.setNestedScrollingEnabled(false)
        /*     recyclerView_movies.addItemDecoration(EqualSpacingItemDecoration(resources.getDimension(R.dimen._10sdp).toInt(),
                     EqualSpacingItemDecoration.GRID))*/
        try {
            if (nowShowingItem.size > 0) {
                tvNomovie.visibility = View.GONE
                mAdapter = MoviesAdapter(nowShowingItem, mcontext)
                recyclerView_movies.setHasFixedSize(true)
                val mLayoutManager = GridLayoutManager(activity, 2)
                recyclerView_movies.setLayoutManager(mLayoutManager)
                recyclerView_movies.setAdapter(mAdapter)
                recyclerView_movies.addOnItemTouchListener(RecyclerTouchListener(activity,
                        RecyclerTouchListener.OnItemClickListener { view, position ->
                            val item = nowShowingItem.get(position)
                            AppConstants.putString(AppConstants.KEY_SHOWTIME, "home_showtime", mcontext)
                            AppConstants.putString(AppConstants.KEY_TMDBID, item.tMDBId, mcontext)
                            AppConstants.putString(AppConstants.KEY_MOVIECODE, item.movie_code, mcontext)
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
            LogUtils.d("NowShowing", "Exception loading data ${e.message}")
        }
        return rootView
    }

    public fun scrollToTop() {
        val layoutManager = recyclerView_movies
                .getLayoutManager() as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(0, 0)
    }
}