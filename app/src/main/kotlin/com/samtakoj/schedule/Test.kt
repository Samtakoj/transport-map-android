package com.samtakoj.schedule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import android.widget.TextView
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import com.google.android.gms.location.DetectedActivity
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams

/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity() {

    val TV_ID = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            textView("Hello world!") {
                id = TV_ID
                textSize = 24f
            }
            textView("Null") {
                id = TV_ID + 1
                textSize = 24f
            }
            button("Click") {
                onClick {
                    startActivity<MainActivity>()
                    finish()
                }
            }
        }

        val textView = find<TextView>(TV_ID)
        val textView1 = find<TextView>(TV_ID + 1)
        val provider = LocationGooglePlayServicesProvider()
        provider.setCheckLocationSettings(true)
        provider.setLocationSettingsAlwaysShow(true)

        SmartLocation.with(this).
                location(provider).
                config(LocationParams.Builder().setAccuracy(LocationAccuracy.HIGH).setDistance(0.0F).setInterval(300L).build()). //TODO: need to move these to the SharedPreferences
                start { location ->
            textView1.text = "Lat: ${location?.latitude}, Lng: ${location?.longitude}"
        }

        SmartLocation.with(this).activity().start { activity ->
            textView.text = "Activity: ${getNameFromType(activity)}, confidence: ${activity?.confidence}"
        }
    }

    override fun onStop() {
        SmartLocation.with(this).location().stop()
        SmartLocation.with(this).activity().stop()
        super.onStop()
    }

    private fun getNameFromType(activityType: DetectedActivity): String {
        when (activityType.type) {
            DetectedActivity.IN_VEHICLE -> return "in vehicle"
            DetectedActivity.ON_BICYCLE -> return "on bicycle"
            DetectedActivity.ON_FOOT -> return "on foot"
            DetectedActivity.STILL -> return "still"
            DetectedActivity.TILTING -> return "tilting"
            else -> return "unknown"
        }
    }
}