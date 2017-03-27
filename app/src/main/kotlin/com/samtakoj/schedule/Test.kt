package com.samtakoj.schedule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.pm.PackageManager
import android.util.Log


/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity(), LocationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationPermissions.requestBasic(this)

        verticalLayout {
            textView("Hello world!") {
                textSize = 24f
            }
            button("Click") {
                onClick {
                    toast("Hi!")
                    (getSystemService(Context.LOCATION_SERVICE) as LocationManager).removeUpdates(this@TestActivity)
                    startActivity<MainActivity>()
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ApplicationPermissions.INITIAL_REQUEST -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this)

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.i("TRANSPOTR_SCHED", "Lat: ${location?.latitude}, Lng: ${location?.longitude}")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}