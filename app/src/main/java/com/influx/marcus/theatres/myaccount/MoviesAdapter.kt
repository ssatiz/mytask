package com.influx.marcus.theatres.myaccount

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.MovieYoulikeItem
import com.squareup.picasso.Picasso


/**
 * Created by influx on 02-04-2018.
 */
class MoviesAdapter(dataList: List<MovieYoulikeItem>, context: Context?) : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {
    lateinit var moviesLikeList: List<MovieYoulikeItem>
    var moviedata: String = ""
    var size = 0

    var mcontext = context

    init {
        if (dataList.size > 0) {
                moviesLikeList = dataList as List<MovieYoulikeItem>
                moviedata = "moviesulike"
                size = moviesLikeList.size
            }


        }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var movieImage: ImageView
        lateinit var txtMovieName: TextView
        lateinit var genre: TextView
        lateinit var tvDirector: TextView
        lateinit var tvReleaseDate: TextView
        lateinit var tvRating : TextView

        init {
            movieImage = view.findViewById(R.id.ivCinemaLoc)
            txtMovieName = view.findViewById(R.id.tvMovieName)
            tvDirector = view.findViewById(R.id.tvDirector)
            tvReleaseDate = view.findViewById(R.id.tvReleaseDate)
            tvRating = view.findViewById(R.id.tvRating)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            if (moviedata == "moviesulike") {

                val eachItem = moviesLikeList.get(position)
                if (eachItem.image_url.isNotBlank()) {
                    Picasso.with(mcontext)
                            .load(eachItem.image_url)
                            .fit()
                            .placeholder(R.drawable.marcus_placeholder)
                            .into(holder.movieImage)
                }
                holder.txtMovieName.text = eachItem.name
                holder.tvRating.text = eachItem.rating
                var genre = eachItem.genre.toString()
                genre = genre.replace("[", "")
                genre = genre.replace("]", "")
                holder.tvDirector.text = genre
                holder.tvReleaseDate.visibility = View.GONE

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return size
    }
}