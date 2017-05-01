package com.samtakoj.schedule

import android.graphics.Color
import android.os.Bundle
import org.jetbrains.anko.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import com.samtakoj.schedule.view.RouteListViewAdapter
import com.samtakoj.shedule.model.*
import org.jetbrains.anko.appcompat.v7.toolbar

class RouteActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routes = intent.extras.getSerializable("routes") as List<RouteCsv>
        val stopName = intent.extras.getString("stopName")

        verticalLayout {
            toolbar {
                backgroundColor = Color.argb(255, 11, 191, 214)
                textView {
                    id = R.id.route_name_text_view
                    textColor = Color.WHITE
                    text = "Routes for $stopName"
                }
            }
            listView {
                id = 321
            }
        }

        val listView = find<ListView>(321)
        val adapter = RouteListViewAdapter(this, routes as ArrayList<RouteCsv>)

        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.i("TEST", "itemClick: position = $position, id = $id")

            val routeTo = routes[position]
            val routeFrom = RouteCsv()
//                    routeBox.query()
//                    .equal(RouteCsv_.num, routeTo.num)
//                    .and()
//                    .equal(RouteCsv_.transportType, routeTo.transportType)
//                    .and()
//                    .notEqual(RouteCsv_.id, routeTo.id).build().findFirst()

            Log.i("TRANSPORT_SCHEDULE", "RouteName: ${routeTo.name}")

            val stopsTo = getRouteStops(routeTo)
            val stopsFrom = emptyList<StopCsv>()

            startActivity<StopsActivity>("stopsTo" to stopsTo, "routeTo" to routeTo, "routeFrom" to routeFrom, "stopsFrom" to stopsFrom, "isChange" to false)
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

    private fun getColorByTransportType(transportType: String): Int {
        when(transportType) {
            "bus" -> return Color.argb(255, 11, 191, 214)
            "trol" -> return Color.argb(255, 91, 232, 48)
            "metro" -> return Color.argb(255, 39, 59, 122)
            "tram" -> return Color.argb(255, 244, 75, 63)
            else -> return -1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}