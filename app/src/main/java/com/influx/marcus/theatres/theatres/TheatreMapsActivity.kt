package com.influx.marcus.theatres.theatres

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.theatre.TheatreListResp
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import kotlinx.android.synthetic.main.activity_theatre_maps.*


class TheatreMapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    lateinit var theatreList: TheatreListResp
    private var marker: Marker? = null
    private lateinit var ivBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theatre_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initViews()


        viewpagerTheatre?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                try {
                    mMap.clear()
                    val lat = theatreList.DATA.get(position).latitude
                    val long = theatreList.DATA.get(position).longitude
                    val latLng = LatLng(lat.toDouble(), long.toDouble())
                    /* marker = mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f))*/
                    for (eachitem in theatreList.DATA) {
                        if (lat.equals(eachitem.latitude)) {
                            LogUtils.d("OURTHEATHRE", " lat1 $lat and lat2 ${eachitem.latitude}${eachitem.name} ")
                            val latLng = LatLng(lat.toDouble(), long.toDouble())
                            marker = mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f))
                        } else {
                            // LogUtils.d("OURTHEATHRE", " lat1 $lat and lat2 ${eachitem.latitude} else case ")
                            val latLong = LatLng(eachitem.latitude.toDouble(), eachitem.longitude.toDouble())
                            marker = mMap.addMarker(MarkerOptions().position(latLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapblack)))
                        }
                    }
                    //  currentListSizeLimit=5
                    // updateShowTimesForDate(dateInfo[position].bdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun initViews() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        theatreList = AppConstants.getObject(AppConstants.KEY_THEATRELIST,
                this@TheatreMapsActivity, TheatreListResp::class.java as Class<Any>) as TheatreListResp
        var theatreAdapter = PreferredLocationsPagerAdapter(theatreList.DATA, this@TheatreMapsActivity)
        viewpagerTheatre.offscreenPageLimit = theatreAdapter.count
        viewpagerTheatre.adapter = theatreAdapter
        viewpagerTheatre.clipToPadding = false
        var dimension = resources.getDimension(R.dimen._70sdp)
        viewpagerTheatre.setPadding(dimension.toInt(), 0, dimension.toInt(), 0)
        viewpagerTheatre.setPageMargin(resources.getDimension(R.dimen._15sdp).toInt())
        ivBack = findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            onBackPressed()
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (theatreList.DATA.size > 1) {
            for (i in 1 until theatreList.DATA.size) {
                val latLng = LatLng(theatreList.DATA.get(i).latitude.toDouble(), theatreList.DATA.get(i).longitude.toDouble())
                marker = mMap.addMarker(MarkerOptions().position(latLng).title(theatreList.DATA.get(i).name).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapblack)))

            }
        }
        val firstLatlng = LatLng(theatreList.DATA.first().latitude.toDouble(), theatreList.DATA.first().longitude.toDouble())
        marker = mMap.addMarker(MarkerOptions().position(firstLatlng).title(theatreList.DATA.first().name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatlng, 10.0f))
        mMap.setOnMarkerClickListener(OnMarkerClickListener { markerr ->

            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker!!.position, 10.0f))
            val locAddress = markerr.title
            val position = marker!!.position

            // Toast.makeText(applicationContext,"LocationAddre="+locAddress+"=locmarkerpos="+position,Toast.LENGTH_SHORT).show()
            if (marker != null) {
                marker!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mapblack))
            }
            markerr.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            marker = markerr
            for (i in 0 until theatreList.DATA.size) {
                if (theatreList.DATA.get(i).latitude.toDouble() == (markerr.position.latitude)) {
                    viewpagerTheatre.setCurrentItem(i)
                    LogUtils.i("Theatres", "currentitem pos $i position latitude${position.latitude}" +
                            "eachitem latitude ${theatreList.DATA.get(i).name}")
                }
            }


            true
        })

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }

}

