package com.samtakoj.schedule

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import android.widget.TextView
import io.nlopez.smartlocation.SmartLocation
import com.google.android.gms.location.DetectedActivity
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import io.nlopez.smartlocation.location.providers.MultiFallbackProvider

/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity() {

    val TV_ID = 1234
    lateinit var textView1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationPermissions.requestBasic(this)
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
        textView1 = find<TextView>(TV_ID + 1)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ApplicationPermissions.INITIAL_REQUEST -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val provider = MultiFallbackProvider.Builder().
                            withGooglePlayServicesProvider().
                            withProvider(LocationManagerProvider()).
                            build()


                    SmartLocation.with(this).
                            location(provider).
                            start { location ->
                                textView1.text = "Lat: ${location?.latitude}, Lng: ${location?.longitude}"
                            }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }
}