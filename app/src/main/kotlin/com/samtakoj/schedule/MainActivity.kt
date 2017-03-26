package com.samtakoj.schedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.samtakoj.schedule.api.ScheduleFetcher
import android.location.Location
import android.location.LocationListener
import com.samtakoj.schedule.api.LocListener
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ScheduleFetcher.test(application as TransportApplication)
        verticalLayout {
            padding = dip(20)
            textView {
                text = "Lat: "// + location.getLat().toString()
            }
            textView {
                text = "Lon: "// + location.getLon().toString()
            }
            textView {
                text = "Alt: "// + location.getAlt().toString()
            }
            textView {
                text = "Speed: "// + location.getSpeed().toString()
            }
        }

    }

}
