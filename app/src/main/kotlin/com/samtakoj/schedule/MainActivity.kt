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
        val stopBox = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java)
        val routeList = routeBox.query().order(RouteCsv_.num).build().find()

        val listView = find<ListView>(123)
        val adapter = RouteListViewAdapter(this, routeList as ArrayList<RouteCsv>)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.i("TEST", "itemClick: position = $position, id = $id")

            val route = routeList[position]

            Log.i("TRANSPORT_SCHEDULE", "RouteName: ${route.name}")

            val stops = ArrayList<StopCsv>()
            route.stops.split(",").forEach { stopId ->
                stops.add(stopBox.query().equal(StopCsv_.id, stopId.toLong()).build().findFirst())
            }

            startActivity<StopsActivity>("stops" to stops, "routeId" to id)
        }

        SmartLocation.with(this).location().oneFix().start { location ->
            toast("Lat: ${location.latitude}, Long: ${location.longitude}")
        }
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }
}
