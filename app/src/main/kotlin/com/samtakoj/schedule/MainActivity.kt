package com.samtakoj.schedule

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.samtakoj.schedule.api.ScheduleFetcher
import android.util.Log
import android.view.View
import android.widget.ListView
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.*
import rx.Subscriber
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.AdapterView.OnItemClickListener
import com.samtakoj.schedule.model.TestModel
import com.samtakoj.schedule.view.RouteListViewAdapter
import com.samtakoj.shedule.model.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val location = SmartLocation.with(this).location().lastLocation
        val verticalLayout = verticalLayout {
            listView {
                id = 123
            }
        }

        val routeBox = (application as TransportApplication).boxStore.boxFor(RouteCsv::class.java)
        val routeList = routeBox.query().order(RouteCsv_.num).build().find().groupBy { route ->
            "${route.num}-${route.transportType}"
        }.flatMap { grouped ->
            grouped.value.take(1)
        }

        val listView = find<ListView>(123)
        val adapter = RouteListViewAdapter(this, routeList as ArrayList<RouteCsv>)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.i("TEST", "itemClick: position = $position, id = $id")

            val routeTo = routeList[position]
            val routeFrom = routeBox.query()
                    .equal(RouteCsv_.num, routeTo.num)
                    .and()
                    .equal(RouteCsv_.transportType, routeTo.transportType)
                    .and()
                    .notEqual(RouteCsv_.id, routeTo.id).build().findFirst()

            Log.i("TRANSPORT_SCHEDULE", "RouteName: ${routeTo.name}")

            val stopsTo = getRouteStops(routeTo)
            val stopsFrom = getRouteStops(routeFrom)

            startActivity<StopsActivity>("stopsTo" to stopsTo, "routeTo" to routeTo, "routeFrom" to routeFrom, "stopsFrom" to stopsFrom)
        }

        SmartLocation.with(this).location().oneFix().start { location ->
            toast("Lat: ${location.latitude}, Long: ${location.longitude}")
        }
    }

    private fun getRouteStops(route: RouteCsv): List<StopCsv> {
        val stopBox = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java)
        val stops = ArrayList<StopCsv>()
        route.stops.split(",").forEach { stopId ->
            stops.add(stopBox.query().equal(StopCsv_.id, stopId.toLong()).build().findFirst())
        }

        return stops
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }
}
