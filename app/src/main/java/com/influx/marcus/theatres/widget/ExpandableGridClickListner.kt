package com.influx.marcus.theatres.widget


interface ExpandableGridClickListner {

    fun onParentClick(value: String)

    fun onPreviewClick()

    fun onBuyTicketClick()
}