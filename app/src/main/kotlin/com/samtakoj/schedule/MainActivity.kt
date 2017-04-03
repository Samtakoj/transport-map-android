package com.samtakoj.schedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.samtakoj.schedule.api.ScheduleFetcher
import android.location.Location
import android.location.LocationListener
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.common.collect.Lists
import com.samtakoj.schedule.api.LocListener
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
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

//        val stops = Lists.newArrayList<StopCsv>()
        val subscriber = object: Subscriber<StopCsv>() {
            override fun onCompleted() {
                Log.i("TRANSPORT_SCHEDULE", "Stops is completed!")
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onNext(stop: StopCsv) {
//                stops.add(stop)
                Log.i("TRANSPORT_SCHEDULE", "Stop: ${stop.id}; ${stop.name}; ${stop.lng * 0.00001}; ${stop.ltd * 0.00001}")
            }
        }
//        location?.latitude = 53.929018
//        location?.longitude = 27.585731  2758469;5392808
        subscription = ScheduleFetcher.stops(application as TransportApplication, subscriber, 53.927010, 27.576126) //53.929018, 27.585731
//        subscription = ScheduleFetcher.routes(application as TransportApplication, object: Subscriber<RouteCsv>() {
//            override fun onCompleted() {
//                Log.i("TRANSPORT_SCHEDULE", "Routes is completed!")
//            }
//
//            override fun onError(e: Throwable) {
//                e.printStackTrace()
//            }
//
//            override fun onNext(route: RouteCsv) {
//                Log.i("TRANSPORT_SCHEDULE", "Route: ${route.id}; ${route.name}; ${route.num}; ${route.weekDays}; ${route.transportType}; ${route.stops}")
//            }
//        })

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
