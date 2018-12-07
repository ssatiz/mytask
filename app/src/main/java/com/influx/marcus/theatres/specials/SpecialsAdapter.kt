package com.influx.marcus.theatres.specials

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.specials.Special
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView
import org.jetbrains.anko.toast


/**
 * Created by influx on 02-04-2018.
 */
class SpecialsAdapter(dataList:  List<Special>, context: Context?,SpecialsAdapterListener: SpecialsAdapterListener) : RecyclerView.Adapter<SpecialsAdapter.MyViewHolder>() {
    var Item = dataList
    var mcontext = context
    var specialsAdapterListener = SpecialsAdapterListener


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var splimg: ImageView
        var avLoadingIndicatorView: AVLoadingIndicatorView
        init {
            splimg = view.findViewById(R.id.ivSplImg)
            avLoadingIndicatorView= view.findViewById(R.id.loader)

        }
    }
    interface SpecialsAdapterListener {
        fun getDetails(position: Int,title: String,image : String,url :String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.specials_adapter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {

            holder.splimg.setOnClickListener {
                specialsAdapterListener.getDetails(position,Item.get(position).title,
                        Item.get(position).promotion_image,Item.get(position).webView)
            }


            if (Item.get(position).promotion_image.isNotEmpty()) {
                Picasso.with(mcontext).load(Item.get(position).promotion_image)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(holder.splimg, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                holder.avLoadingIndicatorView.visibility = View.GONE
                                holder.splimg.visibility = View.VISIBLE
                            }

                            override fun onError() {
                                holder.avLoadingIndicatorView.visibility = View.GONE
                            }
                        })
            }else{

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return Item.size
    }
}
