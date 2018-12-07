package com.influx.marcus.theatres.cinegreetings

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R

class GreetingsAdapter(var context: Context) : RecyclerView.Adapter<GreetingsAdapter.GreetVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GreetVH {
        val row_view = LayoutInflater.from(context).inflate(R.layout.row_greeting, parent, false)
        return GreetVH(row_view)
    }

    override fun onBindViewHolder(holder: GreetVH, position: Int) {
        holder!!.ivBanner.setImageResource(eventImg[position])
        holder.ivBanner.setOnClickListener { openCustomisationActivity(eventName[position], eventImg[position]) }
        holder.tvEvent.text = eventName[position]
    }

    var greetCount: Int = 3
    val eventName = arrayOf("Anniversary", "Birthday", "Valentine's")
    val eventImg = arrayOf(R.drawable.bday, R.drawable.bday, R.drawable.bday)



    override fun getItemCount(): Int {
        return greetCount
    }


    private fun openCustomisationActivity(eventName: String, eventimage: Int) {
        val mainIntent = Intent(context, GreetingCustomActivitynew::class.java)
        mainIntent.putExtra("event_name", eventName)
        mainIntent.putExtra("event_image", eventimage)
        context.startActivity(mainIntent)
    }

    class GreetVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner = itemView.findViewById<ImageView>(R.id.ivGreetingBanner)
        val tvEvent = itemView.findViewById<TextView>(R.id.tvGreetname)
    }
}