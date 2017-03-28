package com.samtakoj.schedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.samtakoj.schedule.api.ScheduleFetcher
import android.location.Location
import android.location.LocationListener
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.samtakoj.schedule.api.LocListener
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val location = SmartLocation.with(this).location().lastLocation
        verticalLayout {
            padding = dip(20)
            textView {
                text = "Lat: ${location?.latitude}"
            }
            textView {
                text = "Lon: ${location?.longitude}"
            }
            textView {
                text = "Alt: ${location?.altitude}"
            }
            textView {
                text = "Speed: ${location?.speed}"
            }
            textView("Null") {
                id = 123
                textSize = 24f
            }
        }

        ScheduleFetcher.test(application as TransportApplication)

        SmartLocation.with(this).location().oneFix().start { location ->
            toast("Lat: ${location.latitude}, Long: ${location.longitude}")
        }
    }

    override fun onStop() {
        SmartLocation.with(this).location().stop()
        super.onStop()
    }

    override fun onDestroy() {
        ScheduleFetcher.subStop.unsubscribe()
        super.onDestroy()
    }
}
