package com.influx.marcus.theatres.homepage

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.ComingSoon
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.EventCinema
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.NowShowing

import com.influx.marcus.theatres.api.ApiModels.myaccount.MovieYoulikeItem
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView


/**
 * Created by influx on 02-04-2018.
 */
class MoviesAdapter(dataList: List<out Any>, context: Context?) : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {
    lateinit var eventCinemaItem: List<EventCinema>
    lateinit var comingSoon: List<ComingSoon>
    lateinit var nowshowing: List<NowShowing>
    lateinit var moviesLikeList: List<MovieYoulikeItem>
    var moviedata: String = ""

    var size = 0

    var mcontext = context

    init {
        if (dataList.size > 0) {

            if (dataList.get(0) is EventCinema) {
                eventCinemaItem = dataList as List<EventCinema>
                moviedata = "eventcinema"
                size = eventCinemaItem.size
            } else if (dataList.get(0) is ComingSoon) {
                comingSoon = dataList as List<ComingSoon>
                moviedata = "comingsoon"
                size = comingSoon.size
            } else if (dataList.get(0) is NowShowing) {
                nowshowing = dataList as List<NowShowing>
                moviedata = "nowshowing"
                size = nowshowing.size
            } else if (dataList.get(0) is MovieYoulikeItem) {
                moviesLikeList = dataList as List<MovieYoulikeItem>
                moviedata = "moviesulike"
                size = moviesLikeList.size
            }


        }
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var movieImage: ImageView
        lateinit var txtMovieName: TextView
        lateinit var genre: TextView
        lateinit var tvDirector: TextView
        lateinit var tvReleaseDate: TextView
        lateinit var tvRating: TextView
        lateinit var rvMovieAdapterLayout: RelativeLayout
        lateinit var loader : AVLoadingIndicatorView
        lateinit var linearLayout2 : LinearLayout

        init {
            movieImage = view.findViewById(R.id.ivCinemaLoc)
            txtMovieName = view.findViewById(R.id.tvMovieName)
            tvDirector = view.findViewById(R.id.tvDirector)
            tvReleaseDate = view.findViewById(R.id.tvReleaseDate)
            tvRating = view.findViewById(R.id.tvRating)
            rvMovieAdapterLayout = view.findViewById(R.id.rvMovieAdapterLayout)
            loader = view.findViewById(R.id.loader)
            linearLayout2 = view.findViewById(R.id.linearLayout2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            if (moviedata == "eventcinema") {
                val eachItem = eventCinemaItem.get(position)
                if (eachItem.image_url.isNotBlank()) {
                    Picasso.with(mcontext).load(eachItem.image_url)
                            .into(holder.movieImage, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    holder.loader.visibility = View.GONE
                                    holder.movieImage.visibility = View.VISIBLE
                                    holder.linearLayout2.visibility = View.VISIBLE
                                }

                                override fun onError() {
                                    holder.loader.visibility = View.GONE
                                }
                            })
                }
                holder.txtMovieName.text = eachItem.name
                holder.tvRating.text = eachItem.rating
                var genre = eachItem.genre.toString()
                genre = genre.replace("[", "")
                genre = genre.replace("]", "")
                holder.tvDirector.text = genre
                holder.tvReleaseDate.visibility = View.GONE
            } else if (moviedata == "comingsoon") {
                val eachItem = comingSoon.get(position)
                if (eachItem.image_url.isNotBlank()) {

                    Picasso.with(mcontext).load(eachItem.image_url)
                            .into(holder.movieImage, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    holder.loader.visibility = View.GONE
                                    holder.movieImage.visibility = View.VISIBLE
                                    holder.linearLayout2.visibility = View.VISIBLE
                                }

                                override fun onError() {
                                    holder.loader.visibility = View.GONE
                                }
                            })

                }
                holder.txtMovieName.text = eachItem.name
                holder.tvRating.text = eachItem.rating
                var genre = eachItem.genre.toString()
                genre = genre.replace("[", "")
                genre = genre.replace("]", "")
                holder.tvDirector.text = genre
                holder.tvReleaseDate.visibility = View.VISIBLE
                holder.tvReleaseDate.text = eachItem.releaseDateStr
            } else if (moviedata == "nowshowing") {
                val eachItem = nowshowing.get(position)
                holder.tvRating.text = eachItem.rating
                if (eachItem.image_url.isNotBlank()) {
                    Picasso.with(mcontext).load(eachItem.image_url)
                            .into(holder.movieImage, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    holder.loader.visibility = View.GONE
                                    holder.movieImage.visibility = View.VISIBLE
                                    holder.linearLayout2.visibility = View.VISIBLE

                                }

                                override fun onError() {
                                    holder.loader.visibility = View.GONE
                                }
                            })

                }
                holder.txtMovieName.text = eachItem.name
                var genre = eachItem.genre.toString()
                genre = genre.replace("[", "")
                genre = genre.replace("]", "")
                holder.tvDirector.text = genre
                holder.tvReleaseDate.visibility = View.GONE

            } else if (moviedata == "moviesulike") {

                val eachItem = moviesLikeList.get(position)
                if (eachItem.image_url.isNotBlank()) {
                    Picasso.with(mcontext).load(eachItem.image_url)
                            .into(holder.movieImage, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    holder.loader.visibility = View.GONE
                                    holder.movieImage.visibility = View.VISIBLE
                                    holder.linearLayout2.visibility = View.VISIBLE

                                }

                                override fun onError() {
                                    holder.loader.visibility = View.GONE
                                }
                            })

                }
                holder.txtMovieName.text = eachItem.name
                holder.tvRating.text = eachItem.rating
                var genre = eachItem.genre.toString()
                genre = genre.replace("[", "")
                genre = genre.replace("]", "")
                holder.tvDirector.text = genre
                holder.tvReleaseDate.visibility = View.GONE
            }


            /*holder.rvMovieAdapterLayout.setOnClickListener{
                mcontext!!.toast("Working")
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return size
    }
}