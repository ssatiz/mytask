package com.influx.marcus.theatres.preferences

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.statelist.StateListResp
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.OnItemClickListener
import org.jetbrains.anko.textColor

class StateAdapter(val stateList: StateListResp,val context: Context,
                   val clickListener: OnItemClickListener):
        RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val rowlayout = LayoutInflater.from(context).inflate(R.layout.row_statename,
                 parent,false)
        return ViewHolder(rowlayout)
    }

    override fun getItemCount(): Int {
     return stateList.DATA.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statedata = stateList.DATA.get(position)
        holder.tvStatename.text=statedata.state
        holder.tvStatename.setOnClickListener {
            clickListener.onItemClick(position)
        }
        if (statedata.scode.equals(AppConstants.state)){
            holder.tvStatename.textColor=context.resources.getColor(R.color.marcus_red)
        }
    }
}

class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {
        val tvStatename = itemView!!.findViewById<TextView>(R.id.tvStatename)
}