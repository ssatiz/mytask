package com.influx.marcus.theatres.payment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.MovieYoulikeItem


/**
 * Created by influx on 02-04-2018.
 */
class PaymentsAdapter(dataList: List<MovieYoulikeItem>, context: Context?) : RecyclerView.Adapter<PaymentsAdapter.MyViewHolder>() {
    lateinit var moviesLikeList: List<MovieYoulikeItem>
    var moviedata: String = ""
    var size = 0
    var mcontext = context

    /* init {
         if (dataList.size > 0) {
                 moviesLikeList = dataList as List<MovieYoulikeItem>
                 moviedata = "moviesulike"
                 size = moviesLikeList.size
             }


         }*/


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // var movieImage: ImageView
        lateinit var txtMovieName: TextView
        lateinit var genre: TextView
        lateinit var tvDirector: TextView
        lateinit var tvReleaseDate: TextView
        lateinit var tvRating: TextView
        lateinit var ivDropDown: ImageView
        lateinit var cardDetails: LinearLayout


        init {
            /*  movieImage = view.findViewById(R.id.ivMve)
              txtMovieName = view.findViewById(R.id.tvMovieName)
              tvDirector = view.findViewById(R.id.tvDirector)
              tvReleaseDate = view.findViewById(R.id.tvReleaseDate)
              tvRating = view.findViewById(R.id.tvRating)*/
            ivDropDown = view.findViewById(R.id.ivDropDown)
            cardDetails = view.findViewById(R.id.cardDetails)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.payment_adapter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {


            /*if (moviedata == "moviesulike") {

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
                holder.tvReleaseDate.visibility = View.GONE}*/

            holder.ivDropDown.setOnClickListener {
                holder.cardDetails.visibility = View.VISIBLE
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}