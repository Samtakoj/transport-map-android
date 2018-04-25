package com.samtakoj.schedule

/**
 * Created by alex on 4/16/18.
 */
import android.os.Bundle
import android.support.v4.app.FragmentActivity

import com.beyondar.android.plugin.GoogleMapWorldPlugin
import com.beyondar.android.world.GeoObject
import com.beyondar.android.world.World
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.Marker
import com.samtakoj.schedule.model.StopCsv
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import io.nlopez.smartlocation.location.providers.MultiFallbackProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.samtakoj.schedule.api.ScheduleHelper
import com.samtakoj.schedule.extensions.transportApp


class GoogleMapActivity : FragmentActivity(), OnMarkerClickListener, OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var mGoogleMapPlugin: GoogleMapWorldPlugin? = null
    private var mWorld: World? = null

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        mWorld = World(this@GoogleMapActivity)

        val playServicesProvider = LocationGooglePlayServicesProvider()
        playServicesProvider.setCheckLocationSettings(true)
        playServicesProvider.setLocationSettingsAlwaysShow(true)
        val provider = MultiFallbackProvider.Builder().
                withProvider(playServicesProvider).
                withProvider(LocationManagerProvider()).
                build()

        var latLng: LatLng
        SmartLocation.with(this@GoogleMapActivity).location(provider).config(LocationParams.BEST_EFFORT)
                .start { location ->
                    mWorld!!.setLocation(location)
                }
        val location = SmartLocation.with(this).location().lastLocation
        if (location?.latitude != null) mWorld!!.setLocation(location)
        latLng = LatLng(location!!.latitude, location.longitude)

        mGoogleMapPlugin = GoogleMapWorldPlugin(this@GoogleMapActivity, mMap)

        mWorld!!.addPlugin(mGoogleMapPlugin)

        mMap!!.setOnMarkerClickListener(this@GoogleMapActivity)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)

        val user = GeoObject(1000L)
        user.setGeoPosition(mWorld!!.latitude, mWorld!!.longitude)
        user.setImageResource(R.drawable.user_marker)
        user.name = "Your position"
        mWorld!!.addBeyondarObject(user)

        val stops = transportApp().boxStore.boxFor(StopCsv::class.java).query().filter{
            ScheduleHelper.calculateDistanceMeters(location!!.longitude, location.latitude, it.lng * 0.00001, it.ltd * 0.00001) <= 500
        }.build().find()

        stops.forEach { stop ->
            val obj = GeoObject(stop.id)
            obj.setGeoPosition(stop.ltd * 0.00001, stop.lng * 0.00001)
            obj.setImageResource(R.drawable.stop_icon)
            obj.name = stop.name
            mWorld!!.addBeyondarObject(obj)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val fragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val geoObject = mGoogleMapPlugin!!.getGeoObjectOwner(marker)
        return false
    }
}