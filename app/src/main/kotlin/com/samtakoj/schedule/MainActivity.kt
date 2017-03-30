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
import com.samtakoj.schedule.model.StopCsv
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.*
import retrofit2.HttpException
import rx.Subscriber
import rx.Subscription

class MainActivity : AppCompatActivity() {

    lateinit var subscription: Subscription

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

        subscription = ScheduleFetcher.stops(application as TransportApplication, object: Subscriber<StopCsv>() {
            override fun onCompleted() {
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onNext(stop: StopCsv) {
                Log.i("TRANSPORT_SCHEDULE", "Stop: ${stop.name}")
            }
        })

        SmartLocation.with(this).location().oneFix().start { location ->
            toast("Lat: ${location.latitude}, Long: ${location.longitude}")
        }
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        subscription.unsubscribe()
        super.onDestroy()
    }
}
