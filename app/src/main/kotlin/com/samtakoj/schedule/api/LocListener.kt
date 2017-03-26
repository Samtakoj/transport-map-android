package com.samtakoj.schedule.api

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

/**
 * Created by Александр on 26.03.2017.
 */
class LocListener: LocationListener {
    private var lat = 0.0
    private var lon = 0.0
    private var alt = 0.0
    private var speed = 0.0

    fun getLat(): Double {
        return lat
    }

    fun getLon(): Double {
        return lon
    }

    fun getAlt(): Double {
        return alt
    }

    fun getSpeed(): Double {
        return speed
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            lat = location.latitude
            lon = location.longitude
            alt = location.altitude
            speed = location.speed.toDouble()
        }
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }
}