package com.samtakoj.schedule

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


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val location = SmartLocation.with(this).location().lastLocation
        val verticalLayout = verticalLayout {
            padding = dip(20)
            listView {
                id = 123
            }
        }

        val arrString = ArrayList<String>()
        val listView = find<ListView>(123)
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                arrString)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        val start = System.currentTimeMillis()
        Log.i("TRANSPORT_SCHEDULE", "Time now")
        ScheduleFetcher.getList(application as TransportApplication).subscribe(object: Subscriber<TestModel>() {
            override fun onCompleted() {
                Log.i("TRANSPORT_SCHEDULE", "Download time = ${System.currentTimeMillis() - start}")
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onNext(test: TestModel) {
                adapter.add("${test.route.num}-${test.route.transportType}: ${test.route.name}")
            }
        })
//        val routes = ScheduleFetcher.routes(application as TransportApplication)
//        routes.subscribe(object: Subscriber<RouteCsv>() {
//            override fun onCompleted() {
//                Log.i("TRANSPORT_SCHEDULE", "Routes is completed!")
//            }
//
//            override fun onError(e: Throwable) {
//                e.printStackTrace()
//            }
//
//            override fun onNext(route: RouteCsv) {
//                adapter.add("${route.num}-${route.transportType}: ${route.name}")
//            }
//        })

        //53.929018, 27.585731
//        val testModels = routes.flatMap { route ->
//            ScheduleFetcher.getStops(application as TransportApplication, route)
//        }
//        testModels.subscribe()

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.i("TEST", "itemClick: position = $position, id = $id")
//            testModels.elementAt(id.toInt()).subscribe { test ->
//                Log.i("TEST", "Route: ${test.route.name}")
//                Log.i("TEST", "Stops: ${test.stops}")
//            }
//            Log.i("TRANSPORT_SCHEDULE", "${arrTestModel[id.toInt()]}")
//            Log.i("TRANSPORT_SCHEDULE", "Stops: ${arrTestModel[id.toInt() + 1].stops}")

//            startActivity<StopsActivity>("test" to arrTestModel[id.toInt() + 1])

//            routes.take(id.toInt() + 1).last().subscribe { route ->
////                Log.i("TEST", "Route: ${route.name}")
//                val stops = ScheduleFetcher.getStops(application as TransportApplication, route.stops)
//                stops.forEach {
//                    Log.i("TEST", "Stops: $it")
//                }
//
//                ScheduleFetcher.times(application as TransportApplication, route.id).subscribe{ time ->
////                    var tempStr = ""
////                    for(i in 0..time.timeTable.count() - 1) {
////                        tempStr += " " + time.timeTable[i].toString()
////                    }
//                    Log.i("TEST", "Time: ${time.workDay}; ${time.intervalCount}; ${time.timeTable}")
//                }
//            }
        }

        listView.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                Log.d("TEST", "itemSelect: position = $position, id = $id")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("TEST", "itemSelect: nothing")
            }
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
