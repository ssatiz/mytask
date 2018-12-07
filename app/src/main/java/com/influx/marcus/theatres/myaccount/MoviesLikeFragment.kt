package com.influx.marcus.theatres.myaccount


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
import com.influx.marcus.theatres.api.ApiModels.myaccount.MovieYoulikeItem
import com.influx.marcus.theatres.homepage.MoviesAdapter
import com.influx.marcus.theatres.homepage.RecyclerTouchListener
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.EqualSpacingItemDecoration
import com.influx.marcus.theatres.utils.LogUtils


/**
 * A simple [Fragment] subclass.
 *
 */
class MoviesLikeFragment : Fragment() {

    lateinit var mcontext: Context

    companion object {
        lateinit var movie_youlike: List<MovieYoulikeItem>

        fun newInstance(moviesList: List<MovieYoulikeItem>) = MoviesLikeFragment().apply {
            movie_youlike = moviesList
        }

    }


    private lateinit var rvMovies: RecyclerView
    private lateinit var tvNoMovies: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_movies_like, container, false)
        mcontext = (this.activity as Context?)!!
        tvNoMovies = rootView.findViewById(R.id.tvNoMovies)
        rvMovies = rootView.findViewById(R.id.movies_recyclerview)
        rvMovies.addItemDecoration(EqualSpacingItemDecoration(resources.getDimension(R.dimen._10sdp).toInt(), EqualSpacingItemDecoration.GRID))
        rvMovies.setNestedScrollingEnabled(false);

        try {
            val mAdapter = MoviesAdapter(movie_youlike, mcontext)
            rvMovies.setHasFixedSize(true)
            val mLayoutManager = GridLayoutManager(activity, 2)
            if (movie_youlike.size > 0) {
                rvMovies.visibility = View.VISIBLE
                tvNoMovies.visibility = View.GONE
                rvMovies.setLayoutManager(mLayoutManager)
                rvMovies.setAdapter(mAdapter)
                rvMovies.addOnItemTouchListener(RecyclerTouchListener(activity, RecyclerTouchListener.OnItemClickListener { view, position ->
                    val item = movie_youlike.get(position)
                    AppConstants.putString(AppConstants.KEY_TMDBID, item.tMDBId, mcontext)
                    AppConstants.putString(AppConstants.KEY_MOVIECODE, item.movie_code, mcontext)
                    AppConstants.putObject(AppConstants.KEY_MOVIEDETAILSOBJECT, item, mcontext)
                    AppConstants.putString(AppConstants.KEY_MOVIETYPE, "banner", mcontext)
                    val intent = Intent(activity, ShowtimeActivity::class.java)
                    startActivity(intent)
                    activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                }))
            } else {
                rvMovies.visibility = View.GONE
                tvNoMovies.visibility = View.VISIBLE
            }


        } catch (e: Exception) {
            LogUtils.d("NowShowing", "Exception loading data ${e.message}")
        }

        return rootView
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
    }
}
