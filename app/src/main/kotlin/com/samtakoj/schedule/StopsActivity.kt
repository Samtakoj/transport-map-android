package com.samtakoj.schedule

import android.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import com.samtakoj.schedule.view.StopListViewAdapter
import com.samtakoj.shedule.model.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar

/**
 * Created by Александр on 11.04.2017.
 */

class StopsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stops = intent.extras.getSerializable("stops") as List<StopCsv>
        val routeId = intent.extras.getLong("routeId")

        val routeBox = (application as TransportApplication).boxStore.boxFor(RouteCsv::class.java)
        val route = routeBox.query().equal(RouteCsv_.id, routeId).build().findFirst()

        verticalLayout {
            toolbar {
                textView {
                    text = "${route.num}-${route.transportType}: ${route.name}"
                }
            }
            listView {
                id = 321
            }
        }

        val timeBox = (application as TransportApplication).boxStore.boxFor(TimeCsv::class.java)
        val time = timeBox.query().equal(TimeCsv_.routeId, routeId).build().findFirst()

        val listView = find<ListView>(321)
        val adapter = StopListViewAdapter(this, stops as ArrayList<StopCsv>)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.i("STOPS_SCHEDULE", "itemClick: position = $position, id = $id")

            val test = ArrayList<MyTestModel>()
            Log.i("STOPS_SCHEDULE", "Stop: ${time.timeTable.count()}")

            var skipCount = 0
            time.workDay.forEach {
                test.add(MyTestModel(it, time.timeTable.subList(skipCount + position * time.intervalCount, position * time.intervalCount + it.countInterval)))
                skipCount += it.countInterval
            }

            val stop = stops[position]
            Log.i("STOPS_SCHEDULE", "Stop: ${stop.name}")

            test.forEach {
                var timeString = ""
                it.timeTable.forEach {
                    timeString += getTimeString(it) + "; "
                }
                Log.i("STOPS_SCHEDULE", "Stop TIME: ${it.workDay} ---- $timeString")
            }

        }

    }

    fun getTimeString(time: Long): String {
        val minute = time % 60
        return "${time / 60}:${if(minute < 10) '0' + minute.toString() else minute}"
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    class MyTestModel(val workDay: WorkDay, val timeTable: List<Long>)
}