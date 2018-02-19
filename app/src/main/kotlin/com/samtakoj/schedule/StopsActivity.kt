package com.samtakoj.schedule

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.samtakoj.schedule.model.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.qap.ctimelineview.TimelineRow
import org.qap.ctimelineview.TimelineViewAdapter

/**
 * Created by Александр on 11.04.2017.
 */

class StopsActivity : AppCompatActivity() {

    var isChange: Boolean = false
    lateinit  var stops: List<StopCsv>
    lateinit var route: RouteCsv
    lateinit var adapter: TimelineViewAdapter
    var color: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stopsTo = intent.extras.getSerializable("stopsTo") as List<StopCsv>
        stops = stopsTo
        val stopsFrom = intent.extras.getSerializable("stopsFrom") as List<StopCsv>
        val routeTo = intent.extras.getSerializable("routeTo") as RouteCsv
        route = routeTo
        val routeFrom = intent.extras.getSerializable("routeFrom") as RouteCsv
        val isSwitch = intent.extras.getBoolean("isChange")

        color = getColorByTransportType(route.transportType)

        verticalLayout {
            toolbar {
                backgroundColor = color
                textView {
                    id = R.id.route_name_text_view
                    textColor = Color.WHITE
                    text = route.name
                }
                if(isSwitch) {
                    imageView {
                        imageResource = R.drawable.change_route
                        onClick {
                            isChange = if (isChange) {
                                changeStopList(stopsTo, routeTo)
                                false
                            } else {
                                changeStopList(stopsFrom, routeFrom)
                                true
                            }
                        }
                    }.lparams { gravity = Gravity.RIGHT }
                }
            }
            listView {
                id = LIST_ID
                divider = null
            }
        }

        val listView = find<ListView>(LIST_ID)
        adapter = TimelineViewAdapter(this, 0, ArrayList(stops.map(this::convertToRow)), false)

        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        val timeBox = (application as TransportApplication).boxStore.boxFor(TimeCsv::class.java)

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.i("STOPS_SCHEDULE", "itemClick: position = $position, id = $id")

            val time = timeBox.query().equal(TimeCsv_.routeId, route.id).build().findFirst()

            val stop = stops[position]
//            Log.i("STOPS_SCHEDULE", "Stop: ${stop.name}")

            if(time != null)
                startActivity<TimeActivity>("time" to time, "stopPosition" to position, "stopName" to stop.name)

        }

    }

    private fun getColorByTransportType(transportType: String): Int {
        return when(transportType) {
            "bus" -> Color.argb(255, 11, 191, 214)
            "trol" -> Color.argb(255, 91, 232, 48)
            "metro" -> Color.argb(255, 39, 59, 122)
            "tram" -> Color.argb(255, 244, 75, 63)
            else -> -1
        }
    }

    private fun convertToRow(stop: StopCsv): TimelineRow {
        val row = TimelineRow(stop.id.toInt(), null, stop.name, "")
        row.bellowLineColor = color
        row.bellowLineSize = 5
        row.backgroundSize = 25
        row.backgroundColor = color
        return row
    }

    private fun changeStopList(newStops: List<StopCsv>, newRoute: RouteCsv) {
        stops = newStops
        route = newRoute
        val textView = find<TextView>(R.id.route_name_text_view)
        textView.text = route.name

        adapter.clear()
        adapter.addAll(ArrayList(stops.map(this::convertToRow)))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        val LIST_ID = View.generateViewId()
    }

    data class MyTestModel(val workDay: WorkDay, val timeTable: List<Long>)
}