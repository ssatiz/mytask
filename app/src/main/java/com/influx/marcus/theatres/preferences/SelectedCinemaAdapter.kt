package com.influx.marcus.theatres.preferences

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R


/**
 * Created by influx on 02-04-2018.
 */
class SelectedCinemaAdapter(cinemaList: ArrayList<String>,context: Context?,selectedCinemaListener: SelectedCinemaListener) : RecyclerView.Adapter<SelectedCinemaAdapter.MyViewHolder>() {
     var Item = cinemaList
    var mcontext = context
    var selectedCinemaListener = selectedCinemaListener
    var isSelect = true

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCinema: TextView

        init {
            tvCinema = view.findViewById(R.id.tvSelectedCinema)
        }
    }

    interface SelectedCinemaListener {
        fun select(v: View, isSelect: Boolean, cardInfo: String)
        fun unselect()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_selected_cinema, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            holder.tvCinema.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    isSelect = false
                    selectedCinemaListener.select(v!!, isSelect,Item.get(position))

                }
            })
            holder.tvCinema.setText(Item.get(position))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return Item.size
    }
}
